package net.pedroksl.advanced_ae.common.items.upgrades;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;

import appeng.core.localization.Tooltips;
import appeng.items.AEBaseItem;

public class QuantumUpgradeBaseItem extends AEBaseItem {
    private final UpgradeType type;

    public QuantumUpgradeBaseItem(Properties properties) {
        super(properties);
        this.type = UpgradeType.EMPTY;
    }

    public QuantumUpgradeBaseItem(UpgradeType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public UpgradeType getType() {
        return this.type;
    }

    @Override
    public void appendHoverText(
            ItemStack itemStack,
            TooltipContext context,
            TooltipDisplay display,
            Consumer<Component> lines,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, lines, tooltipFlag);
        if (type == UpgradeType.EMPTY) {
            lines.accept(type.getTooltip());
            return;
        }

        lines.accept(AAEText.QuantumUpgradeTooltip.text().withStyle(Tooltips.NUMBER_TEXT));
        lines.accept(type.getTooltip());
        List<QuantumArmorBase> list = QuantumArmorBase.upgradeAvailableFor(type);
        lines.accept(Component.empty());
        lines.accept(AAEText.UpgradeTooltip.text().withStyle(Tooltips.NORMAL_TOOLTIP_TEXT));
        for (var equip : list) {
            lines.accept(Component.translatable(equip.getDescriptionId()).withStyle(Tooltips.NORMAL_TOOLTIP_TEXT));
        }
    }
}
