package net.pedroksl.advanced_ae.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import appeng.api.stacks.AEKey;
import appeng.parts.reporting.AbstractMonitorPart;

@Mixin(AbstractMonitorPart.class)
public interface MixinAbstractMonitorPartAccessor {

    @Accessor
    AEKey getConfiguredItem();

    @Accessor
    long getAmount();

    @Accessor
    boolean getCanCraft();

    @Accessor
    String getLastHumanReadableText();

    @Accessor
    boolean getIsLocked();
}
