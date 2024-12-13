package net.pedroksl.advanced_ae.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import appeng.blockentity.AEBaseBlockEntity;

public class BlockUpgradeItem extends Item {
    public BlockUpgradeItem(Properties pProperties) {
        super(pProperties);
    }

    protected void replaceTile(
            Level world, BlockPos pos, BlockEntity oldTile, BlockEntity newTile, BlockState newBlock) {
        CompoundTag contents = oldTile.serializeNBT();
        world.removeBlockEntity(pos);
        world.removeBlock(pos, false);
        world.setBlock(pos, newBlock, 3);
        world.setBlockEntity(newTile);
        newTile.deserializeNBT(contents);
        if (newTile instanceof AEBaseBlockEntity aeTile) {
            aeTile.markForUpdate();
        } else {
            newTile.setChanged();
        }
    }
}
