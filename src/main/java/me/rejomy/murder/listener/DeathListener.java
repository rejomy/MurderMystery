package me.rejomy.murder.listener;

import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.util.FlyingItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Match match = MurderAPI.INSTANCE.getMatchManager().get(player);

        if(match == null) {
            return;
        }

        // Check if player died in void, we dont need to spawn bow.
        if (player.getLastDamageCause().getCause() != EntityDamageEvent.DamageCause.VOID) {
            if (match.isDetective(player)) {
                FlyingItemUtil.spawnFlyingBow(player, match);
            }
        }

        // Kill player
        match.kill(player);
    }
}
