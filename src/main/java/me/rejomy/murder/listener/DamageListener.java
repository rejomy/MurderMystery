package me.rejomy.murder.listener;

import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.util.FlyingItemUtil;
import me.rejomy.murder.util.Logger;
import me.rejomy.murder.util.PlayerUtil;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;

public class DamageListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void on(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            if(event.getEntity() instanceof ArmorStand && event.getDamager() instanceof Player) {
                if(MurderAPI.INSTANCE.getMatchManager().get((Player) event.getDamager()) != null) {
                    event.setCancelled(true);
                }
            }

            return;
        }

        Player player = (Player) event.getEntity();

        if (PlayerUtil.isNPC(player)) {
            return;
        }

        if (event.getDamager() instanceof Arrow) {
            if (!(((Arrow) event.getDamager()).getShooter() instanceof Player)) {
                return;
            }

            Player shooter = (Player) ((Arrow) event.getDamager()).getShooter();

            Match match = MurderAPI.INSTANCE.getMatchManager().get(shooter);

            if (match != null) {
                // Remove arrow for prevent stuck in player body.
                event.getDamager().remove();

                if (match.isDetective(shooter)) {
                    if (match.isMurder(player)) {
                        match.kill(player, true);
                    } else {
                        match.kill(shooter, false);
                        match.kill(player, true);
                    }
                }
            }

            setAction(event, "bow-hit");
            // Set action and if event does not cancelled, give damage to the player.
            if (!event.isCancelled()) {
                player.damage(event.getDamage());
            }

            event.setCancelled(true);
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player killer = (Player) event.getDamager();

        Match match = MurderAPI.INSTANCE.getMatchManager().get(player);

        if (match == null) {
            return;
        }

        if (match.isMurder(killer) && killer.getInventory().getItemInHand() != null
                && killer.getItemInHand().getType() == MurderAPI.INSTANCE.getDataManager().get(killer).swordSkin) {
            if (match.isMurder(player)) {
                killer.damage(2);
            } else {
                if(match.isDetective(player)) {
                    FlyingItemUtil.spawnFlyingBow(player, match);
                }

                match.kill(player, true);

                setAction(event, "murder-hit");
                // Set action and if event does not cancelled, give damage to the player.
                if (!event.isCancelled()) {
                    player.damage(event.getDamage());
                }
            }

            event.setCancelled(true);
        } else {
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                setAction(event, "player-hit");
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Match match = MurderAPI.INSTANCE.getMatchManager().get(player);

        if(match != null) {
            // Спектаторы бессмертны.
            if(match.getPlayers().get(player).spectator) {
                event.setCancelled(true);
                return;
            }

            EntityDamageEvent.DamageCause cause = event.getCause();

            if(cause == EntityDamageEvent.DamageCause.FALL) {
                setAction(event, "fall");
            } else if(cause == EntityDamageEvent.DamageCause.FIRE) {
                setAction(event, "burn");
            } else if(cause == EntityDamageEvent.DamageCause.DROWNING) {
                setAction(event, "water");
            } else if (cause == EntityDamageEvent.DamageCause.VOID) {
                setAction(event, "void");
            } else {
                setAction(event, "any");
            }
        }
    }

    /**
     * Set damage value by config value.
     * @param event current EntityDamageEvent
     * @param path path to action string with value
     */
    private void setAction(EntityDamageEvent event, String path) {
        HashMap<String, String> damage = MurderAPI.INSTANCE.getFileManager().getConfig().getDamageValues();
        String action = damage.get(path);

        if(action.equalsIgnoreCase("disable")) {
            event.setCancelled(true);
            return;
        } else if(action.equalsIgnoreCase("zero")) {
            event.setDamage(0);
            return;
        } else if(action.equalsIgnoreCase("default")) {
            return;
        }

        try {
            int value = Integer.parseInt(action);
            event.setDamage(value);
        } catch (NumberFormatException exception) {
            Logger.warn("Error write action in config:settings.game.damage." + path + "! Action " + action + " not found!");
        }
    }
}
