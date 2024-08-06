package net.pedroksl.advanced_ae.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.*;

public class AAELootTableProvider extends LootTableProvider {

    public AAELootTableProvider(PackOutput pOutput) {
        super(pOutput, Collections.emptySet(),
                Collections.singletonList(
                        new LootTableProvider.SubProviderEntry(
                                AAESubProvider::new, LootContextParamSets.BLOCK)));
    }

    public static class AAESubProvider extends BlockLootSubProvider {

        protected AAESubProvider() {
            super(Collections.emptySet(), FeatureFlagSet.of(), new HashMap<>());
        }

        @Override
        protected void generate() {

        }
    }
}
