package me.rejomy.murder.file.impl;

import lombok.Getter;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.file.Reloadable;
import me.rejomy.murder.util.ColorUtil;
import me.rejomy.murder.util.item.ItemObject;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class ItemsFile implements Reloadable {
    
    private final JavaPlugin plugin;

    private final File file = new File(MurderAPI.INSTANCE.getPlugin().getDataFolder(), "items.yml");

    private YamlConfiguration config;

    public HashMap<Integer, ItemObject> WAITING_ITEMS = new HashMap<>();
    public HashMap<Integer, ItemObject> ENDING_ITEMS = new HashMap<>();

    public ItemsFile() {
        this.plugin = MurderAPI.INSTANCE.getPlugin();
        
        reload();
    }

    @Override
    public void reload() {
        if(!file.exists()) {
            plugin.saveResource("items.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);

        fillItem(ENDING_ITEMS, "ending");
        fillItem(WAITING_ITEMS, "waiting.");
    }

    private void fillItem(HashMap<Integer, ItemObject> map, String section) {
        for(String slotSection : config.getConfigurationSection(section).getKeys(false)) {
            try {
                int slot = Integer.parseInt(slotSection);

                try {
                    Material material = Material.valueOf(config.getString(section + "." + slotSection + ".type"));

                    ItemStack item = new ItemStack(material);
                    ItemMeta meta = item.getItemMeta();

                    String name = config.getString(section + "." + slotSection + ".name");

                    if(name != null && !name.isEmpty()) {
                        meta.setDisplayName(ColorUtil.toColor(name));
                    }

                    List<String> lore = config.getStringList(section + "." + slotSection + ".lore");

                    if(lore != null) {
                        lore.replaceAll(ColorUtil::toColor);
                        meta.setLore(lore);
                    }

                    item.setItemMeta(meta);

                    List<String> commands = config.getStringList(section + "." + slotSection + ".action");

                    if(commands != null) {
                        commands.replaceAll(ColorUtil::toColor);
                    } else {
                        commands = new ArrayList<>();
                    }

                    ItemObject itemObject = new ItemObject(item, commands);
                    map.put(slot, itemObject);
                } catch (IllegalArgumentException exception) {
                    plugin.getLogger().severe("items.yml -> ending -> " + slotSection + " -> "
                            + config.getString(section + "." + slotSection + ".type") + " is incorrect material!");
                }
            } catch (NumberFormatException exception) {
                plugin.getLogger().severe("items.yml -> " + section + " -> slot " + slotSection +
                        " is not a number!");
            }
        }
    }
}
