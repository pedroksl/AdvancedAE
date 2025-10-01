package net.pedroksl.advanced_ae.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;

import appeng.block.AEBaseEntityBlock;
import appeng.block.crafting.ICraftingUnitType;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;

public abstract class AAEAbstractCraftingUnitBlock<T extends AdvCraftingBlockEntity> extends AEBaseEntityBlock<T> {
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final BooleanProperty MULTIBLOCKED = BooleanProperty.create("multiblocked");
    public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light_level", 0, 15);

    public final ICraftingUnitType type;

    public AAEAbstractCraftingUnitBlock(Properties props, ICraftingUnitType type) {
        super(props);
        this.type = type;
        this.registerDefaultState(defaultBlockState()
                .setValue(FORMED, false)
                .setValue(POWERED, false)
                .setValue(MULTIBLOCKED, false)
                .setValue(LIGHT_LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
        builder.add(FORMED);
        builder.add(MULTIBLOCKED);
        builder.add(LIGHT_LEVEL);
    }

    @Override
    public BlockState updateShape(
            BlockState stateIn,
            Direction facing,
            BlockState facingState,
            LevelAccessor level,
            BlockPos currentPos,
            BlockPos facingPos) {
        BlockEntity te = level.getBlockEntity(currentPos);
        if (te != null) {
            te.requestModelDataUpdate();
        }
        return super.updateShape(stateIn, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public void neighborChanged(
            BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        final AdvCraftingBlockEntity cp = this.getBlockEntity(level, pos);
        if (cp != null) {
            cp.updateMultiBlock(fromPos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() == state.getBlock()) {
            return; // Just a block state change
        }

        final AdvCraftingBlockEntity cp = this.getBlockEntity(level, pos);
        if (cp != null) {
            cp.breakCluster();
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof AdvCraftingBlockEntity be && be.isFormed() && be.isActive()) {
            if (!level.isClientSide()) {
                MenuOpener.open(AAEMenus.QUANTUM_COMPUTER.get(), player, MenuLocators.forBlockEntity(be));
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
