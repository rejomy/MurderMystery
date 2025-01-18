package me.rejomy.murder.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Locale;

public class CommandUtil {

    public static void runCommand(Player player, String command) {
        if(command.toLowerCase(Locale.ENGLISH).contains("[player]")) {
            command = command.replaceAll("\\[player](\\s)?", "");
            player.chat("/" + command);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("$player", player.getName()));
        }
    }

}
