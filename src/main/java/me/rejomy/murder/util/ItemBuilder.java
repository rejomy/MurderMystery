package me.rejomy.murder.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {
    ItemStack itemStack;
    ItemMeta meta;
    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.meta = itemStack.getItemMeta();
    }

    public ItemStack build() {
        return this.itemStack;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setName(String name) {
        meta.setDisplayName(ColorUtil.toColor(name));
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(String[] lines) {
        List<String> lore = Arrays.stream(lines).collect(Collectors.toList());
        lore.replaceAll(ColorUtil::toColor);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return this;
    }
}
