package net.pedroksl.advanced_ae.common.blocks;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.pedroksl.advanced_ae.common.entities.ReactionChamberEntity;

import appeng.block.AEBaseEntityBlock;

public class ReactionChamberBlock extends AEBaseEntityBlock<ReactionChamberEntity> {

    public ReactionChamberBlock() {
        super(metalProps().noOcclusion());
        this.registerDefaultState(this.defaultBlockState());
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }
}
