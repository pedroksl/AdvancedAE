package net.pedroksl.advanced_ae.common.blocks;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.entities.ReactionChamberEntity;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;

public class ReactionChamberBlock extends AEBaseEntityBlock<ReactionChamberEntity> {

    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    public ReactionChamberBlock() {
        super(metalProps().noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(WORKING, false));
    }

    @Override
    public InteractionResult onActivated(
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            @Nullable ItemStack heldItem,
            BlockHitResult hit) {
        if (InteractionUtil.isInAlternateUseMode(player)) {
            return InteractionResult.PASS;
        }

        if (heldItem != null && heldItem.getItem() instanceof BucketItem) {
            var didSomething = useBucket(player, level, pos, heldItem, hand);
            if (didSomething) {
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        var be = this.getBlockEntity(level, pos);
        if (be != null) {
            if (!level.isClientSide()) {
                MenuOpener.open(AAEMenus.REACTION_CHAMBER.get(), player, MenuLocators.forBlockEntity(be));
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    public boolean useBucket(Player player, Level level, BlockPos pos, ItemStack stack, InteractionHand hand) {
        AtomicBoolean didSomething = new AtomicBoolean(false);

        var capOp = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        var entity = level.getBlockEntity(pos);
        if (!(entity instanceof ReactionChamberEntity)) return false;

        capOp.ifPresent(cap -> {
            var blockCapOp = entity.getCapability(ForgeCapabilities.FLUID_HANDLER);
            blockCapOp.ifPresent(blockCap -> {
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
                            var newCapOp = newBucket.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
                            newCapOp.ifPresent(newCap -> {
                                newCap.fill(extracted, IFluidHandler.FluidAction.EXECUTE);
                                player.setItemInHand(hand, newCap.getContainer());
                                player.addItem(new ItemStack(stack.getItem(), stack.getCount() - 1));
                            });
                        }

                        SoundEvent soundevent = extracted
                                .getFluid()
                                .getFluidType()
                                .getSound(player, level, pos, SoundActions.BUCKET_EMPTY);
                        if (soundevent == null)
                            soundevent = extracted.getFluid() == Fluids.LAVA
                                    ? SoundEvents.BUCKET_FILL_LAVA
                                    : SoundEvents.BUCKET_FILL;
                        level.playSound(player, pos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);

                        didSomething.set(true);
                    }
                }
                // Insert into input
                else {
                    var extracted = cap.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
                    var inserted = blockCap.fill(extracted, IFluidHandler.FluidAction.SIMULATE);
                    if (inserted == FluidType.BUCKET_VOLUME) {
                        extracted = cap.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                        blockCap.fill(extracted, IFluidHandler.FluidAction.EXECUTE);
                        didSomething.set(true);
                        player.setItemInHand(hand, cap.getContainer());

                        SoundEvent soundevent = extracted
                                .getFluid()
                                .getFluidType()
                                .getSound(player, level, pos, SoundActions.BUCKET_EMPTY);
                        if (soundevent == null)
                            soundevent = extracted.getFluid() == Fluids.LAVA
                                    ? SoundEvents.BUCKET_EMPTY_LAVA
                                    : SoundEvents.BUCKET_EMPTY;

                        level.playSound(player, pos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
            });
        });

        return didSomething.get();
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
