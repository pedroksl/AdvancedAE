package net.pedroksl.advanced_ae.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import appeng.parts.automation.IOBusPart;

@Mixin(IOBusPart.class)
public interface MixinIOBusPartInvoker {

    @Invoker
    void callUpdateState();
}
