package me.rejomy.murder.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.PlayerData;
import me.rejomy.murder.file.FileBuilder;
import me.rejomy.murder.util.ItemBuilder;
import me.rejomy.murder.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;

@CommandAlias("murder|murdermystery")
public class MurderCreate extends BaseCommand {
    @Subcommand("create")
    @CommandPermission("murder.edit")
    public void onCreate(Player player, String name) {
        if(MurderAPI.INSTANCE.getArenaManager().hasArena(name)) {
            MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getAlreadyStart()
                    .replace("$arena", name));
            return;
        }

        File file = new File(MurderAPI.INSTANCE.getFileManager().getArenasDirectory(), name + ".yml");
        FileBuilder.create(file);

        MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getArenaCreate()
                .replace("$arena", name));

        player.getInventory().clear();

        player.getInventory().setItem(0, new ItemBuilder(Material.STICK).setName("&aPlayer Spawn Pos").build());
        player.getInventory().setItem(1, new ItemBuilder(Material.GOLD_INGOT).setName("&eGold Spawn Pos").build());
        player.getInventory().setItem(2, new ItemBuilder(Material.GRASS).setName("&bSet World").build());
        player.getInventory().setItem(3, new ItemBuilder(Material.BED).setName("&cArena Waiting Pos").build());

        PlayerData data = MurderAPI.INSTANCE.getDataManager().get(player);
        data.isInEditMode = true;
        data.arenaName = name;
    }
}