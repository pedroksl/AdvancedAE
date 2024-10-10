package net.pedroksl.advanced_ae.common.items.upgrades;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;

public class UpgradeCards {
    public static boolean walkSpeed(Level level, Player player, ItemStack stack) {
        //        ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);
        //        boolean canBoostElytra = chestItem.getItem() instanceof ToggleableTool toggleableTool &&
        // toggleableTool.canUseAbilityAndDurability(itemStack, Ability.ELYTRA);
        //        boolean isNotFlying = player.fallDistance <= 0 && !player.isFallFlying();
        boolean shouldBoostFlight = true; // canBoostElytra || isNotFlying;
        if (!player.isSprinting() && shouldBoostFlight && player.zza > 0F && !player.isInWaterOrBubble()) {
            var upgrade = UpgradeType.WALK_SPEED;
            var value = upgrade.getSettings().multiplier
                    * stack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(upgrade), -1)
                    / 25f;
            if (value > 0) {
                if (!player.onGround()) value /= 4;
                player.moveRelative(value, new Vec3(0, 0, 1));
            }
        }
        return false;
    }

    public static boolean sprintSpeed(Level level, Player player, ItemStack stack) {
        if (player.isSprinting() && !player.isFallFlying() && player.zza > 0F && !player.isInWaterOrBubble()) {
            var upgrade = UpgradeType.SPRINT_SPEED;
            var value = upgrade.getSettings().multiplier
                    * stack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(upgrade), -1)
                    / 25f;
            if (value > 0) {
                if (!player.onGround()) value /= 4;
                player.moveRelative(value, new Vec3(0, 0, 1));
            }
        }
        return false;
    }

    public static boolean jumpHeight(Level level, Player player, ItemStack stack) {
        if (!player.isInWaterOrBubble() && !player.isFallFlying()) {
            var upgrade = UpgradeType.JUMP_HEIGHT;
            var value = upgrade.getSettings().multiplier
                    * stack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(upgrade), -1)
                    / 8f;
            if (value > 0) {
                player.moveRelative(value, new Vec3(0, 1, 0));
            }
        }
        return false;
    }
}
