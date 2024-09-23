package net.pedroksl.advanced_ae.api;

import java.util.EnumSet;

import net.minecraft.world.item.ItemStack;

import appeng.api.orientation.RelativeSide;
import appeng.api.storage.ISubMenuHost;

public interface IDirectionalOutputHost extends ISubMenuHost {

    EnumSet<RelativeSide> getAllowedOutputs();

    void updateOutputSides(EnumSet<RelativeSide> sides);

    ItemStack getAdjacentBlock(RelativeSide side);
}
