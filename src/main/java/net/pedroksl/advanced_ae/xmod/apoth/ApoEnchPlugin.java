package net.pedroksl.advanced_ae.xmod.apoth;

import dev.shadowsoffire.apothic_enchanting.Ench;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class ApoEnchPlugin {

    public enum Enchantment {
        STABLE_FOOTING
    }

    public static DataComponentType<?> getEnchantment(Enchantment enchantment) {
        return switch (enchantment) {
            case STABLE_FOOTING -> Ench.EnchantEffects.STABLE_FOOTING;
        };
    }

    public static boolean checkForEnchant(Player player, Enchantment enchantment) {
        MutableBoolean flag = new MutableBoolean(false);
        EnchantmentHelper.runIterationOnEquipment(player, (ench, level, item) -> {
            if (ench.value().effects().has(getEnchantment(enchantment))) {
                flag.setTrue();
            }
        });
        return flag.getValue();
    }
}
