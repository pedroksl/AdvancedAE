package net.pedroksl.advanced_ae.common.items.armors;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

public class QuantumBoots extends QuantumArmorBase {

    private static final double MAX_POWER_STORAGE = 75000;

    public QuantumBoots(Properties properties) {
        super(AAEMaterials.QUANTUM_ALLOY.holder(), Type.BOOTS, properties, () -> MAX_POWER_STORAGE);

        this.possibleUpgrades.add(UpgradeType.STEP_ASSIST);
        this.possibleUpgrades.add(UpgradeType.JUMP_HEIGHT);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (slotId == Inventory.INVENTORY_SIZE + EquipmentSlot.FEET.getIndex()
                && !getPassiveTickAbilities(stack).isEmpty()
                && entity instanceof Player player) {
            tickUpgrades(level, player, stack);
        }
    }
}
