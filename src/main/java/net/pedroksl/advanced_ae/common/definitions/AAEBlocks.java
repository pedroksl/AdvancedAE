package net.pedroksl.advanced_ae.common.definitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.blocks.*;
import net.pedroksl.advanced_ae.common.items.AAECraftingBlockItem;

import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;

public final class AAEBlocks {
    public static final DeferredRegister.Blocks DR = DeferredRegister.createBlocks(AdvancedAE.MOD_ID);

    private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

    public static List<BlockDefinition<?>> getBlocks() {
        return Collections.unmodifiableList(BLOCKS);
    }

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
            "Quantum Computer Structure",
            "quantum_structure",
            () -> new AAECraftingUnitBlock(AAECraftingUnitType.STRUCTURE),
            AAECraftingBlockItem::new);

    public static final BlockDefinition<AdvPatternProviderBlock> ADV_PATTERN_PROVIDER = block(
            "Advanced Pattern Provider", "adv_pattern_provider", AdvPatternProviderBlock::new, AEBaseBlockItem::new);
    public static final BlockDefinition<SmallAdvPatternProviderBlock> SMALL_ADV_PATTERN_PROVIDER = block(
            "Advanced Pattern Provider (9 Slots)",
            "small_adv_pattern_provider",
            SmallAdvPatternProviderBlock::new,
            AEBaseBlockItem::new);

    public static final BlockDefinition<ReactionChamberBlock> REACTION_CHAMBER =
            block("Reaction Chamber", "reaction_chamber", ReactionChamberBlock::new, AEBaseBlockItem::new);

    private static <T extends Block> BlockDefinition<T> block(
            String englishName,
            String id,
            Supplier<T> blockSupplier,
            BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        var block = DR.register(id, blockSupplier);
        var item = AAEItems.DR.register(id, () -> itemFactory.apply(block.get(), new Item.Properties()));

        var definition = new BlockDefinition<>(englishName, block, new ItemDefinition<>(englishName, item));
        BLOCKS.add(definition);
        return definition;
    }
}
