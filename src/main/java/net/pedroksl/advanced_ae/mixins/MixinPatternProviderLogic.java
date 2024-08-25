package net.pedroksl.advanced_ae.mixins;

import java.util.EnumSet;

import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.Direction;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;

import appeng.api.networking.IManagedGridNode;
import appeng.helpers.patternprovider.PatternProviderLogic;

@Mixin(value = PatternProviderLogic.class, remap = false)
public class MixinPatternProviderLogic {

    @Final
    @Shadow
    IManagedGridNode mainNode;

    @Inject(method = "getActiveSides", at = @At("TAIL"), cancellable = true)
    private void onGetActiveSides(CallbackInfoReturnable<EnumSet<Direction>> cir, @Local EnumSet<Direction> sides) {
        // Additionally skip sides with grid connections to advanced pattern providers
        var node = mainNode.getNode();
        if (node != null) {
            for (var entry : node.getInWorldConnections().entrySet()) {
                var otherNode = entry.getValue().getOtherSide(node);
                if (otherNode.getOwner() instanceof AdvPatternProviderLogicHost) {
                    sides.remove(entry.getKey());
                }
            }
        }

        cir.setReturnValue(sides);
    }
}
