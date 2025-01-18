package me.rejomy.murder.manager.init.start;

import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.listener.*;
import me.rejomy.murder.manager.init.Initable;
import me.rejomy.murder.util.Logger;
import org.bukkit.Bukkit;

public class ListenerRegister implements Initable {

    @Override
    public void start() {
        Logger.info("Registering listeners...");

        Bukkit.getPluginManager().registerEvents(new BlockListener(), MurderAPI.INSTANCE.getPlugin());
        Bukkit.getPluginManager().registerEvents(new DamageListener(), MurderAPI.INSTANCE.getPlugin());
        Bukkit.getPluginManager().registerEvents(new SimpleListener(), MurderAPI.INSTANCE.getPlugin());
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), MurderAPI.INSTANCE.getPlugin());
        Bukkit.getPluginManager().registerEvents(new DeathListener(), MurderAPI.INSTANCE.getPlugin());
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), MurderAPI.INSTANCE.getPlugin());
        Bukkit.getPluginManager().registerEvents(new TeleportListener(), MurderAPI.INSTANCE.getPlugin());
        Bukkit.getPluginManager().registerEvents(new ShootBowListener(), MurderAPI.INSTANCE.getPlugin());
        Bukkit.getPluginManager().registerEvents(new InteractListener(), MurderAPI.INSTANCE.getPlugin());
    }
}
