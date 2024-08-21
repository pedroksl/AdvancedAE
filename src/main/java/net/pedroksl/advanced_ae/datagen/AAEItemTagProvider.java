package net.pedroksl.advanced_ae.datagen;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.pedroksl.advanced_ae.AdvancedAE;

public class AAEItemTagProvider extends ItemTagsProvider {
    public AAEItemTagProvider(
            PackOutput pOutput,
            CompletableFuture<HolderLookup.Provider> pLookupProvider,
            CompletableFuture<TagLookup<Block>> pBlockTags,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, AdvancedAE.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        //		tag(AAETags.EX_PATTERN_PROVIDER)
        //				.add(AAEItemAndBlock.ADV_PATTERN_PROVIDER_PART)
        //				.add(AAEItemAndBlock.ADV_PATTERN_PROVIDER.asItem());
        //		tag(AAETags.EX_INTERFACE)
        //				.add(AAEItemAndBlock.ADV_INTERFACE_PART)
        //				.add(AAEItemAndBlock.ADV_INTERFACE.asItem());
    }
}
