package me.rejomy.murder.data;

import org.bukkit.Material;

import java.util.UUID;

public class PlayerData {

    public final UUID uuid;

    // EDITOR MODE - OPEN
    public boolean isInEditMode;
    public String arenaName;
    // EDITOR MODE - CLOSE

    public int gamesWithoutMurderRole = 15;
    public int gamesWithoutDetectiveRole = 15;

    public int games;
    public int wins;

    public Material swordSkin = Material.IRON_SWORD;

    public int swordKnockDelay;

    /**
     * Need for give player bow, sword and compass with random delay. @_@
     */
    public int receiveCompassDelay;
    public int receiveSwordDelay;
    public int receiveBowDelay;
    /**
     * Time in seconds before player receive his role.
     */
    public int receiveRoleDelay;

    public Match currentMatch;
    public boolean spectator;
    public PlayerRole role;
    // Uses for player who take bow from died detective.
    public boolean infinityArrow;
    // Need for prevent farm chances, player should died in the game for increase his chance to murder and detective.
    public boolean wasKilled;
    public int chanceToMurder = 0;
    public int chanceToDetective = 0;


    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }
}
