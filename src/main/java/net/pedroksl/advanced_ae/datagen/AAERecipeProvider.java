package net.pedroksl.advanced_ae.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipeBuilder;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.AndCondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.recipes.ReactionChamberRecipeBuilder;
import net.pedroksl.advanced_ae.xmod.Addons;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.datagen.providers.tags.ConventionTags;

import mekanism.common.registries.MekanismItems;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;

import gripe._90.megacells.definition.MEGAItems;

public class AAERecipeProvider extends RecipeProvider {
    public AAERecipeProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> provider) {
        super(p, provider);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput c) {
        // Devices
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
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.REACTION_CHAMBER)
                .pattern("EME")
                .pattern("FVF")
                .pattern("GBG")
                .define('E', AEItems.FLUIX_DUST)
                .define('M', AEBlocks.CONDENSER)
                .define('F', AEItems.FLUIX_DUST)
                .define('G', Items.GLOWSTONE_DUST)
                .define('V', AEBlocks.VIBRATION_CHAMBER)
                .define('B', Items.BUCKET)
                .unlockedBy("hasItem", has(AEBlocks.VIBRATION_CHAMBER))
                .save(c, AdvancedAE.makeId("reactionchamber"));

        // Items
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
        ReactionChamberRecipeBuilder.react(AAEItems.SHATTERED_SINGULARITY, 2, 200000)
                .input(AEItems.SINGULARITY)
                .input(AEItems.ENDER_DUST, 4)
                .input(AEItems.SKY_DUST, 4)
                .fluid(Fluids.LAVA, 100)
                .save(c, "shatteredsingularity");

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
                .define('E', AAEItems.SHATTERED_SINGULARITY)
                .define('C', EAESingletons.CONCURRENT_PROCESSOR)
                .define('U', AAEBlocks.QUANTUM_UNIT)
                .unlockedBy("hasItem", has(AEItems.SINGULARITY))
                .save(c, AdvancedAE.makeId("quantumaccel"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.QUANTUM_STORAGE_128M)
                .pattern("ECE")
                .pattern("CUC")
                .pattern("ECE")
                .define('E', AAEItems.SHATTERED_SINGULARITY)
                .define('C', MEGAItems.BULK_CELL_COMPONENT)
                .define('U', AAEBlocks.QUANTUM_UNIT)
                .unlockedBy("hasItem", has(AEItems.SINGULARITY))
                .save(Addons.MEGACELLS.conditionalRecipe(c), AdvancedAE.makeId("megaquantumstorage128"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.QUANTUM_STORAGE_128M)
                .pattern("ECE")
                .pattern("CUC")
                .pattern("ECE")
                .define('E', AAEItems.SHATTERED_SINGULARITY)
                .define('C', AEItems.CELL_COMPONENT_256K)
                .define('U', AAEBlocks.QUANTUM_UNIT)
                .unlockedBy("hasItem", has(AEItems.SINGULARITY))
                .save(Addons.MEGACELLS.notConditionalRecipe(c), AdvancedAE.makeId("quantumstorage128"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AAEBlocks.QUANTUM_STORAGE_256M)
                .requires(AAEItems.SHATTERED_SINGULARITY)
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
                .define('E', AAEItems.SHATTERED_SINGULARITY)
                .define('U', AAEBlocks.QUANTUM_UNIT)
                .define('A', AAEBlocks.QUANTUM_ACCELERATOR)
                .define('T', AAEBlocks.QUANTUM_STORAGE_256M)
                .unlockedBy("hasItem", has(AEItems.SINGULARITY))
                .save(c, AdvancedAE.makeId("quantumcore"));
        CrystalAssemblerRecipeBuilder.assemble(AAEBlocks.DATA_ENTANGLER)
                .input(AAEBlocks.QUANTUM_UNIT)
                .input(AAEBlocks.QUANTUM_CORE)
                .input(AAEItems.SHATTERED_SINGULARITY, 8)
                .input(AAEBlocks.QUANTUM_STORAGE_256M, 4)
                .save(c, AdvancedAE.makeId("quantumdataentangler"));
        CrystalAssemblerRecipeBuilder.assemble(AAEBlocks.QUANTUM_MULTI_THREADER)
                .input(AAEBlocks.QUANTUM_UNIT)
                .input(AAEBlocks.QUANTUM_CORE)
                .input(AAEItems.SHATTERED_SINGULARITY, 8)
                .input(AAEBlocks.QUANTUM_ACCELERATOR, 4)
                .input(EAESingletons.CONCURRENT_PROCESSOR, 8)
                .save(c, AdvancedAE.makeId("quantummultithreader"));

        // Reaction Chamber
        ReactionChamberRecipeBuilder.react(AEItems.SINGULARITY, 1000000)
                .input(AEItems.MATTER_BALL, 64)
                .fluid(Fluids.LAVA, 100)
                .save(c, "singularity");
        ReactionChamberRecipeBuilder.react(AEItems.CERTUS_QUARTZ_CRYSTAL, 64, 50000)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 16)
                .input(AEItems.CERTUS_QUARTZ_DUST, 16)
                .fluid(Fluids.WATER, 500)
                .save(c, "quartzcrystal");
        ReactionChamberRecipeBuilder.react(AEItems.FLUIX_CRYSTAL, 64, 200000)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 16)
                .input(Items.REDSTONE, 16)
                .input(Items.QUARTZ, 16)
                .fluid(Fluids.WATER, 500)
                .save(c, "fluixcrystals");
        ReactionChamberRecipeBuilder.react(AEItems.FLUIX_CRYSTAL, 64, 100000)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 32)
                .input(AEItems.FLUIX_DUST, 32)
                .fluid(Fluids.WATER, 500)
                .save(c, "fluixcrystalfromdust");
        ReactionChamberRecipeBuilder.react(EAESingletons.ENTRO_CRYSTAL, 64, 100000)
                .input(EAESingletons.ENTRO_DUST, 32)
                .input(AEItems.FLUIX_CRYSTAL, 32)
                .fluid(Fluids.WATER, 500)
                .save(c, "entrocrystal");
        ReactionChamberRecipeBuilder.react(AEBlocks.DAMAGED_BUDDING_QUARTZ, 8, 100000)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 8)
                .input(AEBlocks.QUARTZ_BLOCK, 8)
                .fluid(Fluids.WATER, 1000)
                .save(c, "damagedbudding");
        ReactionChamberRecipeBuilder.react(AEBlocks.CHIPPED_BUDDING_QUARTZ, 8, 200000)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 8)
                .input(AEBlocks.DAMAGED_BUDDING_QUARTZ, 8)
                .fluid(Fluids.WATER, 1000)
                .save(c, "chippedbudding");
        ReactionChamberRecipeBuilder.react(AEBlocks.FLAWED_BUDDING_QUARTZ, 8, 300000)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 8)
                .input(AEBlocks.CHIPPED_BUDDING_QUARTZ, 8)
                .fluid(Fluids.WATER, 1000)
                .save(c, "flawedbudding");
        ReactionChamberRecipeBuilder.react(EAESingletons.ENTRO_INGOT, 64, 500000)
                .input(EAESingletons.ENTRO_DUST, 32)
                .input(Items.GOLD_INGOT, 32)
                .input(Items.LAPIS_LAZULI, 32)
                .fluid(Fluids.WATER, 500)
                .save(c, "entroingot");

        loadAppFluxRecipes(c);
        loadMegaCellsRecipes(c);
    }

    private void loadAppFluxRecipes(@NotNull RecipeOutput c) {
        ReactionChamberRecipeBuilder.react(AFSingletons.REDSTONE_CRYSTAL, 64, 20000)
                .input(Blocks.REDSTONE_BLOCK, 16)
                .input(AEItems.FLUIX_CRYSTAL, 16)
                .input(Items.GLOWSTONE_DUST, 16)
                .fluid(Fluids.WATER, 500)
                .save(Addons.APPFLUX.conditionalRecipe(c), "redstonecrystal");
    }

    private void loadMegaCellsRecipes(@NotNull RecipeOutput c) {
        ReactionChamberRecipeBuilder.react(MEGAItems.SKY_STEEL_INGOT, 64, 20000)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 16)
                .input(Items.IRON_INGOT, 16)
                .input(AEBlocks.SKY_STONE_BLOCK, 16)
                .fluid(Fluids.LAVA, 500)
                .save(Addons.MEGACELLS.conditionalRecipe(c), "skysteel");
        ReactionChamberRecipeBuilder.react(MEGAItems.SKY_BRONZE_INGOT, 64, 20000)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 16)
                .input(Items.COPPER_INGOT, 16)
                .input(AEBlocks.SKY_STONE_BLOCK, 16)
                .fluid(Fluids.LAVA, 500)
                .save(Addons.MEGACELLS.conditionalRecipe(c), "skybronze");

        ReactionChamberRecipeBuilder.react(MEGAItems.SKY_OSMIUM_INGOT, 32, 20000)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 16)
                .input(MekanismItems.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.OSMIUM), 16)
                .input(AEBlocks.SKY_STONE_BLOCK, 16)
                .fluid(Fluids.LAVA, 500)
                .save(
                        c.withConditions(new AndCondition(List.of(
                                new ModLoadedCondition(Addons.MEGACELLS.getModId()),
                                new ModLoadedCondition(Addons.MEKANISM.getModId())))),
                        "skyosmium");
    }
}
