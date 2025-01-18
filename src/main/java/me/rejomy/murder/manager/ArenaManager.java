package me.rejomy.murder.manager;

import lombok.Getter;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Arena;
import me.rejomy.murder.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArenaManager {
    @Getter
    private List<Arena> arenas = new ArrayList<>();

    public ArenaManager() {
        loadArenas();
    }

    public boolean hasArena(String name) {
        return arenas.stream().anyMatch(arena -> arena.name.equals(name));
    }

    public Arena getArena(String name) {
        return arenas.stream().filter(arena -> arena.name.equals(name))
                .findAny().orElse(null);
    }

    public void loadArenas() {
        File arenas = MurderAPI.INSTANCE.getFileManager().getArenasDirectory();

        for(File arenaFile : arenas.listFiles()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(arenaFile);

            Arena arena = new Arena();
            arena.name = arenaFile.getName().replace(".yml", "");

            arena.minPlayers = (int) Objects.requireNonNullElse(config.get("players.min"),
                    MurderAPI.INSTANCE.getFileManager().getConfig().getDefaultMinPlayers());

            arena.maxPlayers = (int) Objects.requireNonNullElse(config.get("players.max"),
                    MurderAPI.INSTANCE.getFileManager().getConfig().getDefaultMaxPlayers());

            String worldName = config.getString("world");
            if(worldName == null) {
                worldName = MurderAPI.INSTANCE.getFileManager().getConfig().getDefaultWorld();
            }
            World world = Bukkit.getWorld(worldName);

            arena.spawnPositions = new ArrayList<>();

            {
                if(config.get("lobby") == null) {
                    Logger.severe("Arena " + arena.name + " is invalid! Not have lobby position.");
                    continue;
                }

                String[] args = config.getString("lobby").split(" ");

                arena.lobbyPosition = new Location(world, Double.parseDouble(args[0]),
                    Double.parseDouble(args[1]),
                    Double.parseDouble(args[2]),
                    Float.parseFloat(args[3]),
                    Float.parseFloat(args[4]));
            }

            for(String positionInfo : config.getStringList("spawn-positions")) {
                String[] args = positionInfo.split(" ");

                Location location = new Location(world, Double.parseDouble(args[0]),
                        Double.parseDouble(args[1]),
                        Double.parseDouble(args[2]),
                        Float.parseFloat(args[3]),
                        Float.parseFloat(args[4]));

                arena.spawnPositions.add(location);
            }

            arena.goldSpawnPositions = new ArrayList<>();

            for(String positionInfo : config.getStringList("spawn-gold-positions")) {
                String[] args = positionInfo.split(" ");

                Location location = new Location(world, Double.parseDouble(args[0]),
                        Double.parseDouble(args[1]),
                        Double.parseDouble(args[2]),
                        Float.parseFloat(args[3]),
                        Float.parseFloat(args[4]));

                arena.goldSpawnPositions.add(location);
            }

            this.arenas.add(arena);
        }
    }
}
