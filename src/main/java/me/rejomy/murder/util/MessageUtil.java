package me.rejomy.murder.util;

import org.bukkit.entity.Player;

import java.util.List;

public class MessageUtil {

    /**
     * Send message to the selected player.
     * @param player Player who should receive this message.
     * @param message Current message.
     * @param replacers - first value is target, second is replacement. Dont use $ on target, every target will replace with $.
     */
    public static void sendMessage(Player player, String message, Object... replacers) {
        if(message.isEmpty()) {
            return;
        }

        // Do replacers
        if (replacers.length > 1) {
            // This work with principe first element is target, second is replacement.
            for (int index = 1; index < replacers.length; index++) {
                String target = "$" + replacers[index - 1];
                String replacement = String.valueOf(replacers[index]);

                message = message.replace(target, replacement);
            }
        }

        player.sendMessage(message);
    }

    public static void sendMessage(Player player, List<String> message, Object... replacers) {
        for(String line : message) {
            sendMessage(player, line, replacers);
        }
    }
}
