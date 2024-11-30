package net.pedroksl.advanced_ae.common.items.upgrades;

import appeng.core.localization.Tooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuantumUpgradeBaseItem extends Item {
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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, lines, tooltipFlag);
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
