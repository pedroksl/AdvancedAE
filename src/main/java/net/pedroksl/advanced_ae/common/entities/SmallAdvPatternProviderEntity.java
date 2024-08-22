package net.pedroksl.advanced_ae.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;

import appeng.api.stacks.AEItemKey;

public class SmallAdvPatternProviderEntity extends AdvPatternProviderEntity {

    public SmallAdvPatternProviderEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState, 9);
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(AAEBlocks.SMALL_ADV_PATTERN_PROVIDER);
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AAEBlocks.SMALL_ADV_PATTERN_PROVIDER);
    }
}
