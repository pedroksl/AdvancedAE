package net.pedroksl.advanced_ae.common.items.armors;

import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

public class QuantumLeggings extends QuantumArmorBase {

    private static final double MAX_POWER_STORAGE = 250000000;

    public QuantumLeggings(Properties properties) {
        super(ArmorType.LEGGINGS, properties, () -> MAX_POWER_STORAGE);

        registerUpgrades(
                UpgradeType.WALK_SPEED,
                UpgradeType.SPRINT_SPEED,
                UpgradeType.SWIM_SPEED,
                UpgradeType.REACH,
                UpgradeType.CHARGING,
                UpgradeType.CAMO);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);

        if (!getPassiveUpgrades(stack).isEmpty() && entity instanceof Player player) {
            tickUpgrades(level, player, stack);
        }
    }
}
