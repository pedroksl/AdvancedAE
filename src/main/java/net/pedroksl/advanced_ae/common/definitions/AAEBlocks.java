package net.pedroksl.advanced_ae.common.definitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.blocks.AdvPatternProviderBlock;
import net.pedroksl.advanced_ae.common.blocks.SmallAdvPatternProviderBlock;
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

    //	public static final BlockDefinition<CraftingUnitBlock> ADV_CRAFTING_UNIT = block(
    //			"Advanced Crafting Unit",
    //			"adv_crafting_unit",
    //			() -> new CraftingUnitBlock(AAECraftingUnitType.UNIT),
    //			AEBaseBlockItem::new);
    //	public static final BlockDefinition<CraftingUnitBlock> ADV_CRAFTING_ACCELERATOR = craftingBlock(
    //			"Advanced Crafting Co-Processing Unit",
    //			"adv_crafting_accelerator",
    //			() -> new CraftingUnitBlock(AAECraftingUnitType.ACCELERATOR),
    //			() -> AEItems.ENGINEERING_PROCESSOR);

    public static final BlockDefinition<AdvPatternProviderBlock> ADV_PATTERN_PROVIDER = block(
            "Advanced Pattern Provider", "adv_pattern_provider", AdvPatternProviderBlock::new, AEBaseBlockItem::new);
    public static final BlockDefinition<SmallAdvPatternProviderBlock> SMALL_ADV_PATTERN_PROVIDER = block(
            "Advanced Pattern Provider (9 Slots)",
            "small_adv_pattern_provider",
            SmallAdvPatternProviderBlock::new,
            AEBaseBlockItem::new);

    //	public static final BlockDefinition<ReactionChamberBlock> REACTION_CHAMBER =
    //			block("Reaction Chamber", "reaction_chamber", ReactionChamberBlock::new, AEBaseBlockItem::new);

    private static <T extends Block> BlockDefinition<T> craftingBlock(
            String englishName, String id, Supplier<T> blockSupplier, Supplier<ItemLike> disassemblyExtra) {
        return block(
                englishName,
                id,
                blockSupplier,
                (block, props) -> new AAECraftingBlockItem(block, props, disassemblyExtra));
    }

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
