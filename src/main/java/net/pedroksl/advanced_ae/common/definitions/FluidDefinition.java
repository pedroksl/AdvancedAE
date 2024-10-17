package net.pedroksl.advanced_ae.common.definitions;

import java.util.Objects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;

import appeng.core.definitions.ItemDefinition;

public class FluidDefinition<F extends Fluid, B extends LiquidBlock> {
    private final String englishName;
    private final DeferredHolder<FluidType, FluidType> fluidType;
    private final DeferredHolder<Fluid, F> flowing;
    private final DeferredHolder<Fluid, F> source;
    private final DeferredHolder<Block, B> block;
    private final ItemDefinition<BucketItem> bucketItem;

    public FluidDefinition(
            String englishName,
            DeferredHolder<FluidType, FluidType> fluidType,
            DeferredHolder<Fluid, F> flowing,
            DeferredHolder<Fluid, F> source,
            DeferredHolder<Block, B> block,
            ItemDefinition<BucketItem> bucketItem) {
        this.englishName = englishName;
        this.fluidType = Objects.requireNonNull(fluidType);
        this.flowing = Objects.requireNonNull(flowing);
        this.source = Objects.requireNonNull(source);
        this.block = Objects.requireNonNull(block);
        this.bucketItem = Objects.requireNonNull(bucketItem);
    }

    public String getEnglishName() {
        return this.englishName;
    }

    public ResourceLocation id() {
        return this.source.getId();
    }

    public final DeferredHolder<FluidType, FluidType> fluidTypeId() {
        return this.fluidType;
    }

    public final FluidType fluidType() {
        return this.fluidType.get();
    }

    public final DeferredHolder<Fluid, F> flowingId() {
        return this.flowing;
    }

    public final F flowing() {
        return this.flowing.get();
    }

    public final DeferredHolder<Fluid, F> sourceId() {
        return this.source;
    }

    public final F source() {
        return this.source.get();
    }

    public final DeferredHolder<Block, B> blockId() {
        return this.block;
    }

    public final B block() {
        return this.block.get();
    }

    public final ItemDefinition<BucketItem> bucketItemId() {
        return this.bucketItem;
    }

    public final BucketItem bucketItem() {
        return this.bucketItem.get();
    }

    public FluidStack stack() {
        return new FluidStack(this.source.get(), 1000);
    }

    public FluidStack stack(int amount) {
        return new FluidStack(this.source.get(), amount);
    }
}
