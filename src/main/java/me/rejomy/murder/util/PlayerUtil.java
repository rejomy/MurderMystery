package me.rejomy.murder.util;

import lombok.experimental.UtilityClass;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@UtilityClass
public class PlayerUtil {

    public void clearEffects(Player player) {
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
    }

    public void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }

    public boolean isNPC(Entity entity) {
        return entity.hasMetadata("NPC");
    }
}
