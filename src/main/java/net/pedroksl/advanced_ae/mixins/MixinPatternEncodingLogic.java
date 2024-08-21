package net.pedroksl.advanced_ae.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPattern;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.helpers.IPatternTerminalLogicHost;
import appeng.parts.encoding.PatternEncodingLogic;

@Mixin(value = PatternEncodingLogic.class, remap = false)
public class MixinPatternEncodingLogic {

    @Final
    @Shadow
    private IPatternTerminalLogicHost host;

    @Shadow
    private void loadProcessingPattern(AEProcessingPattern pattern) {}

    @Inject(method = "loadEncodedPattern", at = @At(value = "INVOKE", ordinal = 7))
    protected void onLoadEncodedPattern(ItemStack pattern, CallbackInfo ci) {
        IPatternDetails details = PatternDetailsHelper.decodePattern(pattern, this.host.getLevel());
        if (details instanceof AdvProcessingPattern advPattern) {
            var aePattern = advPattern.getAEProcessingPattern(this.host.getLevel());
            if (aePattern != null) {
                loadProcessingPattern(aePattern);
            }
        }
    }
}
