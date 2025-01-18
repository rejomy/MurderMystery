package me.rejomy.murder.task.item;

import lombok.Getter;
import lombok.Setter;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Arena;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.data.PlayerData;
import me.rejomy.murder.util.FlyingItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public class SwordTask implements Runnable {

    private final Match match;
    private final ArmorStand armorStand;
    private Player player;
    private final PlayerData shooterData;
    private final Vector vector;

    @Setter
    @Getter
    private int taskId;
    private int swordTicksLived;

    public SwordTask(Match match, ArmorStand armorStand, PlayerData shooterData, Player player, Vector vector) {
        this.match = match;
        this.armorStand = armorStand;
        this.shooterData = shooterData;
        this.vector = vector;
        this.player = player;

        swordTicksLived = shooterData.swordKnockDelay * 20;
    }

    @Override
    public void run() {
        if(match.getArena().status != Arena.Status.PLAYING) {
            armorStand.remove();
            Bukkit.getScheduler().cancelTask(taskId);
            shooterData.swordKnockDelay = 0;
            return;
        }

        swordTicksLived -= 5;

        if (swordTicksLived <= 0) {
            armorStand.remove();
            Bukkit.getScheduler().cancelTask(taskId);
            shooterData.swordKnockDelay = 0;

            player.getInventory().setHeldItemSlot(1);
            player.getInventory().setItem(0, new ItemStack(shooterData.swordSkin));
            return;
        }

        armorStand.teleport(armorStand.getLocation().add(vector));

        Location swordLocation = armorStand.getLocation().clone().add(0, armorStand.getEyeHeight() / 2, 0);

        if (swordLocation.getBlock().getType().isSolid()) {
            swordTicksLived = 0;
        }

        float distance = MurderAPI.INSTANCE.getFileManager().getConfig().getSwordDamageArea();

        List<Player> entities = armorStand.getNearbyEntities(distance, distance, distance).stream()
                .filter(Player.class::isInstance)
                .map(entity -> (Player) entity)
                .filter(target -> target != player && !match.getPlayers().get(target).spectator)
                .toList();

        for(Player target : entities) {
            if(match.isDetective(target)) {
                FlyingItemUtil.spawnFlyingBow(target, match);
                match.kill(target);
            } else if(match.isMurder(target)) {
                target.damage(6);
            } else {
                match.kill(target);
            }
        }
    }

}
