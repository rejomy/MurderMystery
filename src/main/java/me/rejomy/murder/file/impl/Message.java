package me.rejomy.murder.file.impl;

import lombok.Getter;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.file.Reloadable;
import me.rejomy.murder.util.ColorUtil;
import me.rejomy.murder.util.Logger;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Getter
public class Message implements Reloadable {

    private String prefix;
    private String arenaNotFound, alreadyStart, join, leave, arenaIsFull;
    private String reloadMessage, arenaAlreadyExists, arenaCreate;
    private String entityDead, entityWasKilled;
    private String waitingStop;
    private String isInMatch;

    private List<String> completionTimeEnd, completionMurderDeath, completionMurderWin,
            innocentRole, murderRole, detectiveRole, startDefaultMessage;

    HashMap<Integer, HashMap<String, String>> waitingSection = new HashMap<>();

    public Message() {
        // If file not found in plugin directory, we load the file from jar.
        if(!new File(MurderAPI.INSTANCE.getPlugin().getDataFolder(), "message.yml").exists()) {
            MurderAPI.INSTANCE.getPlugin().saveResource("message.yml", false);
        }

        // Load values from file into the class.
        reload();
    }

    @Override
    public void reload() {
        Logger.info("Loading values from message.yml...");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(
                MurderAPI.INSTANCE.getPlugin().getDataFolder(), "message.yml"));

        prefix = config.getString("prefix");

        arenaNotFound = getStringFromConfig(config, "not-found");
        alreadyStart = getStringFromConfig(config, "already-started");
        join = getStringFromConfig(config, "join");
        leave = getStringFromConfig(config, "leave");
        arenaIsFull = getStringFromConfig(config, "arena-is-full");
        waitingStop = getStringFromConfig(config, "match-waiting-stop");

        isInMatch = getStringFromConfig(config, "is-in-match");
        reloadMessage = getStringFromConfig(config, "command.reload");
        arenaAlreadyExists = getStringFromConfig(config, "command.already-exists");
        arenaCreate = getStringFromConfig(config, "command.create");

        completionTimeEnd = getStringListFromConfig(config, "completion-options.time");
        completionMurderDeath = getStringListFromConfig(config, "completion-options.murder-death");
        completionMurderWin = getStringListFromConfig(config, "completion-options.murder-win");

        innocentRole = getStringListFromConfig(config, "start.innocent");
        murderRole = getStringListFromConfig(config, "start.murder");
        detectiveRole = getStringListFromConfig(config, "start.detective");
        startDefaultMessage = getStringListFromConfig(config, "start.default");

        entityDead = getStringFromConfig(config, "dead-message");
        entityWasKilled = getStringFromConfig(config, "kill-message");

        fillWaitingSection(config);
    }

    private void fillWaitingSection(YamlConfiguration config) {
        for(String key : config.getConfigurationSection("waiting").getKeys(false)) {
            try {
                int delay = Integer.parseInt(key);

                HashMap<String, String> messages = new HashMap<>();

                for(String section : config.getConfigurationSection("waiting." + key).getKeys(false)) {
                    messages.put(section, getStringFromConfig(config, "waiting." + key + "." + section));
                }

                waitingSection.put(delay, messages);
            } catch (NumberFormatException exception) {
                continue;
            }
        }
    }

    private String getStringFromConfig(YamlConfiguration config, String path) {
        return ColorUtil.toColor(config.getString(path).replace("$prefix", config.getString("prefix")));
    }

    private List<String> getStringListFromConfig(YamlConfiguration config, String path) {
        if(!(config.get(path) instanceof List)) {
            return Collections.singletonList(getStringFromConfig(config, path));
        }

        List<String> lines = config.getStringList(path);
        lines.replaceAll(line -> {
            line = line.replace("$prefix", config.getString("prefix"));
            line = ColorUtil.toColor(line);
            return line;
        });

        return lines;
    }
}
