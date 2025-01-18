package me.rejomy.murder.util;

import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.file.FileBuilder;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ArenaEditor {

    public static void addSpawnPos(Player player, Location location, String name) {
        File file = new File(MurderAPI.INSTANCE.getFileManager().getArenasDirectory(), name + ".yml");

        if(!file.exists()) {
            MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getArenaNotFound()
                    .replace("$arena", name));
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        Object maybePositions = config.get("spawn-positions");

        List<String> spawnPositions = maybePositions != null? (List<String>) maybePositions
                : new ArrayList<>();

        String position = location.getX() + " "
                + location.getY() + " "
                + location.getZ() + " "
                + location.getYaw() + " "
                + location.getPitch();
        spawnPositions.add(position);

        config.set("spawn-positions", spawnPositions);

        FileBuilder.save(file, config);

        MessageUtil.sendMessage(player, "Add player spawn position for arena " + name);
    }

    public static void addGoldSpawnPos(Player player, Location location, String name) {
        File file = new File(MurderAPI.INSTANCE.getFileManager().getArenasDirectory(), name + ".yml");

        if(!file.exists()) {
            MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getArenaNotFound()
                    .replace("$arena", name));
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        Object maybePositions = config.get("spawn-gold-positions");

        List<String> spawnPositions = maybePositions != null? (List<String>) maybePositions
                : new ArrayList<>();

        String position = location.getX() + " "
                + location.getY() + " "
                + location.getZ() + " "
                + location.getYaw() + " "
                + location.getPitch();
        spawnPositions.add(position);

        config.set("spawn-gold-positions", spawnPositions);

        FileBuilder.save(file, config);

        MessageUtil.sendMessage(player, "Add gold spawn position for arena " + name);
    }

    public static void setArenaWorld(Player player, String name) {
        File file = new File(MurderAPI.INSTANCE.getFileManager().getArenasDirectory(), name + ".yml");

        if(!file.exists()) {
            MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getArenaNotFound()
                    .replace("$arena", name));
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("world", player.getWorld().getName());

        FileBuilder.save(file, config);

        MessageUtil.sendMessage(player, "Set world " + player.getWorld().getName()
                + " for arena " + name);
    }

    public static void setArenaLobby(Player player, Location location, String name) {
        File file = new File(MurderAPI.INSTANCE.getFileManager().getArenasDirectory(), name + ".yml");

        if (!file.exists()) {
            MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getArenaNotFound()
                    .replace("$arena", name));
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        String position = location.getX() + " "
                + location.getY() + " "
                + location.getZ() + " "
                + location.getYaw() + " "
                + location.getPitch();

        config.set("lobby", position);

        FileBuilder.save(file, config);

        MessageUtil.sendMessage(player, "Set lobby position for arena " + name);
    }
}
