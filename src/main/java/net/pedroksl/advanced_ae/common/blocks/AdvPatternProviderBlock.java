package net.pedroksl.advanced_ae.common.blocks;

import static appeng.block.crafting.PatternProviderBlock.PUSH_DIRECTION;

import javax.annotation.Nonnull;

import com.glodblock.github.extendedae.common.blocks.BlockBaseGui;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.pedroksl.advanced_ae.common.entities.AdvPatternProviderEntity;

import appeng.block.crafting.PatternProviderBlock;
import appeng.block.crafting.PushDirection;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import appeng.util.Platform;

public class AdvPatternProviderBlock extends BlockBaseGui<AdvPatternProviderEntity> {

    public static final BooleanProperty CONNECTION_STATE = BooleanProperty.create("connection_state");

    public AdvPatternProviderBlock() {
        super(metalProps());
        this.registerDefaultState(this.defaultBlockState()
                .setValue(PatternProviderBlock.PUSH_DIRECTION, PushDirection.ALL)
                .setValue(CONNECTION_STATE, false));
    }

    @Override
    public void openGui(AdvPatternProviderEntity advPatternProviderEntity, Player player) {
        advPatternProviderEntity.openMenu(player, MenuLocators.forBlockEntity(advPatternProviderEntity));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PUSH_DIRECTION);
        builder.add(CONNECTION_STATE);
    }

    @Override
    public void neighborChanged(
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Block block,
            @NotNull BlockPos fromPos,
            boolean isMoving) {
        var be = this.getBlockEntity(level, pos);
        if (be != null) {
            be.getLogic().updateRedstoneState();
        }
    }

    @Override
    public ItemInteractionResult check(
            AdvPatternProviderEntity tile, ItemStack stack, Level world, BlockPos pos, BlockHitResult hit, Player p) {
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
