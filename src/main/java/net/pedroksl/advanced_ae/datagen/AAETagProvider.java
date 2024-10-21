package net.pedroksl.advanced_ae.datagen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAETags;

import appeng.core.definitions.BlockDefinition;

public class AAETagProvider {
    public static class AAEBlockTagProvider extends IntrinsicHolderTagsProvider<Block> {
        public AAEBlockTagProvider(
                PackOutput output,
                CompletableFuture<HolderLookup.Provider> registries,
                @Nullable ExistingFileHelper existingFileHelper) {
            super(
                    output,
                    Registries.BLOCK,
                    registries,
                    block -> BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow(),
                    AdvancedAE.MOD_ID,
                    existingFileHelper);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {
            Map<BlockDefinition<?>, List<TagKey<Block>>> specialTags = new HashMap<>();
            for (var skyStoneBlock : QUANTUM_ALLOY_BLOCKS) {
                specialTags.put(skyStoneBlock, List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL));
            }
            var defaultTags = List.of(BlockTags.MINEABLE_WITH_PICKAXE);

            for (var block : AAEBlocks.getBlocks()) {
                for (var desiredTag : specialTags.getOrDefault(block, defaultTags)) {
                    tag(desiredTag).add(block.block());
                }
            }

            tag(AAEConventionTags.QUANTUM_ALLOY_STORAGE_BLOCK_BLOCK).add(AAEBlocks.QUANTUM_ALLOY_BLOCK.block());
            tag(Tags.Blocks.STORAGE_BLOCKS).addTag(AAEConventionTags.QUANTUM_ALLOY_STORAGE_BLOCK_BLOCK);
            tag(BlockTags.WALLS).add(AAEBlocks.QUANTUM_ALLOY_WALL.block());
        }

        private static final BlockDefinition<?>[] QUANTUM_ALLOY_BLOCKS = {
            AAEBlocks.QUANTUM_ALLOY_BLOCK,
            AAEBlocks.QUANTUM_ALLOY_SLAB,
            AAEBlocks.QUANTUM_ALLOY_WALL,
            AAEBlocks.QUANTUM_ALLOY_STAIRS
        };

        @NotNull
        @Override
        public String getName() {
            return "Tags (Block)";
        }
    }

    public static class AAEItemTagProvider extends ItemTagsProvider {
        public AAEItemTagProvider(
                PackOutput output,
                CompletableFuture<HolderLookup.Provider> registries,
                CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
                ExistingFileHelper existing) {
            super(output, registries, blockTags, AdvancedAE.MOD_ID, existing);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {
            tag(AAETags.ADV_PATTERN_PROVIDER)
                    .add(AAEBlocks.ADV_PATTERN_PROVIDER.asItem(), AAEItems.ADV_PATTERN_PROVIDER.asItem());

            tag(AAEConventionTags.CURIOS).add(AAEItems.ADV_PATTERN_ENCODER.asItem());
        }

        @NotNull
        @Override
        public String getName() {
            return "Tags (Item)";
        }
    }
}
