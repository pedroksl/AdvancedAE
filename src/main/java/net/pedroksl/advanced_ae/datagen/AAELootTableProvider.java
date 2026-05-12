package net.pedroksl.advanced_ae.datagen;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.pedroksl.advanced_ae.AdvancedAE;

public class AAELootTableProvider extends LootTableProvider {

    public AAELootTableProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> provider) {
        super(
                p,
                Collections.emptySet(),
                Collections.singletonList(
                        new LootTableProvider.SubProviderEntry(AAESubProvider::new, LootContextParamSets.BLOCK)),
                provider);
    }

    public static class AAESubProvider extends BlockLootSubProvider {

        protected AAESubProvider(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return BuiltInRegistries.BLOCK.stream()
                    .filter(entry -> {
                        var lootTable = entry.getLootTable().orElse(null);
                        return lootTable != null
                                && lootTable.identifier().getNamespace().equals(AdvancedAE.MOD_ID);
                    })
                    .toList();
        }

        @Override
        public void generate() {
            for (var block : getKnownBlocks()) {
                add(
                        block,
                        LootTable.lootTable()
                                .withPool(LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(LootItem.lootTableItem(block))
                                        .when(ExplosionCondition.survivesExplosion())));
            }
        }
    }
}
