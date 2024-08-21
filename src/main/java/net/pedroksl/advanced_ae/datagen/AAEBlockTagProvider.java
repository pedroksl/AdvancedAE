package net.pedroksl.advanced_ae.datagen;


import com.glodblock.github.extendedae.api.ISpecialDrop;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.AAERegistryHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AAEBlockTagProvider extends BlockTagsProvider {
    public AAEBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, AdvancedAE.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        TagKey<Block> pickaxe = BlockTags.MINEABLE_WITH_PICKAXE;
        for (var block : AAERegistryHandler.INSTANCE.getBlocks()) {
            if (!(block instanceof ISpecialDrop)) {
                tag(pickaxe).add(block);
            }
        }
    }
}
