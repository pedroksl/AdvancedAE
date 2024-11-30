package net.pedroksl.advanced_ae.common.helpers;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import appeng.api.config.Actionable;
import appeng.api.config.PowerUnits;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.capabilities.Capabilities;

public class PoweredItemCapabilities implements ICapabilityProvider, IEnergyStorage {

	private final ItemStack is;

	private final IAEItemPowerStorage item;

	public PoweredItemCapabilities(ItemStack is, IAEItemPowerStorage item) {
		this.is = is;
		this.item = item;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == Capabilities.FORGE_ENERGY) {
			return (LazyOptional<T>) LazyOptional.of(() -> this);
		}
		return LazyOptional.empty();
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		final double convertedOffer = PowerUnits.FE.convertTo(PowerUnits.AE, maxReceive);
		final double overflow = this.item.injectAEPower(this.is, convertedOffer,
				simulate ? Actionable.SIMULATE : Actionable.MODULATE);

		return maxReceive - (int) PowerUnits.AE.convertTo(PowerUnits.FE, overflow);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored() {
		return (int) PowerUnits.AE.convertTo(PowerUnits.FE, this.item.getAECurrentPower(this.is));
	}

	@Override
	public int getMaxEnergyStored() {
		return (int) PowerUnits.AE.convertTo(PowerUnits.FE, this.item.getAEMaxPower(this.is));
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

}
