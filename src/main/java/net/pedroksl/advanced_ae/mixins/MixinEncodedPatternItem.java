package net.pedroksl.advanced_ae.mixins;

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPattern;

import appeng.api.crafting.*;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.core.AppEng;
import appeng.core.localization.GuiText;
import appeng.crafting.pattern.EncodedPatternItem;

@Mixin(value = EncodedPatternItem.class, remap = false)
public class MixinEncodedPatternItem<T extends IPatternDetails> {

    @Final
    @Shadow
    private EncodedPatternDecoder<T> decoder;

    @Final
    @Nullable
    private InvalidPatternTooltipStrategy invalidPatternTooltip;

    @Shadow
    protected static Component getTooltipEntryLine(GenericStack stack) {
        return Component.empty();
    }

    @Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true)
    public void onHoverText(
            ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flags, CallbackInfo ci) {
        var what = AEItemKey.of(stack);
        if (what == null) {
            // This can be called very early to index tooltips for search. In those cases,
            // there is no encoded pattern present.
            ci.cancel();
        }

        var clientLevel = AppEng.instance().getClientLevel();
        if (clientLevel == null) {
            ci.cancel(); // Showing pattern details will only work reliably client-side
        }

        PatternDetailsTooltip tooltip;
        try {
            var details = Objects.requireNonNull(decoder.decode(what, clientLevel), "decoder returned null");

            if (!(details instanceof AdvProcessingPattern)) {
                return;
            }

            tooltip = details.getTooltip(clientLevel, flags);
        } catch (Exception e) {
            lines.add(GuiText.InvalidPattern.text().copy().withStyle(ChatFormatting.RED));
            if (invalidPatternTooltip != null) {
                tooltip = invalidPatternTooltip.getTooltip(stack, clientLevel, e, flags);
            } else {
                tooltip = null;
            }
        }

        if (tooltip != null) {
            var label = Component.empty()
                    .append(tooltip.getOutputMethod())
                    .append(": ")
                    .withStyle(ChatFormatting.GRAY);
            var and = Component.literal(" ")
                    .append(GuiText.And.text())
                    .append(" ")
                    .withStyle(ChatFormatting.GRAY);
            var with = GuiText.With.text().copy().append(": ").withStyle(ChatFormatting.GRAY);

            boolean first = true;
            for (var output : tooltip.getOutputs()) {
                lines.add(Component.empty().append(first ? label : and).append(getTooltipEntryLine(output)));
                first = false;
            }

            first = true;
            for (var input : tooltip.getInputs()) {
                var details = ((AdvProcessingPattern) decoder.decode(what, clientLevel));
                var inputDirection = details.getDirectionSideForInputKey(input.what());
                var dirText = inputDirection == null
                        ? ""
                        : " (" + inputDirection.toString().toUpperCase().charAt(0) + ")";
                lines.add(Component.empty()
                        .append(first ? with : and)
                        .append(getTooltipEntryLine(input))
                        .append(dirText));
                first = false;
            }

            for (var property : tooltip.getProperties()) {
                if (property.value() != null) {
                    lines.add(Component.empty()
                            .append(property.name())
                            .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                            .append(property.value()));
                } else {
                    lines.add(Component.empty().withStyle(ChatFormatting.GRAY).append(property.name()));
                }
            }
        }
        ci.cancel();
    }
}
