package net.pedroksl.advanced_ae.common.items.armors;

import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.ArmorType;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import appeng.core.localization.Tooltips;

public class QuantumBoots extends QuantumArmorBase {

    private static final double MAX_POWER_STORAGE = 200000000;

    public QuantumBoots(Properties properties) {
        super(ArmorType.BOOTS, properties, () -> MAX_POWER_STORAGE);

        registerUpgrades(
                UpgradeType.STEP_ASSIST,
                UpgradeType.JUMP_HEIGHT,
                UpgradeType.EVASION,
                UpgradeType.FLIGHT_DRIFT,
                UpgradeType.CHARGING,
                UpgradeType.CAMO);
    }

    @Override
    protected void appendExtraHoverText(
            ItemStack stack, TooltipContext context, Consumer<Component> lines, TooltipFlag advancedTooltips) {
        lines.accept(AAEText.QuantumArmorFallDamageTooltip.text().withStyle(Tooltips.NUMBER_TEXT));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);

        if (!getPassiveUpgrades(stack).isEmpty() && entity instanceof Player player) {
            tickUpgrades(level, player, stack);
        }
    }
}
