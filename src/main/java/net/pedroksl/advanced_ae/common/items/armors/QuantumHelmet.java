package net.pedroksl.advanced_ae.common.items.armors;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

public class QuantumHelmet extends QuantumArmorBase {

    private static final double MAX_POWER_STORAGE = 100000;

    public QuantumHelmet(Properties properties) {
        super(AAEMaterials.QUANTUM_ALLOY.holder(), Type.HELMET, properties, () -> MAX_POWER_STORAGE);

        this.possibleUpgrades.add(UpgradeType.WATER_BREATHING);
        this.possibleUpgrades.add(UpgradeType.AUTO_FEED);
        this.possibleUpgrades.add(UpgradeType.AUTO_STOCK);
        this.possibleUpgrades.add(UpgradeType.MAGNET);
        this.possibleUpgrades.add(UpgradeType.LUCK);
        this.possibleUpgrades.add(UpgradeType.NIGHT_VISION);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (slotId == Inventory.INVENTORY_SIZE + EquipmentSlot.HEAD.getIndex()
                && !getPassiveUpgrades(stack).isEmpty()
                && entity instanceof Player player) {
            tickUpgrades(level, player, stack);
        }
    }
}
