package me.rejomy.murder.listener;

import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Arena;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.data.PlayerData;
import me.rejomy.murder.data.PlayerRole;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

public class ShootBowListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Match match = MurderAPI.INSTANCE.getMatchManager().get(player);

        if (match == null) {
            return;
        }

        PlayerData data = match.getPlayers().get(player);

        if (data.infinityArrow || data.role == PlayerRole.DETECTIVE) {
            Bukkit.getScheduler().scheduleAsyncDelayedTask(MurderAPI.INSTANCE.getPlugin(), () -> {
                        if (match.getArena().status != Arena.Status.PLAYING) {
                            return;
                        }

                        player.getInventory().setItem(8, new ItemStack(Material.ARROW));
                    }, MurderAPI.INSTANCE.getFileManager().getConfig().getReturnArrowDelay() * 20L);
        }
    }

    // Remove arrow method from floor.
    @EventHandler
    public void onEvent(ProjectileHitEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Arrow) {
            boolean worldIsArenaWorld = MurderAPI.INSTANCE.getArenaManager().getArenas().stream()
                    .anyMatch(arena -> arena.spawnPositions.get(0).getWorld().equals(entity.getWorld()));

            if (worldIsArenaWorld) {
                entity.remove();
            }
        }
    }
}
