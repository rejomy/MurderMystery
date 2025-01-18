package me.rejomy.murder.util;

import lombok.experimental.UtilityClass;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.data.PlayerData;
import me.rejomy.murder.task.item.BowTask;
import me.rejomy.murder.task.item.SwordTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

@UtilityClass
public class FlyingItemUtil {

    /**
     * Shoot sword from player position to forward.
     * @param player is murder.
     * @param data is murder data.
     * @param match is current player match.
     */
    public void shootSword(Player player, PlayerData data, Match match) {
        Location location = player.getLocation();

        player.getInventory().setItemInHand(new ItemStack(Material.AIR));

        data.swordKnockDelay = MurderAPI.INSTANCE.getFileManager().getConfig().getShootSwordDelay();

        // Create and rotate invisible armor stand as "flying sword"
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        armorStand.setGravity(false);
        armorStand.setRemoveWhenFarAway(true);

        armorStand.setItemInHand(new ItemStack(MurderAPI.INSTANCE.getDataManager().get(player).swordSkin));
        armorStand.setVisible(false);

        armorStand.setRightArmPose(new EulerAngle(Math.toRadians(350), Math.toRadians(location.getPitch() * -1),
                Math.toRadians(90)));
        //

        Location startLocation = armorStand.getLocation().clone();

        Vector vector = startLocation.getDirection().normalize().multiply(
                MurderAPI.INSTANCE.getFileManager().getConfig().getSwordFlyingSpeed());

        SwordTask swordTask = new SwordTask(match, armorStand, data, player, vector);
        swordTask.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(MurderAPI.INSTANCE.getPlugin(), swordTask, 3, 3));
    }

    public static void spawnFlyingBow(Player deathDetective, Match match) {
        Location location = deathDetective.getLocation();
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        armorStand.setGravity(false);
        armorStand.setRemoveWhenFarAway(true);

        armorStand.setRightArmPose(new EulerAngle(Math.toRadians(350), Math.toRadians(location.getPitch() * -1),
                Math.toRadians(90)));

        armorStand.setItemInHand(new ItemStack(Material.BOW));

        armorStand.setVisible(false);

        BowTask bowTask = new BowTask(match, armorStand);
        bowTask.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(MurderAPI.INSTANCE.getPlugin(), bowTask, 3, 3));
    }
}
