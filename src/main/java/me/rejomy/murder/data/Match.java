package me.rejomy.murder.data;

import lombok.Getter;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.util.*;
import me.rejomy.murder.util.item.ItemObject;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class Match {

    private final Arena arena;
    private final HashMap<Player, PlayerData> players = new HashMap<>();
    // Here we handle players who leave from the match. Key is name, value is role.
    private final HashMap<String, PlayerRole> leavePlayers = new HashMap<>();
    List<Item> droppedGold = new ArrayList<>();

    /**
     * Use for prevent situation when player is leave and waiting task could cancelled so first task dont cancel because player is small
     * and we start second task, but at first task when is check (every second) player equals two, because people is leave and join.
     */
    public boolean schedule;

    public Match(Arena arena) {
        this.arena = arena;
    }

    public HashMap<Player, PlayerData> getAlivePlayers() {
        return (HashMap<Player, PlayerData>) players.entrySet().stream().filter(entry -> !entry.getValue().spectator)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public List<Player> getMurderers() {
        return players.entrySet().stream().filter(entry -> {
                    PlayerData data = entry.getValue();
                    return data.role == PlayerRole.MURDER && !data.spectator;
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Player> getDetectives() {
        return players.entrySet().stream().filter(entry -> {
                    PlayerData data = entry.getValue();
                    return data.role == PlayerRole.DETECTIVE && !data.spectator;
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Player> getInnocents() {
        return players.entrySet().stream().filter(entry -> {
                    PlayerData data = entry.getValue();
                    return (data.role == PlayerRole.HERO || data.role == PlayerRole.INNOCENT) && !data.spectator;
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public boolean isMurder(Player player) {
        PlayerData data = players.get(player);
        return data != null && !data.spectator && data.role == PlayerRole.MURDER;
    }

    public boolean isDetective(Player player) {
        PlayerData data = players.get(player);
        return data != null && !data.spectator && data.role == PlayerRole.DETECTIVE;
    }

    public void kill(Player player) {
        kill(player, false);
    }

    public void kill(Player target, boolean wasKilled) {
        // Dont check spectators for prevent duplicate messages, for example when damage.
        if (players.get(target).spectator) return;

        PlayerUtil.clearInventory(target);
        PlayerUtil.clearEffects(target);

        for (Player player : players.keySet()) {
            if (wasKilled) {
                MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getEntityWasKilled(),
                        "player", target.getName());
            } else {
                MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getEntityDead(),
                        "player", target.getName());
            }
        }

        players.get(target).wasKilled = true;

        if (MurderAPI.INSTANCE.getFileManager().getConfig().isSpectators()) {
            addSpectator(target);
            ItemUtil.giveItemsFromConfig(target, MurderAPI.INSTANCE.getFileManager().getItemsFile().ENDING_ITEMS);
        } else {
            remove(target);
        }
    }

    /**
     * Use for remove from match, on quit or on move from world.
     *
     * @param target - who should be removed.
     */
    public void remove(Player target) {
        if (getArena().status == Arena.Status.PLAYING) {
            if (isDetective(target)) {
                FlyingItemUtil.spawnFlyingBow(target, this);
            }

            for (Player player : players.keySet()) {
                MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getEntityDead()
                        .replace("$player", target.getName()));
            }
        } else {
            for (Player player : players.keySet()) {
                MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getLeave(),
                        "arena", arena.name, "player", player.getName(), "max", arena.maxPlayers,
                        "current", getPlayers().size() - 1);
            }
        }

        removeSpectator(target);

        PlayerData data = players.remove(target);

        if (data.wasKilled) {
            MurderUtil.saveStats(data);
            data.wasKilled = false;
        }

        data.currentMatch = null;
        data.infinityArrow = false;

        // Reset player level.
        target.setLevel(0);

        PlayerUtil.clearInventory(target);
        PlayerUtil.clearEffects(target);

        TeleportUtil.teleportToLobby(target);

        // Add player as leave player.
        leavePlayers.put(target.getName(), data.role);
    }

    public void addSpectator(Player player) {
        players.get(player).spectator = true;

        player.setAllowFlight(true);
        player.setFlying(true);

        players.forEach((key, value) -> {
            if (!value.spectator) {
                key.hidePlayer(player);
            }
        });
    }

    public void removeSpectator(Player player) {
        PlayerData data = players.get(player);

        // If player does not spectator, we dont need to remove him from spec :)
        if (!data.spectator) return;

        data.spectator = false;

        player.setAllowFlight(false);
        player.setFlying(false);

        players.forEach((key, value) -> {
            player.showPlayer(key);
            key.showPlayer(player);
        });
    }

    public void giveRole(Player player, PlayerData data) {
        boolean giveItemsWithRole = MurderAPI.INSTANCE.getFileManager().getConfig().isGiveItemWithRole();

        if (data.role == PlayerRole.DETECTIVE) {
            if (giveItemsWithRole) ItemUtil.giveBow(player);
            MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getDetectiveRole());
        } else if (data.role == PlayerRole.INNOCENT) {
            MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getInnocentRole());
        } else if (data.role == PlayerRole.MURDER) {
            if (giveItemsWithRole) {
                ItemUtil.giveSword(player);
                ItemUtil.giveCompassToSlot(player, 8);
            }

            MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getMurderRole(),
                    "time", data.receiveSwordDelay);
        }
    }
}
