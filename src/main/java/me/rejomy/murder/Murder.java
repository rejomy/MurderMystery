package me.rejomy.murder;

import org.bukkit.plugin.java.JavaPlugin;

public class Murder extends JavaPlugin {
    @Override
    public void onLoad() {
        MurderAPI.INSTANCE.load(this);
    }

    @Override
    public void onEnable() {
        MurderAPI.INSTANCE.start(this);
    }

    @Override
    public void onDisable() {
        MurderAPI.INSTANCE.stop(this);
    }
}