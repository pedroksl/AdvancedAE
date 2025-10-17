package net.pedroksl.advanced_ae.datagen;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.*;

import appeng.core.AppEng;
import appeng.core.definitions.BlockDefinition;
import appeng.datagen.providers.tags.ConventionTags;
import appeng.items.tools.MemoryCardItem;

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

            for (var block : AAEBlocks.INSTANCE.getBlocks()) {
                for (var desiredTag : specialTags.getOrDefault(block, defaultTags)) {
                    tag(desiredTag).add(block.block());
                }
            }

            tag(AAEConventionTags.QUANTUM_ALLOY_STORAGE_BLOCK_BLOCK).add(AAEBlocks.QUANTUM_ALLOY_BLOCK.block());
            tag(Tags.Blocks.STORAGE_BLOCKS).addTag(AAEConventionTags.QUANTUM_ALLOY_STORAGE_BLOCK_BLOCK);
            tag(BlockTags.WALLS).add(AAEBlocks.QUANTUM_ALLOY_WALL.block());
            tag(BlockTags.STAIRS).add(AAEBlocks.QUANTUM_ALLOY_STAIRS.block());
            tag(BlockTags.SLABS).add(AAEBlocks.QUANTUM_ALLOY_SLAB.block());
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
                    .add(
                            AAEBlocks.ADV_PATTERN_PROVIDER.asItem(),
                            AAEItems.ADV_PATTERN_PROVIDER.asItem(),
                            AAEBlocks.SMALL_ADV_PATTERN_PROVIDER.asItem(),
                            AAEItems.SMALL_ADV_PATTERN_PROVIDER.asItem());
            tag(ConventionTags.PATTERN_PROVIDER)
                    .add(
                            AAEBlocks.ADV_PATTERN_PROVIDER.asItem(),
                            AAEItems.ADV_PATTERN_PROVIDER.asItem(),
                            AAEBlocks.SMALL_ADV_PATTERN_PROVIDER.asItem(),
                            AAEItems.SMALL_ADV_PATTERN_PROVIDER.asItem());

            tag(AAEConventionTags.ENCODER_CURIO).add(AAEItems.ADV_PATTERN_ENCODER.asItem());
            tag(ConventionTags.CURIOS).add(AAEItems.QUANTUM_CRAFTER_WIRELESS_TERMINAL.asItem());

            tag(ConventionTags.WRENCH).add(AAEItems.MONITOR_CONFIGURATOR.asItem());

            tag(Tags.Items.INGOTS).add(AAEItems.QUANTUM_ALLOY.asItem());

            tag(Tags.Items.ARMORS)
                    .add(
                            AAEItems.QUANTUM_HELMET.asItem(),
                            AAEItems.QUANTUM_CHESTPLATE.asItem(),
                            AAEItems.QUANTUM_LEGGINGS.asItem(),
                            AAEItems.QUANTUM_BOOTS.asItem());
            tag(ItemTags.HEAD_ARMOR).add(AAEItems.QUANTUM_HELMET.asItem());
            tag(ItemTags.CHEST_ARMOR).add(AAEItems.QUANTUM_CHESTPLATE.asItem());
            tag(ItemTags.LEG_ARMOR).add(AAEItems.QUANTUM_LEGGINGS.asItem());
            tag(ItemTags.FOOT_ARMOR).add(AAEItems.QUANTUM_BOOTS.asItem());
            tag(Tags.Items.BUCKETS).add(AAEFluids.QUANTUM_INFUSION.bucketItem());

            tag(Tags.Items.DUSTS).add(AAEItems.QUANTUM_INFUSED_DUST.asItem());
            tag(AAEConventionTags.SHATTERED_SINGULARITY).add(AAEItems.QUANTUM_INFUSED_DUST.asItem());

            tag(AAEConventionTags.QUANTUM_ALLOY_PLATE).add(AAEItems.QUANTUM_ALLOY_PLATE.asItem());
            tag(AAEConventionTags.PLATES).addTag(AAEConventionTags.QUANTUM_ALLOY_PLATE);

            tag(AAEConventionTags.QUANTUM_ALLOY_STORAGE_BLOCK_ITEM).add(AAEBlocks.QUANTUM_ALLOY_BLOCK.asItem());
            tag(Tags.Items.STORAGE_BLOCKS).addTag(AAEConventionTags.QUANTUM_ALLOY_STORAGE_BLOCK_ITEM);

            tag(ItemTags.STAIRS).add(AAEBlocks.QUANTUM_ALLOY_STAIRS.asItem());
            tag(ItemTags.SLABS).add(AAEBlocks.QUANTUM_ALLOY_SLAB.asItem());
        }

        @NotNull
        @Override
        public String getName() {
            return "Tags (Item)";
        }
    }

    public static class AAEDataComponentTypeTagProvider extends TagsProvider<DataComponentType<?>> {
        private final AAELanguageProvider localization;

        public AAEDataComponentTypeTagProvider(
                PackOutput output,
                CompletableFuture<HolderLookup.Provider> registries,
                @Nullable ExistingFileHelper existingFileHelper,
                AAELanguageProvider localization) {
            super(output, Registries.DATA_COMPONENT_TYPE, registries, AppEng.MOD_ID, existingFileHelper);
            this.localization = localization;
        }

        private final HashSet<DataComponentType<?>> translated = new HashSet<>();

        @Override
        protected void addTags(HolderLookup.Provider registries) {

            Map<DataComponentType<?>, ResourceKey<DataComponentType<?>>> componentKeys = new IdentityHashMap<>();
            for (var entry : AAEComponents.INSTANCE.getEntries()) {
                componentKeys.put(entry.get(), entry.getKey());
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
}
