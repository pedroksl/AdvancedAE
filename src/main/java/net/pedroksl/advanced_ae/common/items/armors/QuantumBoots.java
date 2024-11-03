package net.pedroksl.advanced_ae.common.items.armors;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import appeng.core.localization.Tooltips;

public class QuantumBoots extends QuantumArmorBase {

    private static final double MAX_POWER_STORAGE = 200000000;

    public QuantumBoots(Properties properties) {
        super(AAEMaterials.QUANTUM_ALLOY.holder(), Type.BOOTS, properties, () -> MAX_POWER_STORAGE);

        registerUpgrades(
                UpgradeType.STEP_ASSIST,
                UpgradeType.JUMP_HEIGHT,
                UpgradeType.EVASION,
                UpgradeType.FLIGHT_DRIFT,
                UpgradeType.CHARGING);
    }

    @Override
    protected void appendExtraHoverText(
            ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag advancedTooltips) {
        lines.add(AAEText.QuantumArmorFallDamageTooltip.text().withStyle(Tooltips.NUMBER_TEXT));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (slotId == Inventory.INVENTORY_SIZE + EquipmentSlot.FEET.getIndex()
                && !getPassiveUpgrades(stack).isEmpty()
                && entity instanceof Player player) {
            tickUpgrades(level, player, stack);
        }
    }
}
