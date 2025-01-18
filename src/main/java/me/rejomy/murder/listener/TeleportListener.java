package me.rejomy.murder.listener;

import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Match match = MurderAPI.INSTANCE.getMatchManager().get(player);

        if (match == null)
            return;

        Location to = event.getTo(), from = event.getFrom();

        // Prevent teleports from world
        if (MurderAPI.INSTANCE.getFileManager().getConfig().isTeleportFromWorld() &&
            !to.getWorld().equals(from.getWorld()) && !to.getWorld().equals(match.getArena().lobbyPosition.getWorld())) {
            match.remove(player);
        }
    }

}
