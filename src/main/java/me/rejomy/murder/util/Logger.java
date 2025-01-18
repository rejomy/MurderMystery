package me.rejomy.murder.util;

import me.rejomy.murder.MurderAPI;

public class Logger {
    public static void info(String message) {
        MurderAPI.INSTANCE.getPlugin().getLogger().info(message);
    }

    public static void warn(String message) {
        MurderAPI.INSTANCE.getPlugin().getLogger().warning(message);
    }

    public static void severe(String message) {
        MurderAPI.INSTANCE.getPlugin().getLogger().severe(message);
    }
}
