package net.pedroksl.advanced_ae.xmod.apoth;

import net.minecraft.world.entity.player.Player;

import dev.shadowsoffire.apotheosis.ench.enchantments.StableFootingEnchant;

public class ApoEnchPlugin {

    public enum Enchantment {
        STABLE_FOOTING
    }

    public static boolean isSameAs(net.minecraft.world.item.enchantment.Enchantment enchantment, Enchantment ench) {
        return switch (ench) {
            case STABLE_FOOTING -> enchantment instanceof StableFootingEnchant;
        };
    }

    public static net.minecraft.world.item.enchantment.Enchantment getEnchantment(Enchantment enchantment) {
        return switch (enchantment) {
            case STABLE_FOOTING -> new StableFootingEnchant();
        };
    }

    public static boolean checkForEnchant(Player player, Enchantment enchantment) {
        var armor = player.getArmorSlots();
        for (var stack : armor) {
            if (stack.getEnchantmentLevel(getEnchantment(enchantment)) > 0) {
                return true;
            }
        }
        return false;
    }
}
