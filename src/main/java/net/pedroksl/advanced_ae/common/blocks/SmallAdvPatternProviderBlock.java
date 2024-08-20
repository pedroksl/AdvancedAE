package net.pedroksl.advanced_ae.common.blocks;

import appeng.block.crafting.PatternProviderBlock;
import appeng.block.crafting.PushDirection;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import appeng.util.Platform;
import com.glodblock.github.extendedae.common.blocks.BlockBaseGui;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.pedroksl.advanced_ae.common.entities.SmallAdvPatternProviderEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static appeng.block.crafting.PatternProviderBlock.PUSH_DIRECTION;
import static net.pedroksl.advanced_ae.common.blocks.AdvPatternProviderBlock.CONNECTION_STATE;

public class SmallAdvPatternProviderBlock extends BlockBaseGui<SmallAdvPatternProviderEntity> {
	public SmallAdvPatternProviderBlock() {
		super(metalProps());
		this.registerDefaultState(this.defaultBlockState()
				.setValue(PatternProviderBlock.PUSH_DIRECTION, PushDirection.ALL)
				.setValue(AdvPatternProviderBlock.CONNECTION_STATE, false)
		);
	}

	@Override
	public void openGui(SmallAdvPatternProviderEntity advPatternProviderEntity, Player player) {
		advPatternProviderEntity.openMenu(player, MenuLocators.forBlockEntity(advPatternProviderEntity));
	}

	@Override
	protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(PUSH_DIRECTION);
		builder.add(CONNECTION_STATE);
	}

	@Override
	public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
	                            @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
		var be = this.getBlockEntity(level, pos);
		if (be != null) {
			be.getLogic().updateRedstoneState();
		}
	}

	@Override
	public ItemInteractionResult check(SmallAdvPatternProviderEntity tile, ItemStack stack, Level world, BlockPos pos, BlockHitResult hit, Player p) {
		if (stack != null && InteractionUtil.canWrenchRotate(stack)) {
			this.setSide(world, pos, hit.getDirection());
			return ItemInteractionResult.sidedSuccess(world.isClientSide);
		}
		return null;
	}

	public void setSide(Level level, BlockPos pos, Direction facing) {
		var currentState = level.getBlockState(pos);
		var pushSide = currentState.getValue(PUSH_DIRECTION).getDirection();

		PushDirection newPushDirection;
		if (pushSide == facing.getOpposite()) {
			newPushDirection = PushDirection.fromDirection(facing);
		} else if (pushSide == facing) {
			newPushDirection = PushDirection.ALL;
		} else if (pushSide == null) {
			newPushDirection = PushDirection.fromDirection(facing.getOpposite());
		} else {
			newPushDirection = PushDirection.fromDirection(Platform.rotateAround(pushSide, facing));
		}

		level.setBlockAndUpdate(pos, currentState.setValue(PUSH_DIRECTION, newPushDirection));
	}
}
