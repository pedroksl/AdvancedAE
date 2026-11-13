package net.pedroksl.advanced_ae.datagen;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.*;

import appeng.core.AppEng;
import appeng.core.ConventionTags;
import appeng.core.definitions.BlockDefinition;
import appeng.items.tools.MemoryCardItem;

public class AAETagProvider {
    public static class AAEBlockTagProvider extends TagsProvider<Block> {
        public AAEBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, Registries.BLOCK, registries, AdvancedAE.MOD_ID);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {
            Map<BlockDefinition<?>, List<TagKey<Block>>> specialTags = new HashMap<>();
            for (var skyStoneBlock : QUANTUM_ALLOY_BLOCKS) {
                specialTags.put(skyStoneBlock, List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL));
            }
            var defaultTags = List.of(BlockTags.MINEABLE_WITH_PICKAXE);

            for (var block : AAEBlocks.INSTANCE.getBlocks()) {
                for (var desiredTag : specialTags.getOrDefault(block, defaultTags)) {
                    tagBlock(desiredTag, block);
                }
            }

            tagBlock(AAEConventionTags.QUANTUM_ALLOY_STORAGE_BLOCK_BLOCK, AAEBlocks.QUANTUM_ALLOY_BLOCK);
            tag(Tags.BlockItems.STORAGE_BLOCKS.block()).addTag(AAEConventionTags.QUANTUM_ALLOY_STORAGE_BLOCK_BLOCK);
            tagBlock(BlockTags.WALLS, AAEBlocks.QUANTUM_ALLOY_WALL);
            tagBlock(BlockTags.STAIRS, AAEBlocks.QUANTUM_ALLOY_STAIRS);
            tagBlock(BlockTags.SLABS, AAEBlocks.QUANTUM_ALLOY_SLAB);
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

        private void tagBlock(TagKey<Block> tagKey, BlockDefinition<?> block) {
            tag(tagKey)
                    .add(BuiltInRegistries.BLOCK.getResourceKey(block.block()).get());
        }
    }

    public static class AAEItemTagProvider extends BlockTagCopyingItemTagProvider {
        public AAEItemTagProvider(
                PackOutput output,
                CompletableFuture<HolderLookup.Provider> registries,
                CompletableFuture<TagsProvider.TagLookup<Block>> blockTagsProvider) {
            super(output, registries, blockTagsProvider, AdvancedAE.MOD_ID);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {
            tagItem(
                    AAETags.ADV_PATTERN_PROVIDER,
                    AAEBlocks.ADV_PATTERN_PROVIDER,
                    AAEItems.ADV_PATTERN_PROVIDER,
                    AAEBlocks.SMALL_ADV_PATTERN_PROVIDER,
                    AAEItems.SMALL_ADV_PATTERN_PROVIDER);
            tagItem(
                    ConventionTags.PATTERN_PROVIDER,
                    AAEBlocks.ADV_PATTERN_PROVIDER,
                    AAEItems.ADV_PATTERN_PROVIDER,
                    AAEBlocks.SMALL_ADV_PATTERN_PROVIDER,
                    AAEItems.SMALL_ADV_PATTERN_PROVIDER);

            tagItem(AAEConventionTags.ENCODER_CURIO, AAEItems.ADV_PATTERN_ENCODER);
            // tag(ConventionTags.CURIOS).add(AAEItems.QUANTUM_CRAFTER_WIRELESS_TERMINAL.asItem());

            tagItem(ConventionTags.WRENCH, AAEItems.MONITOR_CONFIGURATOR);

            tagItem(Tags.Items.INGOTS, AAEItems.QUANTUM_ALLOY);

            tagItem(
                    Tags.Items.ARMORS,
                    AAEItems.QUANTUM_HELMET,
                    AAEItems.QUANTUM_CHESTPLATE,
                    AAEItems.QUANTUM_LEGGINGS,
                    AAEItems.QUANTUM_BOOTS);
            tagItem(ItemTags.HEAD_ARMOR, AAEItems.QUANTUM_HELMET);
            tagItem(ItemTags.CHEST_ARMOR, AAEItems.QUANTUM_CHESTPLATE);
            tagItem(ItemTags.LEG_ARMOR, AAEItems.QUANTUM_LEGGINGS);
            tagItem(ItemTags.FOOT_ARMOR, AAEItems.QUANTUM_BOOTS);
            tagItem(Tags.Items.BUCKETS, AAEFluids.QUANTUM_INFUSION.bucketItem());

            tagItem(Tags.Items.DUSTS, AAEItems.QUANTUM_INFUSED_DUST);
            tagItem(AAEConventionTags.SHATTERED_SINGULARITY, AAEItems.QUANTUM_INFUSED_DUST);

            tagItem(AAEConventionTags.QUANTUM_ALLOY_PLATE, AAEItems.QUANTUM_ALLOY_PLATE);
            tag(AAEConventionTags.PLATES).addTag(AAEConventionTags.QUANTUM_ALLOY_PLATE);

            tagItem(AAEConventionTags.QUANTUM_ALLOY_STORAGE_BLOCK_ITEM, AAEBlocks.QUANTUM_ALLOY_BLOCK);
            tag(Tags.BlockItems.STORAGE_BLOCKS.item()).addTag(AAEConventionTags.QUANTUM_ALLOY_STORAGE_BLOCK_ITEM);
        }

        @NotNull
        @Override
        public String getName() {
            return "Tags (Item)";
        }

        private void tagItem(TagKey<Item> tagKey, ItemLike... items) {
            var tag = tag(tagKey);
            for (ItemLike item : items) {
                tag.add(BuiltInRegistries.ITEM.getResourceKey(item.asItem()).get());
            }
        }
    }

    public static class AAEDataComponentTypeTagProvider extends TagsProvider<DataComponentType<?>> {
        private final AAELanguageProvider localization;

        public AAEDataComponentTypeTagProvider(
                PackOutput output,
                CompletableFuture<HolderLookup.Provider> registries,
                AAELanguageProvider localization) {
            super(output, Registries.DATA_COMPONENT_TYPE, registries, AppEng.MOD_ID);
            this.localization = localization;
        }

        private final HashSet<DataComponentType<?>> translated = new HashSet<>();

        @Override
        protected void addTags(HolderLookup.Provider registries) {

            Map<DataComponentType<?>, ResourceKey<DataComponentType<?>>> componentKeys = new IdentityHashMap<>();
            for (var entry : AAEComponents.INSTANCE.getEntries()) {
                componentKeys.put(entry.get(), entry.key());
            }

            addExportedComponentCategory("Allowed Sides", AAEComponents.EXPORTED_ALLOWED_SIDES);
        }

        private void addExportedComponentCategory(String englishCategoryName, DataComponentType<?>... types) {
            for (var type : types) {
                translated.add(type);
                var key = BuiltInRegistries.DATA_COMPONENT_TYPE
                        .getResourceKey(type)
                        .get();
                tag(ConventionTags.EXPORTED_SETTINGS).add(key);

                localization.add(MemoryCardItem.getSettingTranslationKey(type), englishCategoryName);
            }
        }
    }

    private static ResourceKey<Block> getBlockKey(Block block) {
        return BuiltInRegistries.BLOCK.getResourceKey(block).get();
    }
}
