package me.rejomy.murder.file.impl;

import lombok.Getter;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.file.Reloadable;
import me.rejomy.murder.util.Logger;
import me.rejomy.murder.util.TimeUtil;
import me.rejomy.murder.util.value.BothIntValue;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

@Getter
public class Config implements Reloadable {

    int matchDelay, waitingDelay, minWaitingDelay, endDelay, compassDelay, goldSpawnDelay, goldSpawnAmount;
    int shootSwordDelay, returnArrowDelay;
    int defaultMinPlayers, defaultMaxPlayers;
    int bowGoldCosts;
    boolean giveItemWithRole; // Should we give player an item that goes with their role immediately?
    BothIntValue gameGiveBowAfter, gameGiveSwordAfter, gameGiveCompassAfter;
    BothIntValue roleMurder, roleDetective, roleInnocent; // How long before a player is given a role.

    float swordDamageArea, swordFlyingSpeed;

    // This value is select how much player should be player for create large than 1 detective and murder.
    int settingPlayerDependency;

    String defaultWorld;

    boolean cancelHunger, cancelBlockBreak, cancelBlockPlace, cancelMoveItemToOtherInventory;
    boolean spectators;
    boolean teleportFromWorld;
    boolean settingPlayerDependencyRandomize;

    GameMode gameMode;

    HashMap<String, String> damageValues = new HashMap<>();

    public Config() {
        MurderAPI.INSTANCE.getPlugin().saveDefaultConfig();

        reload();
    }

    @Override
    public void reload() {
        Logger.info("Loading values from config.yml...");
        FileConfiguration config = MurderAPI.INSTANCE.getPlugin().getConfig();

        defaultMaxPlayers = config.getInt("default-arena-settings.max-players");
        defaultMinPlayers = config.getInt("default-arena-settings.min-players");
        defaultWorld = config.getString("default-arena-settings.world");

        matchDelay = config.getInt("settings.time.match-delay");

        waitingDelay = config.getInt("settings.time.waiting-delay.time");
        minWaitingDelay = config.getInt("settings.time.waiting-delay.min-time");

        endDelay = config.getInt("settings.time.end-delay");
        compassDelay = config.getInt("settings.time.get-compass-delay");

        returnArrowDelay = config.getInt("settings.time.time-after-shoot");

        goldSpawnDelay = config.getInt("settings.time.gold.spawn-delay");
        goldSpawnAmount = config.getInt("settings.time.gold.amount");

        gameGiveBowAfter = getBothIntValueFromConfig("settings.time.item.give-bow-after", config);
        gameGiveSwordAfter = getBothIntValueFromConfig("settings.time.item.give-sword-after", config);
        gameGiveCompassAfter = getBothIntValueFromConfig("settings.time.item.give-compass-after", config);

        giveItemWithRole = config.getBoolean("settings.time.role.give-item-with-role");
        roleDetective = getBothIntValueFromConfig("settings.time.role.detective", config);
        roleMurder = getBothIntValueFromConfig("settings.time.role.murder", config);
        roleInnocent = getBothIntValueFromConfig("settings.time.role.innocent", config);

        shootSwordDelay = config.getInt("murder-sword-shoot-delay");
        swordFlyingSpeed = (float) config.getDouble("murder-sword-flying-speed");
        swordDamageArea = (float) config.getDouble("murder-sword-shoot-kill-distance");

        for(String value : config.getConfigurationSection("settings.game.damage").getKeys(false)) {
            damageValues.put(value, config.getString("settings.game.damage." + value));
        }

        settingPlayerDependency = config.getInt("settings.dependency");
        settingPlayerDependencyRandomize = config.getBoolean("settings.randomize");

        bowGoldCosts = config.getInt("settings.bow-gold-costs");

        teleportFromWorld = config.getBoolean("settings.game.auto-quit-when-world-change");

        cancelHunger = config.getBoolean("settings.game.actions.hunger");
        cancelBlockBreak = config.getBoolean("settings.game.actions.block-break");
        cancelBlockPlace = config.getBoolean("settings.game.actions.block-place");
        cancelMoveItemToOtherInventory = config.getBoolean("settings.game.actions.move-item-to-other-inventory");

        // Attempt to get game mode from config, if game mode has incorrect value
        //  - we shouldnt change his game mode.
        try {
            gameMode = GameMode.valueOf(config.getString("settings.game.gamemode"));
        } catch (Exception ignored) { }

        spectators = config.getBoolean("spectator.enable");
    }

    BothIntValue getBothIntValueFromConfig(String path, FileConfiguration config) {
        String[] string = config.getString(path).split("-");

        String first = string[0];
        String second = string.length == 2? string[1] : first;

        return new BothIntValue(TimeUtil.toSeconds(first), TimeUtil.toSeconds(second));
    }
}
