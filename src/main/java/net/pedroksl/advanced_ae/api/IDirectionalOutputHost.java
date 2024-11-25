package net.pedroksl.advanced_ae.api;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.storage.ISubMenuHost;
import appeng.blockentity.networking.CableBusBlockEntity;

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
        ItemStack itemStack = blockState.getBlock().asItem().getDefaultInstance();
        if (blockState.hasBlockEntity()) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity != null) {
                if (blockEntity instanceof CableBusBlockEntity cable) {
                    var part = cable.getPart(dir.getOpposite());
                    if (part != null) {
                        itemStack = new ItemStack(part.getPartItem().asItem(), 1);
                    }
                } else {
                    blockEntity.saveToItem(itemStack);
                }
            }
        }
        return itemStack;
    }
}
