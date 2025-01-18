package me.rejomy.murder.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.rejomy.murder.util.ArenaEditor;
import org.bukkit.entity.Player;

@CommandAlias("murder|murdermystery")
public class MurderArenaLobby extends BaseCommand {

    @Subcommand("arenaspawn")
    @CommandPermission("murder.edit")
    public void onSpawn(Player player, String name) {
        ArenaEditor.setArenaLobby(player, player.getLocation(), name);
    }
}
