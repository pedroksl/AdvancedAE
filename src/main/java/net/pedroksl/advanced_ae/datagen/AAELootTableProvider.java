package net.pedroksl.advanced_ae.datagen;

import com.glodblock.github.extendedae.api.ISpecialDrop;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.pedroksl.advanced_ae.common.AAERegistryHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

public class AAELootTableProvider extends LootTableProvider {

    public AAELootTableProvider(PackOutput pOutput) {
        super(pOutput, Collections.emptySet(),
                Collections.singletonList(
                        new LootTableProvider.SubProviderEntry(
                                AAESubProvider::new, LootContextParamSets.BLOCK)));
    }

    public static class AAESubProvider extends BlockLootSubProvider {

        protected AAESubProvider() {
            super(Set.of(), FeatureFlagSet.of(), new HashMap<>());
        }

        @Override
        protected void generate() {
            for (var block : AAERegistryHandler.INSTANCE.getBlocks()) {
                if (!(block instanceof ISpecialDrop)) {
                    add(block, createSingleItemTable(block));
                }
            }
        }

        public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> bi) {
            this.generate();
            for (var e : this.map.entrySet()) {
                bi.accept(e.getKey(), e.getValue());
            }
        }
    }
}
