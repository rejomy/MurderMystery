package me.rejomy.murder.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDamageEvent;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityStatus;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@UtilityClass
public class PEUtil {

    boolean isPE = Bukkit.getServer().getPluginManager().isPluginEnabled("PacketEvents");

    public void sendDamage(Player player, double amount) {
        if (isPE) {
            // Send damage effect only to the player who died.
            WrapperPlayServerEntityStatus statusPacket = new WrapperPlayServerEntityStatus(player.getEntityId(), 2);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, statusPacket);
        } else {
            // Send damage effect for all entity nearby.
            player.damage(amount);
        }
    }

    public void hideName(Player player) {

    }

}
