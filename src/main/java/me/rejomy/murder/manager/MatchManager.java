package me.rejomy.murder.manager;

import lombok.Getter;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Arena;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.data.PlayerData;
import me.rejomy.murder.data.PlayerRole;
import me.rejomy.murder.task.game.GameTask;
import me.rejomy.murder.task.game.WaitingTask;
import me.rejomy.murder.util.*;
import me.rejomy.murder.util.item.ItemObject;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class MatchManager {

    private final List<Match> matches = new ArrayList<>();

    public MatchManager() {
        createMatches();
    }

    public void add(Player player, Match match) {
        Arena arena = match.getArena();
        
        if (arena.maxPlayers == match.getPlayers().size()) {
            MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getArenaIsFull()
                    .replace("$arena", arena.name));
            return;
        }

        if (arena.status == Arena.Status.STARTING || arena.status == Arena.Status.WAITING) {
            int playersSize = match.getPlayers().size();

            if(arena.minPlayers > playersSize && arena.minPlayers == playersSize + 1) {
                if (!match.schedule) {
                    match.schedule = true;

                    WaitingTask waitingTask = new WaitingTask(match);
                    waitingTask.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(MurderAPI.INSTANCE.getPlugin(),
                            waitingTask::run, 20, 20));
                }
            }

            // Reset player fall distance when teleport to arena.
            // Because if player is falling when we teleport him, he take a damage.
            player.setFallDistance(0);

            player.teleport(arena.lobbyPosition);

            // Reset player parameters
            PlayerUtil.clearEffects(player);
            PlayerUtil.clearInventory(player);
            player.setFoodLevel(20);
            player.setHealth(player.getMaxHealth());

            // Change player game mode if its selected in config.
            GameMode gameMode = MurderAPI.INSTANCE.getFileManager().getConfig().getGameMode();

            if (gameMode != null) {
                player.setGameMode(gameMode);
            }

            PlayerData data = MurderAPI.INSTANCE.getDataManager().get(player);

            data.role = PlayerRole.INNOCENT;
            data.currentMatch = match;
            // Reset player spectator state.
            data.spectator = false;

            match.getPlayers().put(player, data);

            // Reset chances
            MurderUtil.recalculatePlayerChances(match);

            for(Map.Entry<Integer, ItemObject> items : MurderAPI.INSTANCE.getFileManager().getItemsFile().WAITING_ITEMS.entrySet()) {
                player.getInventory().setItem(items.getKey(), items.getValue().item);
            }

            for(Player target : match.getPlayers().keySet()) {
                MessageUtil.sendMessage(target, MurderAPI.INSTANCE.getFileManager().getMessage().getJoin(), 
                        "arena", arena.name, "player", player.getName(), "max", arena.maxPlayers, "current", match.getPlayers().size());
            }
        } else {
            MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getAlreadyStart()
                    .replace("$arena", arena.name));
        }
    }

    public void start(Match match) {
        match.getArena().status = Arena.Status.PLAYING;

        // Randomize spawn positions for every game, because else we will spawn player only in order.
        List<Location> spawnPositions = new ArrayList<>(match.getArena().spawnPositions);
        Collections.shuffle(spawnPositions);

        // Teleport players to spawn positions.
        int nextPosition = 0;

        for(Player player : match.getPlayers().keySet()) {
            if(nextPosition + 1 == spawnPositions.size()) {
                nextPosition = 0;
            }

            player.teleport(match.getArena().spawnPositions.get(nextPosition));

            nextPosition++;
        }

        MurderUtil.setDetectiveAndMurders(match);

        // Отправляем сообщение в начале матче о том, какая у кого роль.
        for(Map.Entry<Player, PlayerData> map : match.getAlivePlayers().entrySet()) {
            Player player = map.getKey();
            PlayerData data = map.getValue();

            // Murderers
            if (data.role == PlayerRole.MURDER) {

                data.receiveCompassDelay = MurderAPI.INSTANCE.getFileManager().getConfig().getGameGiveCompassAfter().getDiapasonValue();
                data.receiveSwordDelay = MurderAPI.INSTANCE.getFileManager().getConfig().getGameGiveSwordAfter().getDiapasonValue();

                data.receiveRoleDelay = MurderAPI.INSTANCE.getFileManager().getConfig().getRoleMurder().getDiapasonValue();
            }
            // Detectives
            else if (data.role == PlayerRole.DETECTIVE) {

                data.receiveBowDelay = MurderAPI.INSTANCE.getFileManager().getConfig().getGameGiveBowAfter().getDiapasonValue();
                data.receiveRoleDelay = MurderAPI.INSTANCE.getFileManager().getConfig().getRoleDetective().getDiapasonValue();
            }
            // Innocents
            else {
                data.receiveRoleDelay = MurderAPI.INSTANCE.getFileManager().getConfig().getRoleInnocent().getDiapasonValue();
            }

            // Reset parameters to player
            PlayerUtil.clearEffects(player);
            PlayerUtil.clearInventory(player);

            MessageUtil.sendMessage(map.getKey(),
                    MurderAPI.INSTANCE.getFileManager().getMessage().getStartDefaultMessage(),
                    "detectives", match.getDetectives().size(),
                    "murderers", match.getMurderers().size());
        }

        // Запускаем game task для игры.
        GameTask gameTask = new GameTask(match);
        gameTask.setTaskId(Bukkit.getScheduler().scheduleAsyncRepeatingTask(MurderAPI.INSTANCE.getPlugin(), gameTask, 0, 20));
    }

    public Match get(String arena) {
        return getMatches().stream().filter(match -> match.getArena().name.equalsIgnoreCase(arena)).findAny().orElse(null);
    }

    public Match get(Player player) {
        return getMatches().stream().filter(match -> match.getPlayers().containsKey(player)).findAny().orElse(null);
    }

    public void end(Match match) {
        Arena arena = match.getArena();

        arena.status = Arena.Status.ENDING;

        Bukkit.getScheduler().runTask(MurderAPI.INSTANCE.getPlugin(), () -> {
            // Удаляем всё неподобранное золото.
            for (Item item : match.getDroppedGold()) {
                if (item != null) {
                    item.remove();
                }
            }

            for (Map.Entry<Player, PlayerData> entry : match.getPlayers().entrySet()) {
                Player player = entry.getKey();
                PlayerData data = entry.getValue();

                PlayerUtil.clearEffects(player);

                // If player does not spectator, we should give him end match items.
                if (!data.spectator) {
                    PlayerUtil.clearInventory(player);

                    ItemUtil.giveItemsFromConfig(player, MurderAPI.INSTANCE.getFileManager().getItemsFile().ENDING_ITEMS);
                }

                match.removeSpectator(player);
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(MurderAPI.INSTANCE.getPlugin(), () -> {
                // Remove at first, for dont use match.remove in teleport listener (prevent world change)
                matches.remove(match);

                for (Map.Entry<Player, PlayerData> entry : match.getPlayers().entrySet()) {
                    Player player = entry.getKey();
                    PlayerData data = entry.getValue();

                    MurderUtil.saveStats(data);

                    // Reset variables related to the match.
                    data.currentMatch = null;
                    data.wasKilled = false;
                    data.infinityArrow = false;

                    TeleportUtil.teleportToLobby(player);
                }

                arena.status = Arena.Status.WAITING;

                matches.add(new Match(match.getArena()));
            }, MurderAPI.INSTANCE.getFileManager().getConfig().getEndDelay() * 20L);
        });
    }

    public void stop() {
        // Remove all players from matches
        matches.removeIf(match -> {
            for (Player player : match.getPlayers().keySet()) {
                player.getInventory().clear();
                player.teleport(MurderAPI.INSTANCE.getFileManager().getLobbyFile().getLobby());
            }

            return true;
        });
    }

    public void createMatches() {
        for(Arena arena : MurderAPI.INSTANCE.getArenaManager().getArenas()) {
            Match match = new Match(arena);
            matches.add(match);
        }
    }

    public int getTotalOnline() {
        return matches.stream().mapToInt(match -> match.getPlayers().size()).sum();
    }

}
