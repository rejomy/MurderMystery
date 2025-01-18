package me.rejomy.murder.listener;

import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Match;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        Match match = MurderAPI.INSTANCE.getMatchManager().get(player);

        if (match == null || inventory == null ||
                !MurderAPI.INSTANCE.getFileManager().getConfig().isCancelMoveItemToOtherInventory()) {
            return;
        }

        // Check if the player shift-clicked or dragged the item
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            // Check if the clicked inventory is the player's inventory
            if (inventory.getType() == InventoryType.PLAYER) {
                // Check if the destination inventory is not the player's inventory (i.e., a container)
                if (event.getView().getTopInventory().getType() != InventoryType.PLAYER) {
                    // Cancel the event to prevent moving the item from player inventory to container
                    event.setCancelled(true);
                }
            }
        } else {
            // Handle regular clicks (non-shift)
            Inventory topInventory = event.getView().getTopInventory();

            // Check if the clicked inventory is the player's inventory and the destination is another inventory
            if (inventory.getType() == InventoryType.PLAYER && topInventory.getType() != InventoryType.PLAYER) {
                // Cancel the event to prevent moving the item
                event.setCancelled(true);
            }
        }
    }

}
