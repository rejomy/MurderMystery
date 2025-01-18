package me.rejomy.murder.listener;

import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Match;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (!MurderAPI.INSTANCE.getFileManager().getConfig().isCancelBlockBreak()) {
            return;
        }

        Player player = event.getPlayer();
        Match match = MurderAPI.INSTANCE.getMatchManager().get(player);

        if (match != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockPlaceEvent event) {
        if (!MurderAPI.INSTANCE.getFileManager().getConfig().isCancelBlockPlace()) {
            return;
        }

        Player player = event.getPlayer();
        Match match = MurderAPI.INSTANCE.getMatchManager().get(player);

        if (match != null) {
            event.setCancelled(true);
        }
    }
}
