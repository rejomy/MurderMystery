package me.rejomy.murder.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.file.FileBuilder;
import me.rejomy.murder.util.ArenaEditor;
import me.rejomy.murder.util.MessageUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@CommandAlias("murder|murdermystery")
public class MurderGoldPos extends BaseCommand {

    @Subcommand("gold")
    @CommandPermission("murder.edit")
    public void onSpawn(Player player, String name) {
        ArenaEditor.addGoldSpawnPos(player, player.getLocation(), name);
    }
}