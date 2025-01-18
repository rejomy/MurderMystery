package me.rejomy.murder.data;

import org.bukkit.Location;

import java.util.List;

public class Arena {

    public String name;
    public Location lobbyPosition;
    public List<Location> spawnPositions; // Spawn position should be equals or larger than players size.
    public List<Location> goldSpawnPositions; // Spawn gold position, the bigger, the better :D
    public int minPlayers, maxPlayers; // Players amount for an arena.
    public Status status = Status.WAITING;

    // Status see a situation in the arena.
    public enum Status {
        WAITING, // We can join to the arena.
        STARTING, // We can join to the arena, but match already starts.
        PLAYING, // We can`t join to the arena, match has been started.
        ENDING; // We can`t join and spectator to the arena, match ending.
    }
}
