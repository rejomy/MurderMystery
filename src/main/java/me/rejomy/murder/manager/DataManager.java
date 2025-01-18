package me.rejomy.murder.manager;

import lombok.Getter;
import me.rejomy.murder.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class DataManager {
    private final List<PlayerData> users = new ArrayList<>();

    public void add(PlayerData data) {
        if(users.stream().anyMatch(user -> user.uuid.equals(data.uuid))) {
            return;
        }

        users.add(data);
    }

    public void remove(UUID uuid) {
        users.removeIf(user -> user.uuid == uuid);
    }

    public PlayerData get(Player player) {
        return users.stream().filter(user -> user.uuid == player.getUniqueId()).findAny().orElse(null);
    }
}
