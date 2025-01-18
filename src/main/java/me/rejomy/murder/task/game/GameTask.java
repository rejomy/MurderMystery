package me.rejomy.murder.task.game;

import lombok.Getter;
import lombok.Setter;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Arena;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.data.PlayerData;
import me.rejomy.murder.data.PlayerRole;
import me.rejomy.murder.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameTask implements Runnable {
    @Getter
    @Setter
    private int taskId;
    private final Match match;
    private final Arena arena;

    /**
     * Game delay equals match delay and decrease before he is stay zero,
     *  when he is to this, we stop the game as "time expired"
     */
    private int gameDelay = MurderAPI.INSTANCE.getFileManager().getConfig().getMatchDelay();

    public GameTask(Match match) {
        this.match = match;
        this.arena = match.getArena();
    }

    @Override
    public void run() {
        gameDelay--;

        // Просчитываем инвентари всех игроков и выдаем тому, у кого больше золото чем в конфиге лук.
        for (Player player : match.getAlivePlayers().keySet()) {
            int goldCount = 0;

            for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                ItemStack item = player.getInventory().getItem(slot);

                if (item != null && item.getType() == Material.GOLD_INGOT) {
                    goldCount += item.getAmount();
                }
            }

            int bowGoldCosts = MurderAPI.INSTANCE.getFileManager().getConfig().getBowGoldCosts();
            if (goldCount > bowGoldCosts) {
                // Give bow and arrow our detective.
                ItemUtil.giveBow(player);

                // Перезаписываем голд коунт и снова проходимся по инвентарю забирая золото
                // Когда кол-во забранного золота будет больше или равно нужному нам количеству для лука, останавливаем.
                // Сразу скажу, что собрать за 1 сек больше 5 золото анрил, поэтому, выдаем только 1 стрелу и забираем только 5 золота.
                goldCount = 0;

                for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                    ItemStack item = player.getInventory().getItem(slot);

                    if (item.getType() == Material.GOLD_INGOT) {
                        int amount = item.getAmount();
                        int needTake = bowGoldCosts - goldCount;

                        if (amount <= needTake) {
                            needTake -= amount;
                            item.setType(Material.AIR);
                        } else {
                            item.setAmount(item.getAmount() - needTake);
                            break;
                        }

                        if (needTake <= 0) {
                            break;
                        }

                        goldCount += amount;
                    }
                }
            }
        }

        // Если время вышло и убийцы не убили всех игроков, победа засчитывается игрокам.
        if (gameDelay == 0) {
            sendEndMessage(MurderAPI.INSTANCE.getFileManager().getMessage().getCompletionTimeEnd());

            MurderAPI.INSTANCE.getMatchManager().end(match);
            Bukkit.getScheduler().cancelTask(getTaskId());
            return;
        }
        // Если осталось до конца указанное в конфиге время, то выдаём убийцам компасс.
        // Или если игроков меньше нормы.
        else if (MurderAPI.INSTANCE.getFileManager().getConfig().getMatchDelay() - gameDelay <= MurderAPI.INSTANCE.getFileManager().getConfig().getCompassDelay()) {
            ItemUtil.updateCompass(match);
        }

        // Если убийц нету, победа засчитывается игрокам.
        if (match.getMurderers().isEmpty()) {
            sendEndMessage(MurderAPI.INSTANCE.getFileManager().getMessage().getCompletionMurderDeath());

            MurderAPI.INSTANCE.getMatchManager().end(match);
            Bukkit.getScheduler().cancelTask(taskId);
            return;
        }
        // Если невинных и детективов нету, победу получают убийцы.
        else if (match.getDetectives().isEmpty() && match.getInnocents().isEmpty()) {
            sendEndMessage(MurderAPI.INSTANCE.getFileManager().getMessage().getCompletionMurderWin());

            MurderAPI.INSTANCE.getMatchManager().end(match);
            Bukkit.getScheduler().cancelTask(taskId);
        }

        // Give sword to all our murders
        int delayInSeconds = MurderAPI.INSTANCE.getFileManager().getConfig().getMatchDelay() - gameDelay;

        for (Map.Entry<Player, PlayerData> entry : match.getAlivePlayers().entrySet()) {
            Player player = entry.getKey();
            PlayerData data = entry.getValue();

            // Give role after some time.
            if (data.receiveRoleDelay == delayInSeconds) {
                match.giveRole(player, data);
            }

            // Check if when role is give player dont receive item, we give item with delay in config.
            if (!MurderAPI.INSTANCE.getFileManager().getConfig().isGiveItemWithRole()) {
                if (data.role == PlayerRole.MURDER) {
                    if (delayInSeconds == data.receiveCompassDelay) {
                        ItemUtil.giveCompassToSlot(player, 8);
                    }

                    if (delayInSeconds == data.receiveSwordDelay) {
                        ItemUtil.giveSword(player);
                    }
                } else if (data.role == PlayerRole.DETECTIVE) {
                    if (delayInSeconds == data.receiveBowDelay) {
                        ItemUtil.giveBow(player);
                    }
                }
            }
        }

        // Спавним золото, если прошло указанное время в конфиге.
        // Запускаем в главном потоке, так-как иначе будет исключение AsynchronousEntityWorldAdd;
        Bukkit.getScheduler().runTask(MurderAPI.INSTANCE.getPlugin(), () -> {
            if (gameDelay % MurderAPI.INSTANCE.getFileManager().getConfig().getGoldSpawnDelay() == 0) {
                // Спавним золото n раз.
                List<Location> locations = new ArrayList<>(arena.goldSpawnPositions);

                for (byte a = 0; a < MurderAPI.INSTANCE.getFileManager().getConfig().getGoldSpawnAmount(); a++) {
                    if (locations.isEmpty()) {
                        break;
                    }

                    int index = RandomUtil.RANDOM.nextInt(locations.size());
                    Location location = locations.get(index);
                    locations.remove(location);

                    // Заносим этого entity в getDroppedGold, чтобы в конце игры удалить их.
                    match.getDroppedGold().add(
                            location.getWorld().dropItemNaturally(
                                    location, new ItemBuilder(Material.GOLD_INGOT).setName("&e&lGoldIngot").build()));
                }
            }
        });

        // Ставим опыт как единицу показа времени.
        for (Player player : match.getPlayers().keySet()) {
            player.setLevel(gameDelay);
        }
    }

    private void sendEndMessage(List<String> message) {
        HashMap<String, PlayerRole> players = match.getLeavePlayers();

        players.putAll(
                match.getPlayers().entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey().getName(),   // Map the Player's name (String) as the key
                                entry -> entry.getValue().role       // Map the PlayerData's role (PlayerRol) as the value
                        ))
        );

        String murders = players.entrySet().stream().filter(entry -> entry.getValue() == PlayerRole.MURDER)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));

        String detectives = players.entrySet().stream().filter(entry -> entry.getValue() == PlayerRole.DETECTIVE)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));

        String heroes = players.entrySet().stream().filter(entry -> entry.getValue() == PlayerRole.HERO)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));

        message.replaceAll(line -> line.replace("$detectives", detectives).replace("$murders", murders).replace("$heroes", heroes));

        for (Player player : match.getPlayers().keySet()) {
            MessageUtil.sendMessage(player, message);
        }
    }
}
