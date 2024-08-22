package net.pedroksl.advanced_ae.datagen;

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
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAETags;

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
            for (var block : AAEBlocks.getBlocks()) {
                tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE).add(block.block());
            }
        }

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
        }

        @NotNull
        @Override
        public String getName() {
            return "Tags (Item)";
        }
    }
}
