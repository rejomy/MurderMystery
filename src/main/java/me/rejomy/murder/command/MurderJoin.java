package me.rejomy.murder.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Arena;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.data.PlayerData;
import me.rejomy.murder.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("murder|murdermystery")
public class MurderJoin extends BaseCommand {

    @Subcommand("join")
    @CommandPermission("murder.join")
    public void on(Player player, String name) {
        if(!MurderAPI.INSTANCE.getArenaManager().hasArena(name)) {
            MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage()
                    .getArenaNotFound().replace("$arena", name));
            return;
        }

        Match match = MurderAPI.INSTANCE.getMatchManager().get(name);

        if(match == null) {
            MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage()
                    .getArenaNotFound().replace("$arena", "match:" + name));
            return;
        }

        if(match.getPlayers().containsKey(player)) {
            return;
        }

        MurderAPI.INSTANCE.getMatchManager().add(player, match);
    }

    @Subcommand("join")
    @CommandPermission("murder.join")
    public void on(Player player) {
        PlayerData data = MurderAPI.INSTANCE.getDataManager().get(player);

        if(data == null || data.currentMatch != null && !data.spectator) {
            MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getIsInMatch());
            return;
        }

        if (data.spectator) {
            if (data.currentMatch != null) {
                // Remove spectator from the match.
                data.currentMatch.remove(player);
            }
        }

        // At first get matches and sort this, after check if first match has
        List<Match> matches = MurderAPI.INSTANCE.getMatchManager().getMatches().stream()
                .filter(maybeMatch -> maybeMatch.getArena().status == Arena.Status.WAITING
                        || maybeMatch.getArena().status == Arena.Status.STARTING
                        && maybeMatch.getPlayers().size() < maybeMatch.getArena().maxPlayers)
                .sorted(Comparator.comparingInt(findMatch -> -findMatch.getPlayers().size()))
                .collect(Collectors.toList());

        // Check if matches is empty (not matches with our conditions)
        if (matches.isEmpty()) {
            MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage()
                    .getArenaNotFound().replace("$arena", "match:" + " random"));
            return;
        }

        /* Check if first matches has zero players
            I want to randomize first match, because after every restart we get matches with in order otherwise.
         */
        if (matches.get(0).getPlayers().isEmpty()) {
            Collections.shuffle(matches);
        }

        MurderAPI.INSTANCE.getMatchManager().add(player, matches.get(0));
    }
}