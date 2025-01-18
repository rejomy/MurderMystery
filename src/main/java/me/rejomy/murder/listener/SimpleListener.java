package me.rejomy.murder.listener;

import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Match;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

// Здесь все мелкие события.
public class SimpleListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void on(FoodLevelChangeEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        if(MurderAPI.INSTANCE.getFileManager().getConfig().isCancelHunger() &&
                MurderAPI.INSTANCE.getMatchManager().get((Player) event.getEntity()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Match match = MurderAPI.INSTANCE.getMatchManager().get(player);

        if(match == null) {
            return;
        }

        event.setCancelled(true);
    }
}
