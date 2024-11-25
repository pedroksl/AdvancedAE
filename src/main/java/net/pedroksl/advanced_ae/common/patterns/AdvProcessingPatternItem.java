package net.pedroksl.advanced_ae.common.patterns;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.core.localization.GuiText;
import appeng.crafting.pattern.ProcessingPatternItem;

public class AdvProcessingPatternItem extends ProcessingPatternItem {
    public AdvProcessingPatternItem(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public AdvProcessingPattern decode(ItemStack stack, Level level, boolean tryRecovery) {
        return decode(AEItemKey.of(stack), level);
    }

    @Override
    public AdvProcessingPattern decode(AEItemKey what, Level level) {
        if (what == null || !what.hasTag()) {
            return null;
        }

        try {
            return new AdvProcessingPattern(what);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag advancedTooltips) {
        if (!stack.hasTag()) {
            // This can be called very early to index tooltips for search. In those cases,
            // there is no encoded pattern present.
            return;
        }

        var details = decode(stack, level, false);
        if (details == null) {
            super.appendHoverText(stack, level, lines, advancedTooltips);
            return;
        }

        if (stack.hasCustomHoverName()) {
            stack.resetHoverName();
        }

        var in = details.getInputs();
        var out = details.getOutputs();

        var label = GuiText.Produces.text().copy().append(": ").withStyle(ChatFormatting.GRAY);
        var and = Component.literal(" ")
                .copy()
                .append(GuiText.And.text())
                .append(" ")
                .withStyle(ChatFormatting.GRAY);
        var with = GuiText.With.text().copy().append(": ").withStyle(ChatFormatting.GRAY);

        boolean first = true;
        for (var anOut : out) {
            if (anOut == null) {
                continue;
            }

            lines.add(Component.empty().append(first ? label : and).append(getStackComponent(anOut)));
            first = false;
        }

        first = true;
        for (var anIn : in) {
            if (anIn == null) {
                continue;
            }

            var primaryInputTemplate = anIn.getPossibleInputs()[0];
            var primaryInput =
                    new GenericStack(primaryInputTemplate.what(), primaryInputTemplate.amount() * anIn.getMultiplier());
            var inputDirection = details.getDirectionSideForInputKey(primaryInputTemplate.what());
            var dirText = inputDirection == null
                    ? ""
                    : " (" + inputDirection.toString().toUpperCase().charAt(0) + ")";
            lines.add(Component.empty()
                    .append(first ? with : and)
                    .append(getStackComponent(primaryInput))
                    .append(dirText));
            first = false;
        }
    }
}
