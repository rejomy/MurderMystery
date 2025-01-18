package me.rejomy.murder.listener;

import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData data = MurderAPI.INSTANCE.getDataBase().loadDataFromDataBase(player.getUniqueId());

        if (data == null) data = new PlayerData(player.getUniqueId());

        MurderAPI.INSTANCE.getDataManager().add(data);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        MurderAPI.INSTANCE.getDataBase().savePlayerData(MurderAPI.INSTANCE.getDataManager().get(player.getPlayer()));
        MurderAPI.INSTANCE.getDataManager().remove(player.getUniqueId());

        Match match = MurderAPI.INSTANCE.getMatchManager().get(player);

        if(match == null) {
            return;
        }

        match.remove(player);
    }
}
