package me.rejomy.murder.command;

import me.rejomy.murder.MurderAPI;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.rejomy.murder.util.MessageUtil;
import org.bukkit.entity.Player;

@CommandAlias("murder|murdermystery")
public class MurderReload extends BaseCommand {
    @Subcommand("reload")
    @CommandPermission("murder.reload")
    public void onReload(Player player) {
        MurderAPI.INSTANCE.getFileManager().reload();

        MurderAPI.INSTANCE.getMatchManager().stop();
        MurderAPI.INSTANCE.getArenaManager().loadArenas();
        MurderAPI.INSTANCE.getMatchManager().createMatches();

        MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getReloadMessage());
    }
}
