package net.pedroksl.advanced_ae.common.items.armors;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

public class QuantumLeggings extends QuantumArmorBase {

    private static final double MAX_POWER_STORAGE = 250000000;

    public QuantumLeggings(Properties properties) {
        super(AAEMaterials.QUANTUM_ALLOY.material(), Type.LEGGINGS, properties, () -> MAX_POWER_STORAGE);

        registerUpgrades(
                UpgradeType.WALK_SPEED,
                UpgradeType.SPRINT_SPEED,
                UpgradeType.SWIM_SPEED,
                UpgradeType.REACH,
                UpgradeType.CHARGING,
                UpgradeType.CAMO);
    }

    @Override
    public void tick(ItemStack stack, Level level, Entity entity, int slotId) {
        if (slotId == Inventory.INVENTORY_SIZE + EquipmentSlot.LEGS.getIndex()
                && !getPassiveUpgrades(stack).isEmpty()
                && entity instanceof Player player) {
            tickUpgrades(level, player, stack);
        }
    }
}
