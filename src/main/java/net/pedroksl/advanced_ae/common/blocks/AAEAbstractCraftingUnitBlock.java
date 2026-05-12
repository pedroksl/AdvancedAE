package net.pedroksl.advanced_ae.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
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
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te != null) {
            te.requestModelDataUpdate();
        }
        var cp = this.getBlockEntity(level, pos);
        if (cp != null) {
            cp.updateMultiBlock(neighborPos);
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof AdvCraftingBlockEntity be && be.isFormed() && be.isActive()) {
            if (!level.isClientSide()) {
                MenuOpener.open(AAEMenus.QUANTUM_COMPUTER.get(), player, MenuLocators.forBlockEntity(be));
            }

            return InteractionResult.SUCCESS;
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
