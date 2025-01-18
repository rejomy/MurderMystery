package me.rejomy.murder.manager;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import me.rejomy.murder.manager.init.Initable;
import me.rejomy.murder.manager.init.start.CommandRegister;
import me.rejomy.murder.manager.init.start.ListenerRegister;
import me.rejomy.murder.manager.init.stop.ListenerUnregister;

public class InitManager {
    ClassToInstanceMap<Initable> initializersOnStart;
    ClassToInstanceMap<Initable> initializersOnStop;

    public InitManager() {
        initializersOnStart = new ImmutableClassToInstanceMap.Builder<Initable>()
                .put(CommandRegister.class, new CommandRegister())
                .put(ListenerRegister.class, new ListenerRegister())
                .build();
        initializersOnStop = new ImmutableClassToInstanceMap.Builder<Initable>()
                .put(ListenerUnregister.class, new ListenerUnregister())
                .build();
    }

    public void start() {
        for(Initable initable : initializersOnStart.values()) {
            initable.start();
        }
    }

    public void stop() {
        for(Initable initable : initializersOnStop.values()) {
            initable.start();
        }
    }
}
