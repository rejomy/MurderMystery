package me.rejomy.murder.manager.init.start;

import co.aikar.commands.PaperCommandManager;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.command.*;
import me.rejomy.murder.manager.init.Initable;

public class CommandRegister implements Initable {
    @Override
    public void start() {
        // This does not make Grim require paper
        // It only enables new features such as asynchronous tab completion on paper
        PaperCommandManager commandManager = new PaperCommandManager(MurderAPI.INSTANCE.getPlugin());

        commandManager.registerCommand(new MurderReload());
        commandManager.registerCommand(new MurderPlayers());
        commandManager.registerCommand(new MurderWorld());
        commandManager.registerCommand(new MurderGoldPos());
        commandManager.registerCommand(new MurderSpawnPos());
        commandManager.registerCommand(new MurderCreate());
        commandManager.registerCommand(new MurderLeave());
        commandManager.registerCommand(new MurderJoin());
        commandManager.registerCommand(new MurderArenaLobby());
        commandManager.registerCommand(new MurderLobby());
    }
}
