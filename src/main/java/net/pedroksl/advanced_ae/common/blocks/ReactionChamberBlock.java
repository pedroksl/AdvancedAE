package net.pedroksl.advanced_ae.common.blocks;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.entities.ReactionChamberEntity;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;

public class ReactionChamberBlock extends AEBaseEntityBlock<ReactionChamberEntity> {

    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    public ReactionChamberBlock() {
        super(metalProps().noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(WORKING, false));
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof ReactionChamberEntity be) {
            if (!level.isClientSide()) {
                MenuOpener.open(AAEMenus.REACTION_CHAMBER.get(), player, MenuLocators.forBlockEntity(be));
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack heldItem,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit) {
        if (heldItem.getItem() instanceof BucketItem bucket) {
            var didSomething = useBucket(player, level, pos, heldItem, hand);
            if (didSomething) {
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return super.useItemOn(heldItem, state, level, pos, player, hand, hit);
    }

    public boolean useBucket(Player player, Level level, BlockPos pos, ItemStack stack, InteractionHand hand) {
        boolean didSomething = false;

        var cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
        var blockCap = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
        if (cap == null || blockCap == null) return didSomething;

        // Take from output
        if (cap.getFluidInTank(0).isEmpty()) {
            var extracted = blockCap.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
            if (!extracted.isEmpty() && extracted.getAmount() == FluidType.BUCKET_VOLUME) {
                blockCap.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);

                if (cap.getContainer().getCount() == 1) {
                    cap.fill(extracted, IFluidHandler.FluidAction.EXECUTE);
                    player.setItemInHand(hand, cap.getContainer());
                } else {
                    var newBucket = new ItemStack(Items.BUCKET, 1);
                    var newCap = newBucket.getCapability(Capabilities.FluidHandler.ITEM);
                    if (newCap != null) {
                        newCap.fill(extracted, IFluidHandler.FluidAction.EXECUTE);
                        player.setItemInHand(hand, newCap.getContainer());
                        player.addItem(new ItemStack(stack.getItem(), stack.getCount() - 1));
                    }
                }

                SoundEvent soundevent = extracted
                        .getFluidType()
                        .getSound(player, level, pos, net.neoforged.neoforge.common.SoundActions.BUCKET_EMPTY);
                if (soundevent == null)
                    soundevent = extracted.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
                level.playSound(player, pos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);

                didSomething = true;
            }
        }
        // Insert into input
        else {
            var extracted = cap.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
            var inserted = blockCap.fill(extracted, IFluidHandler.FluidAction.SIMULATE);
            if (inserted == FluidType.BUCKET_VOLUME) {
                extracted = cap.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                blockCap.fill(extracted, IFluidHandler.FluidAction.EXECUTE);
                didSomething = true;
                player.setItemInHand(hand, cap.getContainer());

                SoundEvent soundevent = extracted
                        .getFluidType()
                        .getSound(player, level, pos, net.neoforged.neoforge.common.SoundActions.BUCKET_EMPTY);
                if (soundevent == null)
                    soundevent =
                            extracted.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;

                level.playSound(player, pos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }

        return didSomething;
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
