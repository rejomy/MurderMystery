package me.rejomy.murder.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class InventoryUtil {

    public boolean hasItem(Player player, Material material) {
        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);

            if (item != null && item.getType() == material) return true;
        }

        return false;
    }

}
