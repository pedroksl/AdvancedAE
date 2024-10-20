package net.pedroksl.advanced_ae.common.items.armors;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import software.bernie.geckolib.animatable.GeoItem;

public class QuantumChestplate extends QuantumArmorBase implements GeoItem {

    private static final double MAX_POWER_STORAGE = 150000;

    public QuantumChestplate(Properties properties) {
        super(AAEMaterials.QUANTUM_ALLOY.holder(), Type.CHESTPLATE, properties, () -> MAX_POWER_STORAGE);

        this.possibleUpgrades.add(UpgradeType.FLIGHT);
        this.possibleUpgrades.add(UpgradeType.HP_BUFFER);
        this.possibleUpgrades.add(UpgradeType.LAVA_IMMUNITY);
        this.possibleUpgrades.add(UpgradeType.REGENERATION);
        this.possibleUpgrades.add(UpgradeType.STRENGTH);
        this.possibleUpgrades.add(UpgradeType.ATTACK_SPEED);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (slotId == Inventory.INVENTORY_SIZE + EquipmentSlot.CHEST.getIndex()
                && !getPassiveUpgrades(stack).isEmpty()
                && entity instanceof Player player) {
            tickUpgrades(level, player, stack);
        }
    }
}
