package me.rejomy.murder.file.impl;

import lombok.Getter;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.file.FileBuilder;
import me.rejomy.murder.file.Reloadable;
import me.rejomy.murder.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LobbyFile implements Reloadable {
    @Getter
    Location lobby;

    public LobbyFile() {
        reload();
    }

    @Override
    public void reload() {
        File file = new File(MurderAPI.INSTANCE.getPlugin().getDataFolder(), "lobby.yml");

        if(!file.exists()) {
            FileBuilder.create(file);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        if(config.get("world") == null) {
            Logger.info("Lobby location is not found. Use world spawn location.");

            for(World world : Bukkit.getWorlds()) {
                lobby = world.getSpawnLocation();
                break;
            }

            return;
        }

        World world = Bukkit.getWorld(config.getString("world"));
        double x = config.getDouble("x");
        double y = config.getDouble("y");
        double z = config.getDouble("z");
        float yaw = (float) config.getDouble("yaw");
        float pitch = (float) config.getDouble("pitch");

        lobby = new Location(world, x, y, z, yaw, pitch);
    }
}
