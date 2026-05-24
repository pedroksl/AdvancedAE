package net.pedroksl.advanced_ae.common.items.armors;

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
}
