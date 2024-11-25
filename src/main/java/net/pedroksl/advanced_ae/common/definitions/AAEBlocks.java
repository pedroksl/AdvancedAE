package net.pedroksl.advanced_ae.common.definitions;

import static appeng.block.AEBaseBlock.metalProps;
import static appeng.block.AEBaseBlock.stoneProps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.blocks.*;
import net.pedroksl.advanced_ae.common.items.AAECraftingBlockItem;

import appeng.block.AEBaseBlockItem;
import appeng.decorative.AEDecorativeBlock;

public final class AAEBlocks {
    public static final DeferredRegister<Block> DR = DeferredRegister.create(ForgeRegistries.BLOCKS, AdvancedAE.MOD_ID);

    private static final List<AAEBlockDefinition<?>> BLOCKS = new ArrayList<>();

    public static List<AAEBlockDefinition<?>> getBlocks() {
        return Collections.unmodifiableList(BLOCKS);
    }

    public static final AAEBlockDefinition<AEDecorativeBlock> QUANTUM_ALLOY_BLOCK = block(
            "Quantum Alloy Block",
            "quantum_alloy_block",
            () -> new AEDecorativeBlock(stoneProps().strength(25, 150).requiresCorrectToolForDrops()),
            BlockItem::new);
    public static final AAEBlockDefinition<StairBlock> QUANTUM_ALLOY_STAIRS = block(
            "Quantum Alloy Stairs",
            "quantum_alloy_stairs",
            () -> new StairBlock(QUANTUM_ALLOY_BLOCK.block().defaultBlockState(), metalProps()),
            BlockItem::new);
    public static final AAEBlockDefinition<WallBlock> QUANTUM_ALLOY_WALL =
            block("Quantum Alloy Wall", "quantum_alloy_wall", () -> new WallBlock(metalProps()), BlockItem::new);
    public static final AAEBlockDefinition<SlabBlock> QUANTUM_ALLOY_SLAB =
            block("Quantum Alloy Slab", "quantum_alloy_slab", () -> new SlabBlock(metalProps()), BlockItem::new);

    public static final AAEBlockDefinition<AAECraftingUnitBlock> QUANTUM_UNIT = block(
            "Quantum Crafting Unit",
            "quantum_unit",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.QUANTUM_UNIT),
            AAECraftingBlockItem::new);
    public static final AAEBlockDefinition<AAECraftingUnitBlock> QUANTUM_CORE = block(
            "Quantum Computer Core",
            "quantum_core",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.QUANTUM_CORE),
            AAECraftingBlockItem::new);
    public static final AAEBlockDefinition<AAECraftingUnitBlock> QUANTUM_STORAGE_128M = block(
            "128M Quantum Computer Storage",
            "quantum_storage_128",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.STORAGE_128M),
            AAECraftingBlockItem::new);
    public static final AAEBlockDefinition<AAECraftingUnitBlock> QUANTUM_STORAGE_256M = block(
            "256M Quantum Computer Storage",
            "quantum_storage_256",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.STORAGE_256M),
            AAECraftingBlockItem::new);
    public static final AAEBlockDefinition<AAECraftingUnitBlock> DATA_ENTANGLER = block(
            "Quantum Data Entangler",
            "data_entangler",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.STORAGE_MULTIPLIER),
            AAECraftingBlockItem::new);
    public static final AAEBlockDefinition<AAECraftingUnitBlock> QUANTUM_ACCELERATOR = block(
            "Quantum Computer Accelerator",
            "quantum_accelerator",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.QUANTUM_ACCELERATOR),
            AAECraftingBlockItem::new);
    public static final AAEBlockDefinition<AAECraftingUnitBlock> QUANTUM_MULTI_THREADER = block(
            "Quantum Computer Multi-Threader",
            "quantum_multi_threader",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.MULTI_THREADER),
            AAECraftingBlockItem::new);
    public static final AAEBlockDefinition<AAECraftingUnitBlock> QUANTUM_STRUCTURE = block(
            "Quantum Computer Structure",
            "quantum_structure",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.STRUCTURE),
            AAECraftingBlockItem::new);

    public static final AAEBlockDefinition<AdvPatternProviderBlock> ADV_PATTERN_PROVIDER = block(
            "Advanced Extended Pattern Provider",
            "adv_pattern_provider",
            AdvPatternProviderBlock::new,
            AEBaseBlockItem::new);
    public static final AAEBlockDefinition<SmallAdvPatternProviderBlock> SMALL_ADV_PATTERN_PROVIDER = block(
            "Advanced Pattern Provider",
            "small_adv_pattern_provider",
            SmallAdvPatternProviderBlock::new,
            AEBaseBlockItem::new);

    public static final AAEBlockDefinition<ReactionChamberBlock> REACTION_CHAMBER =
            block("Reaction Chamber", "reaction_chamber", ReactionChamberBlock::new, AEBaseBlockItem::new);
    public static final AAEBlockDefinition<QuantumCrafterBlock> QUANTUM_CRAFTER =
            block("Quantum Crafter", "quantum_crafter", QuantumCrafterBlock::new, AEBaseBlockItem::new);

    private static <T extends Block> AAEBlockDefinition<T> block(
            String englishName,
            String id,
            Supplier<T> blockSupplier,
            @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        var block = DR.register(id, blockSupplier);
        var item = AAEItems.DR.register(id, () -> itemFactory.apply(block.get(), new Item.Properties()));

        var itemDefinition = new AAEItemDefinition<>(englishName, item);
        var definition = new AAEBlockDefinition<>(englishName, block, itemDefinition);
        BLOCKS.add(definition);
        return definition;
    }
}
