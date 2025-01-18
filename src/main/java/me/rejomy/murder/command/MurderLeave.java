package me.rejomy.murder.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.util.MessageUtil;
import org.bukkit.entity.Player;

@CommandAlias("murder|murdermystery")
public class MurderLeave extends BaseCommand {

    @Subcommand("leave")
    @CommandPermission("murder.leave")
    public void on(Player player) {
        Match match = MurderAPI.INSTANCE.getMatchManager().get(player);

        if(match == null) {
            MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage()
                    .getArenaNotFound().replace("$arena", "You does not in match."));
            return;
        }

        match.remove(player);
    }
}