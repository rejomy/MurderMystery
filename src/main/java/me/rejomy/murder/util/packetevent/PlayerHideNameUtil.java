package me.rejomy.murder.util.packetevent;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

@UtilityClass
public class PlayerHideNameUtil {

    private final String MURDER_TEAM_NAME = "murderHideName";
    private final HashMap<UUID, String> originalNames = new HashMap<>();

    // Hide player name for all players and replace with random names
    public void hide(Player player) {
        originalNames.put(player.getUniqueId(), player.getName());

        // Generate random name for each player
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            String randomName = generateRandomName();

            // Modify tab list name
            sendPlayerInfoPacket(otherPlayer, player, randomName);

            // Modify the nametag for the other player
            setNametag(otherPlayer, player, randomName);
        }
    }

    // Unhide player name and restore original name for all players
    public void unhide(Player player) {
        String originalName = originalNames.remove(player.getUniqueId());

        if (originalName != null) {
            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                // Restore tab list name
                sendPlayerInfoPacket(otherPlayer, player, originalName);

                // Restore nametag
                Scoreboard scoreboard = otherPlayer.getScoreboard();
                removeTeam(scoreboard, originalName); // Ensure old teams are removed
                setNametag(otherPlayer, player, originalName);
            }
        }
    }

    private void removeTeam(Scoreboard scoreboard, String teamName) {
        Team team = scoreboard.getTeam(teamName);
        if (team != null) {
            team.unregister();
        }
    }

    // Helper method to generate a random name
    private String generateRandomName() {
        int randomNum = (int) (Math.random() * 900) + 100; // Generate 5-digit random number
        return "Player" + randomNum;
    }

    // Send the PlayerInfo packet to update the tab list name
    private void sendPlayerInfoPacket(Player recipient, Player target, String newName) {
        List<WrapperPlayServerPlayerInfo.PlayerData> playerDataList = new ArrayList<>();

        // Create a dummy UserProfile if it's missing
        UserProfile userProfile = new UserProfile(target.getUniqueId(), target.getName());

        // Ensure UserProfile is not null before creating PlayerData
        WrapperPlayServerPlayerInfo.PlayerData playerData = new WrapperPlayServerPlayerInfo.PlayerData(
                Component.text(newName), userProfile, GameMode.defaultGameMode(), -1);

        playerDataList.add(playerData);

        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(
                WrapperPlayServerPlayerInfo.Action.UPDATE_DISPLAY_NAME, playerDataList);

        // Send the packet to the recipient
        PacketEvents.getAPI().getPlayerManager().sendPacket(recipient, packet);
    }

    public void hidePlayerFromAllObserversInWorld(Player player) {
        player.getWorld().getPlayers().forEach(observer -> hideNick(observer, player.getName()));
    }

    public void hideNick(Player player, String hide) {
        Team t = getHideTeam(player);

        if (!t.hasEntry(hide))
            t.addEntry(hide);
    }

    public void showPlayerToAll(Player player) {
        Bukkit.getOnlinePlayers().forEach(observer -> showNick(observer, player.getName()));
    }

    public void showNick(Player player, String show) {
        Team team = getHideTeam(player);

        if (team.hasEntry(show))
            team.removeEntry(show);
    }

    public void showAll(Player player) {
        Team team = getHideTeam(player);
        HashSet<String> entry = new HashSet<>(team.getEntries());

        for (String string : entry) {
            team.removeEntry(string);
        }
    }

    // Return team with hide name tag
    private Team getHideTeam(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam(MURDER_TEAM_NAME);

        if (team == null) {
            team = scoreboard.registerNewTeam(MURDER_TEAM_NAME);
            team.setNameTagVisibility(NameTagVisibility.NEVER);
        }

        return team;
    }
}