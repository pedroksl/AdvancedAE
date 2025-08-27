package net.pedroksl.advanced_ae.api;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;

import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.storage.ISubMenuHost;

public interface IDirectionalOutputHost extends ISubMenuHost {

    BlockOrientation getOrientation();

    BlockPos getBlockPos();

    EnumSet<RelativeSide> getAllowedOutputs();

    void updateOutputSides(EnumSet<RelativeSide> sides);
}
