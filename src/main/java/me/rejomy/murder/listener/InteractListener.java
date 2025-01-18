package me.rejomy.murder.listener;

import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Arena;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.data.PlayerData;
import me.rejomy.murder.file.impl.ItemsFile;
import me.rejomy.murder.util.ArenaEditor;
import me.rejomy.murder.util.CommandUtil;
import me.rejomy.murder.util.FlyingItemUtil;
import me.rejomy.murder.util.item.ItemObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InteractListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        // Проверяем, является ли сущность стойкой для брони
        if (entity instanceof ArmorStand) {
            Match match = MurderAPI.INSTANCE.getMatchManager().get(player);

            if (match != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Arena editor
        if (player.getItemInHand() != null &&
                player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasDisplayName() &&
                MurderAPI.INSTANCE.getDataManager().get(player).isInEditMode) {
            Location location = event.hasBlock() ? event.getClickedBlock().getLocation().clone().add(0, 1.0, 0)
                    : player.getLocation();

            switch (player.getItemInHand().getItemMeta().getDisplayName()) {
                case "§aPlayer Spawn Pos": {
                    ArenaEditor.addSpawnPos(player, location, MurderAPI.INSTANCE.getDataManager().get(player).arenaName);
                    event.setCancelled(true);
                    return;
                }
                case "§eGold Spawn Pos": {
                    ArenaEditor.addGoldSpawnPos(player, location, MurderAPI.INSTANCE.getDataManager().get(player).arenaName);
                    event.setCancelled(true);
                    return;
                }
                case "§cArena Waiting Pos": {
                    ArenaEditor.setArenaLobby(player, location, MurderAPI.INSTANCE.getDataManager().get(player).arenaName);
                    event.setCancelled(true);
                    return;
                }
                case "§bSet World": {
                    ArenaEditor.setArenaWorld(player, MurderAPI.INSTANCE.getDataManager().get(player).arenaName);
                    event.setCancelled(true);
                    return;
                }
            }

            return;
        }
        //

        Match match = MurderAPI.INSTANCE.getMatchManager().get(player);

        if (match == null) {
            return;
        }

        ItemStack item = player.getItemInHand();

        // Handle command in items.yml
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) &&
                item != null && item.hasItemMeta()) {
            ItemsFile itemsFile = MurderAPI.INSTANCE.getFileManager().getItemsFile();

            if (match.getArena().status == Arena.Status.ENDING || match.getPlayers().get(player).spectator) {
                HashMap<Integer, ItemObject> ENDING_ITEMS = itemsFile.getENDING_ITEMS();

                for (Map.Entry<Integer, ItemObject> entry : ENDING_ITEMS.entrySet()) {
                    ItemObject itemObject = entry.getValue();

                    if (itemObject.item.equals(item)) {
                        for (String command : itemObject.commands) {
                            CommandUtil.runCommand(player, command);
                        }
                    }
                }

            } else if (match.getArena().status == Arena.Status.WAITING || match.getArena().status == Arena.Status.STARTING) {
                HashMap<Integer, ItemObject> WAITING_ITEMS = itemsFile.getWAITING_ITEMS();

                for (Map.Entry<Integer, ItemObject> entry : WAITING_ITEMS.entrySet()) {
                    ItemObject itemObject = entry.getValue();

                    if (itemObject.item.equals(item)) {
                        for (String command : itemObject.commands) {
                            CommandUtil.runCommand(player, command);
                        }
                    }
                }

            }

            event.setCancelled(true);
        }
        //

        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        if (!match.isMurder(player)) {
            return;
        }

        PlayerData data = MurderAPI.INSTANCE.getDataManager().get(player);

        if (player.getItemInHand() != null
                && player.getItemInHand().getType() == MurderAPI.INSTANCE.getDataManager().get(player).swordSkin
                && data.swordKnockDelay == 0) {
            FlyingItemUtil.shootSword(player, data, match);
            event.setCancelled(true);
        }
    }
}
