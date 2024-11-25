package net.pedroksl.advanced_ae.common.definitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.fluids.QuantumInfusionBlock;
import net.pedroksl.advanced_ae.common.fluids.QuantumInfusionFluid;
import net.pedroksl.advanced_ae.common.fluids.QuantumInfusionFluidType;

public class AAEFluids {
    public static final DeferredRegister<FluidType> DR_FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, AdvancedAE.MOD_ID);
    public static final DeferredRegister<Fluid> DR_FLUIDS =
            DeferredRegister.create(Registries.FLUID, AdvancedAE.MOD_ID);
    public static final DeferredRegister<Block> DR_FLUID_BLOCKS =
            DeferredRegister.create(Registries.BLOCK, AdvancedAE.MOD_ID);
    public static final DeferredRegister<Item> DR_BUCKET_ITEMS =
            DeferredRegister.create(Registries.ITEM, AdvancedAE.MOD_ID);

    private static final List<FluidDefinition<?, ?>> FLUIDS = new ArrayList<>();

    public static List<FluidDefinition<?, ?>> getFluids() {
        return Collections.unmodifiableList(FLUIDS);
    }

    public static final FluidDefinition<?, ?> QUANTUM_INFUSION = fluid(
            "Quantum Infusion",
            "quantum_infusion",
            QuantumInfusionFluidType::new,
            QuantumInfusionFluid.Flowing::new,
            QuantumInfusionFluid.Source::new,
            QuantumInfusionBlock::new);

    private static <F extends Fluid, B extends LiquidBlock> FluidDefinition<F, B> fluid(
            String englishName,
            String id,
            Supplier<FluidType> fluidTypeSupplier,
            Supplier<F> flowingSupplier,
            Supplier<F> sourceSupplier,
            Supplier<B> liquidBlockSupplier) {
        var type = DR_FLUID_TYPES.register(id + "_type", fluidTypeSupplier);
        var flowing = DR_FLUIDS.register(id + "_flowing", flowingSupplier);
        var source = DR_FLUIDS.register(id + "_source", sourceSupplier);
        var block = DR_FLUID_BLOCKS.register(id + "_block", liquidBlockSupplier);
        var bucketItem = DR_BUCKET_ITEMS.register(
                id + "_bucket",
                () -> new BucketItem(
                        source,
                        new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

        var bucketDefinition = new AAEItemDefinition<>(englishName + " Bucket", bucketItem);
        var definition = new FluidDefinition<>(englishName, type, flowing, source, block, bucketDefinition);

        FLUIDS.add(definition);
        return definition;
    }

    public static void init(IEventBus eventBus) {
        AAEFluids.DR_FLUID_TYPES.register(eventBus);
        AAEFluids.DR_FLUIDS.register(eventBus);
        AAEFluids.DR_FLUID_BLOCKS.register(eventBus);
        AAEFluids.DR_BUCKET_ITEMS.register(eventBus);
    }
}
