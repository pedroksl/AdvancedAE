package net.pedroksl.advanced_ae.common.definitions;

import java.util.Objects;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.RegistryObject;

public class FluidDefinition<F extends Fluid, B extends LiquidBlock> {
    private final String englishName;
    private final RegistryObject<FluidType> fluidType;
    private final RegistryObject<F> flowing;
    private final RegistryObject<F> source;
    private final RegistryObject<B> block;
    private final AAEItemDefinition<BucketItem> bucketItem;

    public FluidDefinition(
            String englishName,
            RegistryObject<FluidType> fluidType,
            RegistryObject<F> flowing,
            RegistryObject<F> source,
            RegistryObject<B> block,
            AAEItemDefinition<BucketItem> bucketItem) {
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

    public final Holder<FluidType> fluidTypeId() {
        return this.fluidType.getHolder().orElseThrow();
    }

    public final FluidType fluidType() {
        return this.fluidType.get();
    }

    public final Holder<F> flowingId() {
        return this.flowing.getHolder().orElseThrow();
    }

    public final F flowing() {
        return this.flowing.get();
    }

    public final Holder<F> sourceId() {
        return this.source.getHolder().orElseThrow();
    }

    public final F source() {
        return this.source.get();
    }

    public final ResourceLocation blockResouce() {
        return this.block.getId();
    }

    public final Holder<B> blockId() {
        return this.block.getHolder().orElseThrow();
    }

    public final B block() {
        return this.block.get();
    }

    public final AAEItemDefinition<BucketItem> bucketItemId() {
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
