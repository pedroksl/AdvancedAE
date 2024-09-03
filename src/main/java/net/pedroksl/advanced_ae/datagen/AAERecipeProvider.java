package net.pedroksl.advanced_ae.datagen;

import java.util.concurrent.CompletableFuture;

import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipeBuilder;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.recipes.ReactionChamberRecipeBuilder;

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
                .save(c, AdvancedAE.makeId("advpatpro"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.SMALL_ADV_PATTERN_PROVIDER)
                .pattern("PR")
                .pattern("EL")
                .define('P', AEBlocks.PATTERN_PROVIDER)
                .define('R', Items.REDSTONE)
                .define('E', Items.ENDER_PEARL)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .unlockedBy("hasItem", has(AEBlocks.PATTERN_PROVIDER))
                .save(c, AdvancedAE.makeId("smalladvpatpro"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AAEBlocks.ADV_PATTERN_PROVIDER)
                .requires(AAEItems.ADV_PATTERN_PROVIDER)
                .unlockedBy("hasItem", has(EAESingletons.EX_PATTERN_PROVIDER))
                .save(c, AdvancedAE.makeId("advpatpro2"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AAEItems.ADV_PATTERN_PROVIDER)
                .requires(AAEBlocks.ADV_PATTERN_PROVIDER)
                .unlockedBy("hasItem", has(EAESingletons.EX_PATTERN_PROVIDER))
                .save(c, AdvancedAE.makeId("advpatpropart"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AAEBlocks.SMALL_ADV_PATTERN_PROVIDER)
                .requires(AAEItems.SMALL_ADV_PATTERN_PROVIDER)
                .unlockedBy("hasItem", has(AEBlocks.PATTERN_PROVIDER))
                .save(c, AdvancedAE.makeId("smalladvpatpro2"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AAEItems.SMALL_ADV_PATTERN_PROVIDER)
                .requires(AAEBlocks.SMALL_ADV_PATTERN_PROVIDER)
                .unlockedBy("hasItem", has(AEBlocks.PATTERN_PROVIDER))
                .save(c, AdvancedAE.makeId("smalladvpatpropart"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.ADV_PATTERN_ENCODER)
                .pattern("QRQ")
                .pattern("RER")
                .pattern("QRQ")
                .define('Q', AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED)
                .define('R', Items.REDSTONE)
                .define('E', AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy("hasItem", has(AEItems.BLANK_PATTERN))
                .save(c, AdvancedAE.makeId("advpartenc"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.ADV_PATTERN_PROVIDER_UPGRADE)
                .pattern("IR")
                .pattern("EL")
                .define('I', Tags.Items.INGOTS)
                .define('R', Items.REDSTONE)
                .define('E', Items.ENDER_PEARL)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .unlockedBy("hasItem", has(AEItems.BLANK_PATTERN))
                .save(c, AdvancedAE.makeId("smallappupgrade"));
        CrystalAssemblerRecipeBuilder.assemble(AAEItems.ADV_PATTERN_PROVIDER_CAPACITY_UPGRADE)
                .input(Tags.Items.INGOTS)
                .input(AEItems.CAPACITY_CARD, 3)
                .input(Items.CRAFTING_TABLE, 3)
                .input(EAESingletons.CONCURRENT_PROCESSOR)
                .input(ConventionTags.GLASS_CABLE, 6)
                .save(c, AdvancedAE.makeId("largeappupgrade"));

        // Quantum Computer
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.QUANTUM_STRUCTURE)
                .pattern("QSQ")
                .pattern("S S")
                .pattern("QSQ")
                .define('Q', AEBlocks.QUARTZ_GLASS)
                .define('S', AEBlocks.SKY_STONE_BLOCK)
                .unlockedBy("hasItem", has(AEItems.SINGULARITY))
                .save(c, AdvancedAE.makeId("quantumstructure"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AAEBlocks.QUANTUM_UNIT)
                .requires(AEBlocks.CRAFTING_UNIT)
                .requires(AEItems.SINGULARITY)
                .requires(EAESingletons.CONCURRENT_PROCESSOR)
                .unlockedBy("hasItem", has(AEItems.SINGULARITY))
                .save(c, AdvancedAE.makeId("quantumunit"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.QUANTUM_ACCELERATOR)
                .pattern("ECE")
                .pattern("CUC")
                .pattern("ECE")
                .define('E', AEItems.QUANTUM_ENTANGLED_SINGULARITY)
                .define('C', EAESingletons.CONCURRENT_PROCESSOR)
                .define('U', AAEBlocks.QUANTUM_UNIT)
                .unlockedBy("hasItem", has(AEItems.SINGULARITY))
                .save(c, AdvancedAE.makeId("quantumaccel"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.QUANTUM_STORAGE_128M)
                .pattern("ECE")
                .pattern("CUC")
                .pattern("ECE")
                .define('E', AEItems.QUANTUM_ENTANGLED_SINGULARITY)
                .define('C', AEItems.CELL_COMPONENT_256K)
                .define('U', AAEBlocks.QUANTUM_UNIT)
                .unlockedBy("hasItem", has(AEItems.SINGULARITY))
                .save(c, AdvancedAE.makeId("quantumstorage128"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AAEBlocks.QUANTUM_STORAGE_256M)
                .requires(AEItems.QUANTUM_ENTANGLED_SINGULARITY)
                .requires(AAEBlocks.QUANTUM_STORAGE_128M)
                .requires(AAEBlocks.QUANTUM_STORAGE_128M)
                .requires(AAEBlocks.QUANTUM_UNIT)
                .unlockedBy("hasItem", has(AEItems.SINGULARITY))
                .save(c, AdvancedAE.makeId("quantumstorage256"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.QUANTUM_CORE)
                .pattern("SES")
                .pattern("AUT")
                .pattern("SES")
                .define('S', AEItems.SINGULARITY)
                .define('E', AEItems.QUANTUM_ENTANGLED_SINGULARITY)
                .define('U', AAEBlocks.QUANTUM_UNIT)
                .define('A', AAEBlocks.QUANTUM_ACCELERATOR)
                .define('T', AAEBlocks.QUANTUM_STORAGE_256M)
                .unlockedBy("hasItem", has(AEItems.SINGULARITY))
                .save(c, AdvancedAE.makeId("quantumcore"));
        CrystalAssemblerRecipeBuilder.assemble(AAEBlocks.DATA_ENTANGLER)
                .input(AAEBlocks.QUANTUM_UNIT)
                .input(AAEBlocks.QUANTUM_CORE)
                .input(AEItems.QUANTUM_ENTANGLED_SINGULARITY, 8)
                .input(AAEBlocks.QUANTUM_STORAGE_256M, 4)
                .save(c, AdvancedAE.makeId("quantumdataentangler"));
        CrystalAssemblerRecipeBuilder.assemble(AAEBlocks.QUANTUM_MULTI_THREADER)
                .input(AAEBlocks.QUANTUM_UNIT)
                .input(AAEBlocks.QUANTUM_CORE)
                .input(AEItems.QUANTUM_ENTANGLED_SINGULARITY, 8)
                .input(AAEBlocks.QUANTUM_ACCELERATOR, 4)
                .input(EAESingletons.CONCURRENT_PROCESSOR, 8)
                .save(c, AdvancedAE.makeId("quantummultithreader"));

        // Reaction Chamber
        ReactionChamberRecipeBuilder.react(AEItems.SINGULARITY)
                .input(AEItems.MATTER_BALL, 64)
                .save(c, "singularity");
        ReactionChamberRecipeBuilder.react(AEItems.FLUIX_CRYSTAL, 64)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 64)
                .input(Items.REDSTONE, 64)
                .input(Items.QUARTZ, 64)
                .fluid(Fluids.WATER, 1000)
                .save(c, "fluixcrystals");
    }
}
