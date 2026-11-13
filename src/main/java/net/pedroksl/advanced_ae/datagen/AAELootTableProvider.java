package net.pedroksl.advanced_ae.datagen;

import java.util.List;
import java.util.Set;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.pedroksl.advanced_ae.AdvancedAE;

public class AAELootTableProvider extends LootTableProvider {

    public AAELootTableProvider() {
        super(
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(AAESubProvider::new, LootContextParamSets.BLOCK)));
    }

    public static class AAESubProvider extends BlockLootSubProvider {

        protected AAESubProvider(LootTableSubProvider.Context output) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), output);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return BuiltInRegistries.BLOCK.stream()
                    .filter(block -> block.getLootTable()
                            .map(key -> key.identifier().getNamespace().equals(AdvancedAE.MOD_ID))
                            .orElse(false))
                    .toList();
        }

        @Override
        protected void generate() {
            for (var block : getKnownBlocks()) {
                dropSelf(block);
            }
        }
    }
}
