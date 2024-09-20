package net.pedroksl.advanced_ae.api;

import appeng.api.orientation.RelativeSide;
import appeng.api.storage.ISubMenuHost;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public interface IDirectionalOutputHost extends ISubMenuHost {

	EnumSet<RelativeSide> getAllowedOutputs();

	void updateOutputSides(EnumSet<RelativeSide> sides);

	ItemStack getAdjacentBlock(RelativeSide side);
}
