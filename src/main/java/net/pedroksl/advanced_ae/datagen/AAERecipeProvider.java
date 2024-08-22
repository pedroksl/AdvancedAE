package net.pedroksl.advanced_ae.datagen;

import java.util.concurrent.CompletableFuture;

import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipeBuilder;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.datagen.providers.tags.ConventionTags;

public class AAERecipeProvider extends RecipeProvider {
    public AAERecipeProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> provider) {
        super(p, provider);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput c) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.ADV_PATTERN_PROVIDER)
                .pattern("PR")
                .pattern("EL")
                .define('P', EAESingletons.EX_PATTERN_PROVIDER)
                .define('R', Items.REDSTONE)
                .define('E', Items.ENDER_PEARL)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .unlockedBy("hasItem", has(EAESingletons.EX_PATTERN_PROVIDER))
                .save(c, "advpatpro");
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.SMALL_ADV_PATTERN_PROVIDER)
                .pattern("PR")
                .pattern("EL")
                .define('P', AEBlocks.PATTERN_PROVIDER)
                .define('R', Items.REDSTONE)
                .define('E', Items.ENDER_PEARL)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .unlockedBy("hasItem", has(AEBlocks.PATTERN_PROVIDER))
                .save(c, "smalladvpatpro");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AAEBlocks.ADV_PATTERN_PROVIDER)
                .requires(AAEItems.ADV_PATTERN_PROVIDER)
                .unlockedBy("hasItem", has(EAESingletons.EX_PATTERN_PROVIDER))
                .save(c, "advpatpro2");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AAEItems.ADV_PATTERN_PROVIDER)
                .requires(AAEBlocks.ADV_PATTERN_PROVIDER)
                .unlockedBy("hasItem", has(EAESingletons.EX_PATTERN_PROVIDER))
                .save(c, "advpatpropart");
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.ADV_PATTERN_ENCODER)
                .pattern("QRQ")
                .pattern("RER")
                .pattern("QRQ")
                .define('Q', AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED)
                .define('R', Items.REDSTONE)
                .define('E', AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy("hasItem", has(AEItems.BLANK_PATTERN))
                .save(c, "advpartenc");
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.ADV_PATTERN_PROVIDER_UPGRADE)
                .pattern("IR")
                .pattern("EL")
                .define('I', Tags.Items.INGOTS)
                .define('R', Items.REDSTONE)
                .define('E', Items.ENDER_PEARL)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .unlockedBy("hasItem", has(AEItems.BLANK_PATTERN))
                .save(c, "smallappupgrade");
        CrystalAssemblerRecipeBuilder.assemble(AAEItems.ADV_PATTERN_PROVIDER_CAPACITY_UPGRADE)
                .input(Tags.Items.INGOTS)
                .input(AEItems.CAPACITY_CARD, 3)
                .input(Items.CRAFTING_TABLE, 3)
                .input(EAESingletons.CONCURRENT_PROCESSOR)
                .input(ConventionTags.GLASS_CABLE, 6)
                .save(c, AdvancedAE.makeId("largeappupgrade"));
    }
}
