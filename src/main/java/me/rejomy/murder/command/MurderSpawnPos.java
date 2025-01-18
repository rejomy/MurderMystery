package me.rejomy.murder.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.rejomy.murder.util.ArenaEditor;
import org.bukkit.entity.Player;

@CommandAlias("murder|murdermystery")
public class MurderSpawnPos extends BaseCommand {

    @Subcommand("spawn")
    @CommandPermission("murder.edit")
    public void onSpawn(Player player, String name) {
        ArenaEditor.addSpawnPos(player, player.getLocation(), name);
    }
}