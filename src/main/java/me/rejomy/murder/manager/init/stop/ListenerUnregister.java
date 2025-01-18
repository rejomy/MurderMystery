package me.rejomy.murder.manager.init.stop;

import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.manager.init.Initable;
import me.rejomy.murder.util.Logger;
import org.bukkit.event.HandlerList;

public class ListenerUnregister implements Initable {
    @Override
    public void start() {
        Logger.info("Unregistering all listeners...");
        HandlerList.unregisterAll(MurderAPI.INSTANCE.getPlugin());
    }
}
