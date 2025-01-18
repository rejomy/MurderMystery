package me.rejomy.murder.util;

import lombok.experimental.UtilityClass;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.util.item.ItemObject;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ItemUtil {

    public void giveBow(Player player) {
        player.getInventory().setHeldItemSlot(1);
        player.getInventory().setItem(0, new ItemStack(Material.BOW));
        player.getInventory().setItem(8, new ItemStack(Material.ARROW));
    }

    public void giveSword(Player player) {
        player.getInventory().setHeldItemSlot(1);
        player.getInventory().setItem(0, new ItemStack(MurderAPI.INSTANCE.getDataManager().get(player).swordSkin));
    }

    public void updateCompass(Match match) {
        for (Player murder : match.getMurderers()) {
            ItemStack inHand = murder.getItemInHand();

            if (inHand != null && inHand.getType() == Material.COMPASS) {
                List<Player> innocentsAndDetectives = match.getInnocents();
                innocentsAndDetectives.addAll(match.getDetectives());
                Player bestTarget = innocentsAndDetectives.stream()
                        .sorted(Comparator.comparing(player ->
                                player.getLocation().distanceSquared(murder.getLocation()))).findAny()
                        .orElse(null);

                // If targets not found, we can get error, so.. we should return.
                if (bestTarget == null) return;

                ItemMeta meta = inHand.getItemMeta();

                meta.setDisplayName(bestTarget.getName() + " " + ((int) bestTarget.getLocation().distance(murder.getLocation())));

                inHand.setItemMeta(meta);

                murder.setCompassTarget(bestTarget.getLocation());
            }
        }
    }

    public void giveCompassToSlot(Player player, int slot) {
        Inventory inventory = player.getInventory();
        inventory.setItem(slot, new ItemStack(Material.COMPASS));
    }

    public void giveItemsFromConfig(Player player, HashMap<Integer, ItemObject> items) {
        for (Map.Entry<Integer, ItemObject> entry : items.entrySet()) {
            int slot = entry.getKey();
            ItemStack item = entry.getValue().item;

            player.getInventory().setItem(slot, item);
        }
    }
}
