package me.rejomy.murder.manager;

import lombok.Getter;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.file.impl.Config;
import me.rejomy.murder.file.impl.ItemsFile;
import me.rejomy.murder.file.impl.LobbyFile;
import me.rejomy.murder.file.impl.Message;

import java.io.File;

@Getter
public class FileManager {

    private final ItemsFile itemsFile;
    private final Config config;
    private final Message message;
    private final File arenasDirectory;
    private final LobbyFile lobbyFile;

    public FileManager() {
        config = new Config();
        message = new Message();
        itemsFile = new ItemsFile();
        lobbyFile = new LobbyFile();

        File folder = MurderAPI.INSTANCE.getPlugin().getDataFolder();

        arenasDirectory = new File(folder, "arenas");
        arenasDirectory.mkdirs();
    }

    public void reload() {
        config.reload();
        message.reload();
        lobbyFile.reload();
    }
}
