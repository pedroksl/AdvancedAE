package net.pedroksl.advanced_ae.common.definitions;

import static appeng.block.AEBaseBlock.metalProps;
import static appeng.block.AEBaseBlock.stoneProps;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.blocks.*;
import net.pedroksl.advanced_ae.common.items.AAECraftingBlockItem;
import net.pedroksl.ae2addonlib.registry.AddonBlocks;

import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.BlockDefinition;
import appeng.decorative.AEDecorativeBlock;

public final class AAEBlocks extends AddonBlocks {

    public static final AAEBlocks INSTANCE = new AAEBlocks();

    AAEBlocks() {
        super(AdvancedAE.MOD_ID);
    }

    public static final BlockDefinition<AEDecorativeBlock> QUANTUM_ALLOY_BLOCK = block(
            "Quantum Alloy Block",
            "quantum_alloy_block",
            () -> new AEDecorativeBlock(stoneProps().strength(25, 150).requiresCorrectToolForDrops()),
            BlockItem::new);
    public static final BlockDefinition<StairBlock> QUANTUM_ALLOY_STAIRS = block(
            "Quantum Alloy Stairs",
            "quantum_alloy_stairs",
            () -> new StairBlock(QUANTUM_ALLOY_BLOCK.block().defaultBlockState(), metalProps()),
            BlockItem::new);
    public static final BlockDefinition<WallBlock> QUANTUM_ALLOY_WALL =
            block("Quantum Alloy Wall", "quantum_alloy_wall", () -> new WallBlock(metalProps()), BlockItem::new);
    public static final BlockDefinition<SlabBlock> QUANTUM_ALLOY_SLAB =
            block("Quantum Alloy Slab", "quantum_alloy_slab", () -> new SlabBlock(metalProps()), BlockItem::new);

    public static final BlockDefinition<AAECraftingUnitBlock> QUANTUM_UNIT = block(
            "Quantum Crafting Unit",
            "quantum_unit",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.QUANTUM_UNIT),
            AAECraftingBlockItem::new);
    public static final BlockDefinition<AAECraftingUnitBlock> QUANTUM_CORE = block(
            "Quantum Computer Core",
            "quantum_core",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.QUANTUM_CORE),
            AAECraftingBlockItem::new);
    public static final BlockDefinition<AAECraftingUnitBlock> QUANTUM_STORAGE_128M = block(
            "128M Quantum Computer Storage",
            "quantum_storage_128",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.STORAGE_128M),
            AAECraftingBlockItem::new);
    public static final BlockDefinition<AAECraftingUnitBlock> QUANTUM_STORAGE_256M = block(
            "256M Quantum Computer Storage",
            "quantum_storage_256",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.STORAGE_256M),
            AAECraftingBlockItem::new);
    public static final BlockDefinition<AAECraftingUnitBlock> DATA_ENTANGLER = block(
            "Quantum Data Entangler",
            "data_entangler",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.STORAGE_MULTIPLIER),
            AAECraftingBlockItem::new);
    public static final BlockDefinition<AAECraftingUnitBlock> QUANTUM_ACCELERATOR = block(
            "Quantum Computer Accelerator",
            "quantum_accelerator",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.QUANTUM_ACCELERATOR),
            AAECraftingBlockItem::new);
    public static final BlockDefinition<AAECraftingUnitBlock> QUANTUM_MULTI_THREADER = block(
            "Quantum Computer Multi-Threader",
            "quantum_multi_threader",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.MULTI_THREADER),
            AAECraftingBlockItem::new);
    public static final BlockDefinition<AAECraftingUnitBlock> QUANTUM_STRUCTURE = block(
            "Quantum Computer Structural Glass",
            "quantum_structure",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.STRUCTURE),
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
}
