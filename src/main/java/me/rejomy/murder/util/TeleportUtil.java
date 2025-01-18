package me.rejomy.murder.util;

import lombok.experimental.UtilityClass;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.file.impl.LobbyFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@UtilityClass
public class TeleportUtil {

    private final LobbyFile lobby = MurderAPI.INSTANCE.getFileManager().getLobbyFile();

    public void teleportToLobby(Player player) {
        teleport(player, lobby.getLobby());
    }

    public void teleport(Player player, Location location) {
        // Run task sync.
        player.teleport(location);
    }
}
