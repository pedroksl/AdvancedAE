package net.pedroksl.advanced_ae.common.helpers;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import appeng.api.implementations.items.IAEItemPowerStorage;

import mekanism.api.radiation.capability.IRadiationShielding;
import mekanism.common.capabilities.Capabilities;

public class RadiationShieldingProvider extends PoweredItemCapabilities implements IRadiationShielding {
    public RadiationShieldingProvider(ItemStack is, IAEItemPowerStorage item) {
        super(is, item);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == Capabilities.RADIATION_SHIELDING) return (LazyOptional<T>) LazyOptional.of(() -> this);
        return super.getCapability(capability, facing);
    }

    @Override
    public double getRadiationShielding() {
        return 0.25;
    }
}
