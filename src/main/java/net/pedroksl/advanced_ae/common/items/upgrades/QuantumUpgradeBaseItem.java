package net.pedroksl.advanced_ae.common.items.upgrades;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
            ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, lines, tooltipFlag);
        if (type == UpgradeType.EMPTY) {
            lines.add(type.getTooltip());
            return;
        }

        lines.add(AAEText.QuantumUpgradeTooltip.text().withStyle(Tooltips.NUMBER_TEXT));
        lines.add(type.getTooltip());
        List<QuantumArmorBase> list = QuantumArmorBase.upgradeAvailableFor(type);
        lines.add(Component.empty());
        lines.add(AAEText.UpgradeTooltip.text().withStyle(Tooltips.NORMAL_TOOLTIP_TEXT));
        for (var equip : list) {
            lines.add(Component.translatable(equip.getDescriptionId()).withStyle(Tooltips.NORMAL_TOOLTIP_TEXT));
        }
    }
}
