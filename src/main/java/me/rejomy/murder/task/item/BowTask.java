package me.rejomy.murder.task.item;

import lombok.Getter;
import lombok.Setter;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Arena;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.data.PlayerData;
import me.rejomy.murder.data.PlayerRole;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BowTask implements Runnable {

    private final Match match;
    private final ArmorStand armorStand;

    @Getter
    @Setter
    private int taskId;

    public BowTask(Match match, ArmorStand armorStand) {
        this.match = match;
        this.armorStand = armorStand;
    }

    @Override
    public void run() {
        if(match.getArena().status != Arena.Status.PLAYING) {
            armorStand.remove();
            Bukkit.getScheduler().cancelTask(taskId);
            return;
        }

        armorStand.getLocation().setYaw(armorStand.getLocation().getYaw() + 5);
        armorStand.getLeftArmPose().add(0.2, 0, 0.2);

        for(Player target : armorStand.getLocation().getWorld()
                .getNearbyEntities(armorStand.getLocation(), 2, 2, 2).stream()
                .filter(entity -> {
                    if (!(entity instanceof Player)) return false;
                    PlayerData data = match.getPlayers().get(entity);
                    return !data.spectator && data.role != PlayerRole.MURDER && data.role != PlayerRole.DETECTIVE;
                })
                .map(entity -> (Player) entity)
                .toList()) {
            target.getInventory().setItem(0, new ItemStack(Material.BOW));
            armorStand.remove();
            PlayerData data = MurderAPI.INSTANCE.getDataManager().get(target);
            data.infinityArrow = true;
            Bukkit.getScheduler().cancelTask(taskId);
            break;
        }
    }
}
