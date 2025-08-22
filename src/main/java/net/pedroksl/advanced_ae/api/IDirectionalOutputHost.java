package net.pedroksl.advanced_ae.api;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.storage.ISubMenuHost;

public interface IDirectionalOutputHost extends ISubMenuHost {

    BlockOrientation getOrientation();

    BlockPos getBlockPos();

    Level getLevel();

    EnumSet<RelativeSide> getAllowedOutputs();

    void updateOutputSides(EnumSet<RelativeSide> sides);

    default ItemStack getAdjacentBlock(RelativeSide side) {
        var dir = getOrientation().getSide(side);
        BlockPos blockPos = getBlockPos().relative(dir);

        Level level = getLevel();
        if (level == null) {
            return null;
        }

        BlockState blockState = level.getBlockState(blockPos);
        if (!blockState.isAir()) {
            return blockState.getCloneItemStack(
                    new BlockHitResult(
                            blockPos.getCenter().relative(dir.getOpposite(), 0.5), dir.getOpposite(), blockPos, false),
                    level,
                    blockPos,
                    Minecraft.getInstance().player);
        } else {
            return ItemStack.EMPTY;
        }
    }
}
