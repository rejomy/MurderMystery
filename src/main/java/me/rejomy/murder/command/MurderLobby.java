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
public class MurderLobby extends BaseCommand {

    @Subcommand("setlobby")
    @CommandPermission("murder.edit")
    public void on(Player player) {
        File file = new File(MurderAPI.INSTANCE.getPlugin().getDataFolder(), "lobby.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("world", player.getWorld().getName());
        config.set("x", player.getLocation().getX());
        config.set("y", player.getLocation().getY());
        config.set("z", player.getLocation().getZ());
        config.set("yaw", player.getLocation().getYaw());
        config.set("pitch", player.getLocation().getPitch());

        FileBuilder.save(file, config);

        MessageUtil.sendMessage(player, "Set lobby.");
    }
}