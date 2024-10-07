package net.pedroksl.advanced_ae.common.items.upgrades;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class UpgradeCards {
    public static boolean walkSpeed(Level level, Player player, ItemStack stack) {
        return true;
    }

    public static boolean sprintSpeed(Level level, Player player, ItemStack stack) {
        return true;
    }

    public static boolean stepAssist(Level level, Player player, ItemStack stack) {
        return true;
    }

    public static boolean jumpHeight(Level level, Player player, ItemStack stack) {
        return true;
    }
}
