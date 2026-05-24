package net.pedroksl.advanced_ae.common.definitions;

import static appeng.block.AEBaseBlock.metalProps;
import static appeng.block.AEBaseBlock.stoneProps;
import static net.pedroksl.advanced_ae.AdvancedAE.makeId;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.blocks.*;
import net.pedroksl.advanced_ae.common.items.AAECraftingBlockItem;
import net.pedroksl.ae2addonlib.registry.BlockRegistry;

import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.BlockDefinition;
import appeng.decorative.AEDecorativeBlock;

public final class AAEBlocks extends BlockRegistry {

    public static final AAEBlocks INSTANCE = new AAEBlocks();

    AAEBlocks() {
        super(AdvancedAE.MOD_ID);
    }

    public static final BlockDefinition<AEDecorativeBlock> QUANTUM_ALLOY_BLOCK = block(
            "Quantum Alloy Block",
            "quantum_alloy_block",
            p -> new AEDecorativeBlock(stoneProps(p).strength(25, 150).requiresCorrectToolForDrops()),
            BlockItem::new);
    public static final BlockDefinition<StairBlock> QUANTUM_ALLOY_STAIRS = block(
            "Quantum Alloy Stairs",
            "quantum_alloy_stairs",
            p -> new StairBlock(QUANTUM_ALLOY_BLOCK.block().defaultBlockState(), metalProps(p)),
            BlockItem::new);
    public static final BlockDefinition<WallBlock> QUANTUM_ALLOY_WALL =
            block("Quantum Alloy Wall", "quantum_alloy_wall", p -> new WallBlock(metalProps(p)), BlockItem::new);
    public static final BlockDefinition<SlabBlock> QUANTUM_ALLOY_SLAB =
            block("Quantum Alloy Slab", "quantum_alloy_slab", p -> new SlabBlock(metalProps(p)), BlockItem::new);

    public static final BlockDefinition<AAECraftingUnitBlock> QUANTUM_UNIT = block(
            "Quantum Crafting Unit",
            "quantum_unit",
            p -> new AAECraftingUnitBlock(p, AAECraftingUnitType.QUANTUM_UNIT),
            AAECraftingBlockItem::new);
    public static final BlockDefinition<AAECraftingUnitBlock> QUANTUM_CORE = block(
            "Quantum Computer Core",
            "quantum_core",
            p -> new AAECraftingUnitBlock(p, AAECraftingUnitType.QUANTUM_CORE),
            AAECraftingBlockItem::new);
    public static final BlockDefinition<AAECraftingUnitBlock> QUANTUM_STORAGE_128M = block(
            "128M Quantum Computer Storage",
            "quantum_storage_128",
            p -> new AAECraftingUnitBlock(p, AAECraftingUnitType.QUANTUM_STORAGE_128),
            AAECraftingBlockItem::new);
    public static final BlockDefinition<AAECraftingUnitBlock> QUANTUM_STORAGE_256M = block(
            "256M Quantum Computer Storage",
            "quantum_storage_256",
            p -> new AAECraftingUnitBlock(p, AAECraftingUnitType.QUANTUM_STORAGE_256),
            AAECraftingBlockItem::new);
    public static final BlockDefinition<AAECraftingUnitBlock> DATA_ENTANGLER = block(
            "Quantum Data Entangler",
            "data_entangler",
            p -> new AAECraftingUnitBlock(p, AAECraftingUnitType.DATA_ENTANGLER),
            AAECraftingBlockItem::new);
    public static final BlockDefinition<AAECraftingUnitBlock> QUANTUM_ACCELERATOR = block(
            "Quantum Computer Accelerator",
            "quantum_accelerator",
            p -> new AAECraftingUnitBlock(p, AAECraftingUnitType.QUANTUM_ACCELERATOR),
            AAECraftingBlockItem::new);
    public static final BlockDefinition<AAECraftingUnitBlock> QUANTUM_MULTI_THREADER = block(
            "Quantum Computer Multi-Threader",
            "quantum_multi_threader",
            p -> new AAECraftingUnitBlock(p, AAECraftingUnitType.QUANTUM_MULTI_THREADER),
            AAECraftingBlockItem::new);
    public static final BlockDefinition<AAECraftingUnitBlock> QUANTUM_STRUCTURE = block(
            "Quantum Computer Structural Glass",
            "quantum_structure",
            p -> new AAECraftingUnitBlock(p, AAECraftingUnitType.QUANTUM_STRUCTURE),
            AAECraftingBlockItem::new);

    public static final BlockDefinition<AdvPatternProviderBlock> ADV_PATTERN_PROVIDER = block(
            "Advanced Extended Pattern Provider",
            "adv_pattern_provider",
            AdvPatternProviderBlock::new,
            AEBaseBlockItem::new);
    public static final BlockDefinition<SmallAdvPatternProviderBlock> SMALL_ADV_PATTERN_PROVIDER = block(
            "Advanced Pattern Provider",
            "small_adv_pattern_provider",
            SmallAdvPatternProviderBlock::new,
            AEBaseBlockItem::new);

    public static final BlockDefinition<ReactionChamberBlock> REACTION_CHAMBER =
            block("Reaction Chamber", "reaction_chamber", ReactionChamberBlock::new, AEBaseBlockItem::new);
    public static final BlockDefinition<QuantumCrafterBlock> QUANTUM_CRAFTER =
            block("Quantum Crafter", "quantum_crafter", QuantumCrafterBlock::new, AEBaseBlockItem::new);

    protected static <T extends Block> BlockDefinition<T> block(
            String englishName, String id, Function<Properties, T> blockSupplier) {
        return block(englishName, makeId(id), blockSupplier, null);
    }

    protected static <T extends Block> BlockDefinition<T> block(
            String englishName,
            String id,
            Function<Properties, T> blockSupplier,
            @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        return block(englishName, makeId(id), blockSupplier, itemFactory);
    }
}
