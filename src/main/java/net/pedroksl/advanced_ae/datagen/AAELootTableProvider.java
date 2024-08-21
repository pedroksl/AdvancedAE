package net.pedroksl.advanced_ae.datagen;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

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
            super(Collections.emptySet(), FeatureFlagSet.of(), provider);
        }

        @Override
        protected void generate() {}
    }
}
