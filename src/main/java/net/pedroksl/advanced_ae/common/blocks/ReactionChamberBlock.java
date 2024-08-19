package net.pedroksl.advanced_ae.common.blocks;

import com.glodblock.github.extendedae.common.blocks.BlockBaseGui;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.pedroksl.advanced_ae.common.entities.ReactionChamberEntity;

import javax.annotation.Nonnull;

public class ReactionChamberBlock extends BlockBaseGui<ReactionChamberEntity> {

	public ReactionChamberBlock() {
		super(metalProps().noOcclusion());
		this.registerDefaultState(this.defaultBlockState());
	}

	@Override
	protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
	}

	@Override
	public void openGui(ReactionChamberEntity reactionChamberEntity, Player player) {

	}
}
