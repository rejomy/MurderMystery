package me.rejomy.murder.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.file.FileBuilder;
import me.rejomy.murder.util.MessageUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

@CommandAlias("murder|murdermystery")
public class MurderPlayers extends BaseCommand {

    @Subcommand("set")
    @CommandPermission("murder.edit")
    public void onSetPlayers(Player player, String name, int min, int max) {
        if(!new File(MurderAPI.INSTANCE.getFileManager().getArenasDirectory(), name + ".yml").exists()) {
            MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getArenaNotFound()
                    .replace("$arena", name));
            return;
        }

        File file = new File(MurderAPI.INSTANCE.getFileManager().getArenasDirectory(), name + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Check if person make a mistake.
        if(max > min) {
            config.set("players.max", max);
            config.set("players.min", min);
        } else {
            config.set("players.min", max);
            config.set("players.max", min);
        }

        FileBuilder.save(file, config);

        MessageUtil.sendMessage(player, "File for arena " + name + " has been updated!");
    }
}