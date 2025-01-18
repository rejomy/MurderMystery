package me.rejomy.murder;

import lombok.Getter;
import me.rejomy.murder.database.DataBase;
import me.rejomy.murder.database.SQLite;
import me.rejomy.murder.manager.*;
import me.rejomy.murder.util.expansion.Expansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public enum MurderAPI {
    INSTANCE;

    private JavaPlugin plugin;

    private DataBase dataBase;

    private InitManager initManager;
    private FileManager fileManager;

    private ArenaManager arenaManager;
    private MatchManager matchManager;

    private DataManager dataManager;

    public void load(JavaPlugin plugin) {
        this.plugin = plugin;

        initManager = new InitManager();

        dataManager = new DataManager();
    }

    public void start(JavaPlugin plugin) {
        this.plugin = plugin;

        fileManager = new FileManager();

        arenaManager = new ArenaManager();
        matchManager = new MatchManager();

        dataBase = new SQLite();

        initManager.start();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Expansion().register();
        }
    }

    public void stop(JavaPlugin plugin) {
        this.plugin = plugin;

        initManager.stop();
    }
}
