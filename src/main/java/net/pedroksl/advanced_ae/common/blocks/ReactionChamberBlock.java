package net.pedroksl.advanced_ae.common.blocks;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.entities.ReactionChamberEntity;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;

public class ReactionChamberBlock extends AEBaseEntityBlock<ReactionChamberEntity> {

    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    public ReactionChamberBlock(Properties p) {
        super(metalProps(p).noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(WORKING, false));
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof ReactionChamberEntity be) {
            if (!level.isClientSide()) {
                MenuOpener.open(AAEMenus.REACTION_CHAMBER.get(), player, MenuLocators.forBlockEntity(be));
            }
            return InteractionResult.SUCCESS;
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack heldItem,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit) {
        var handler = ItemAccess.forStack(heldItem).oneByOne().getCapability(Capabilities.Fluid.ITEM);
        if (FluidUtil.interactWithFluidHandler(player, hand, pos, handler)) {
            return InteractionResult.SUCCESS;
        }

        return super.useItemOn(heldItem, state, level, pos, player, hand, hit);
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WORKING);
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.horizontalFacing();
    }
}
