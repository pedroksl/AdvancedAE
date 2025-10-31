package net.pedroksl.advanced_ae.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipeBuilder;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipeBuilder;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.AndCondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEFluids;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.recipes.ReactionChamberRecipeBuilder;
import net.pedroksl.advanced_ae.xmod.Addons;

import appeng.core.definitions.*;
import appeng.datagen.providers.tags.ConventionTags;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipeBuilder;

import mekanism.api.datagen.recipe.builder.ItemStackToItemStackRecipeBuilder;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;

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
                .unlockedBy("hasItem", has(AEBlocks.PATTERN_PROVIDER))
                .save(Addons.EXTENDEDAE.conditionalRecipe(c), AdvancedAE.makeId("eaeadvpatpro"));
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
                .unlockedBy("hasItem", has(AEBlocks.PATTERN_PROVIDER))
                .save(c, AdvancedAE.makeId("advpatpro2"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AAEItems.ADV_PATTERN_PROVIDER)
                .requires(AAEBlocks.ADV_PATTERN_PROVIDER)
                .unlockedBy("hasItem", has(AEBlocks.PATTERN_PROVIDER))
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
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.QUANTUM_CRAFTER)
                .pattern("SAS")
                .pattern("KUK")
                .pattern("SAS")
                .define('S', AAEItems.SHATTERED_SINGULARITY)
                .define('A', AAEBlocks.QUANTUM_ACCELERATOR)
                .define('K', AEItems.CELL_COMPONENT_64K)
                .define('U', AAEBlocks.QUANTUM_UNIT)
                .unlockedBy("hasItem", has(AAEItems.SHATTERED_SINGULARITY))
                .save(c, AdvancedAE.makeId("quantumcrafter"));

        // Blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.QUANTUM_ALLOY_BLOCK)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', AAEItems.QUANTUM_ALLOY)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("quantum_alloy_block"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AAEItems.QUANTUM_ALLOY, 9)
                .requires(AAEBlocks.QUANTUM_ALLOY_BLOCK)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("quantum_alloy_from_block"));
        stairRecipe(c, AAEBlocks.QUANTUM_ALLOY_BLOCK, AAEBlocks.QUANTUM_ALLOY_STAIRS);
        wallRecipe(c, AAEBlocks.QUANTUM_ALLOY_BLOCK, AAEBlocks.QUANTUM_ALLOY_WALL);
        slabRecipe(c, AAEBlocks.QUANTUM_ALLOY_BLOCK, AAEBlocks.QUANTUM_ALLOY_SLAB);

        // Items
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.STOCK_EXPORT_BUS)
                .pattern("   ")
                .pattern("CEL")
                .pattern("   ")
                .define('E', AEParts.EXPORT_BUS)
                .define('C', AEItems.CALCULATION_PROCESSOR)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .unlockedBy("hasItem", has(AEParts.EXPORT_BUS))
                .save(c, AdvancedAE.makeId("stock_export_bus"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.IMPORT_EXPORT_BUS)
                .pattern("   ")
                .pattern("ILE")
                .pattern("   ")
                .define('I', AEParts.IMPORT_BUS)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .define('E', AEParts.EXPORT_BUS)
                .unlockedBy("hasItem", has(AEParts.EXPORT_BUS))
                .save(c, AdvancedAE.makeId("import_export_bus"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.ADVANCED_IO_BUS)
                .pattern("AQA")
                .pattern("IQS")
                .pattern("AQA")
                .define('I', AAEItems.IMPORT_EXPORT_BUS)
                .define('A', AEItems.SPEED_CARD)
                .define('Q', AAEItems.QUANTUM_PROCESSOR)
                .define('S', AAEItems.STOCK_EXPORT_BUS)
                .unlockedBy("hasItem", has(AEParts.EXPORT_BUS))
                .save(c, AdvancedAE.makeId("advanced_io_bus"));
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
                .save(Addons.EXTENDEDAE.conditionalRecipe(c), AdvancedAE.makeId("eaelargeappupgrade"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.ADV_PATTERN_PROVIDER_CAPACITY_UPGRADE)
                .pattern("STS")
                .pattern("GIG")
                .pattern("CCC")
                .define('I', Tags.Items.INGOTS)
                .define('C', AEItems.CAPACITY_CARD)
                .define('T', Items.CRAFTING_TABLE)
                .define('G', ConventionTags.GLASS_CABLE)
                .define('S', AAEItems.SHATTERED_SINGULARITY)
                .unlockedBy("hasItem", has(AAEItems.SHATTERED_SINGULARITY))
                .save(Addons.EXTENDEDAE.notConditionalRecipe(c), AdvancedAE.makeId("largeappupgrade"));
        ReactionChamberRecipeBuilder.react(AAEItems.SHATTERED_SINGULARITY, 2, 200000)
                .input(AEItems.SINGULARITY)
                .input(ConventionTags.ENDER_PEARL_DUST, 2)
                .input(AEItems.SKY_DUST, 2)
                .fluid(Fluids.LAVA, 100)
                .save(c, "shatteredsingularity");
        InscriberRecipeBuilder.inscribe(AAEItems.SHATTERED_SINGULARITY, AAEItems.QUANTUM_INFUSED_DUST, 1)
                .setMode(InscriberProcessType.PRESS)
                .save(c, AdvancedAE.makeId("quantum_infused_dust"));
        ItemStackToItemStackRecipeBuilder.crushing(
                        IngredientCreatorAccess.item().from(AAEItems.SHATTERED_SINGULARITY),
                        AAEItems.QUANTUM_INFUSED_DUST.stack())
                .build(Addons.MEKANISM.conditionalRecipe(c), AdvancedAE.makeId("quantum_infused_dust_crushed"));
        ReactionChamberRecipeBuilder.react(AAEItems.QUANTUM_ALLOY, 1, 200000)
                .input(Items.COPPER_INGOT, 4)
                .input(AAEItems.SHATTERED_SINGULARITY, 4)
                .input(AEItems.SINGULARITY, 4)
                .fluid(AAEFluids.QUANTUM_INFUSION.source(), 1000)
                .save(c, "quantum_alloy");
        ReactionChamberRecipeBuilder.react(AAEItems.QUANTUM_ALLOY_PLATE, 1, 1000000)
                .input(AAEItems.QUANTUM_ALLOY, 8)
                .input(Items.NETHERITE_INGOT, 2)
                .input(Items.NETHER_STAR, 1)
                .fluid(AAEFluids.QUANTUM_INFUSION.source(), 1000)
                .save(c, "quantum_alloy_plate");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AAEItems.THROUGHPUT_MONITOR)
                .requires(AEItems.CALCULATION_PROCESSOR)
                .requires(AEParts.STORAGE_MONITOR)
                .unlockedBy("hasItem", has(AEParts.STORAGE_MONITOR))
                .save(c, AdvancedAE.makeId("throughput_monitor"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AAEItems.QUANTUM_CRAFTER_TERMINAL)
                .requires(AEParts.PATTERN_ACCESS_TERMINAL)
                .requires(AAEItems.QUANTUM_PROCESSOR)
                .unlockedBy("hasItem", has(AAEBlocks.QUANTUM_CRAFTER))
                .save(c, AdvancedAE.makeId("quantum_crafter_terminal"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.QUANTUM_CRAFTER_WIRELESS_TERMINAL)
                .pattern(" W ")
                .pattern(" T ")
                .pattern(" C ")
                .define('W', AEItems.WIRELESS_RECEIVER)
                .define('T', AAEItems.QUANTUM_CRAFTER_TERMINAL)
                .define('C', AEBlocks.DENSE_ENERGY_CELL)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_CRAFTER_TERMINAL))
                .save(c, AdvancedAE.makeId("wireless_quantum_crafter_terminal"));
        InscriberRecipeBuilder.inscribe(AAEItems.SHATTERED_SINGULARITY, AAEItems.QUANTUM_PROCESSOR_PRESS, 1)
                .setTop(Ingredient.of(AEItems.ENGINEERING_PROCESSOR_PRESS))
                .setBottom(Ingredient.of(AEItems.LOGIC_PROCESSOR_PRESS))
                .setMode(InscriberProcessType.PRESS)
                .save(c, AdvancedAE.makeId("quantum_processor_press"));
        InscriberRecipeBuilder.inscribe(Items.IRON_BLOCK, AAEItems.QUANTUM_PROCESSOR_PRESS, 1)
                .setTop(Ingredient.of(AAEItems.QUANTUM_PROCESSOR_PRESS))
                .setMode(InscriberProcessType.INSCRIBE)
                .save(c, AdvancedAE.makeId("quantum_processor_press_from_iron"));
        InscriberRecipeBuilder.inscribe(AAEItems.QUANTUM_ALLOY, AAEItems.QUANTUM_PROCESSOR_PRINT, 1)
                .setTop(Ingredient.of(AAEItems.QUANTUM_PROCESSOR_PRESS))
                .setMode(InscriberProcessType.INSCRIBE)
                .save(c, AdvancedAE.makeId("quantum_processor_print"));
        CircuitCutterRecipeBuilder.cut(AAEItems.QUANTUM_PROCESSOR_PRINT, 9)
                .input(AAEBlocks.QUANTUM_ALLOY_BLOCK, 1)
                .save(Addons.EXTENDEDAE.conditionalRecipe(c), AdvancedAE.makeId("quantum_processor_print_eae"));
        InscriberRecipeBuilder.inscribe(ConventionTags.REDSTONE, AAEItems.QUANTUM_PROCESSOR, 1)
                .setTop(Ingredient.of(AAEItems.QUANTUM_PROCESSOR_PRINT))
                .setBottom(Ingredient.of(AEItems.SILICON_PRINT))
                .setMode(InscriberProcessType.PRESS)
                .save(c, AdvancedAE.makeId("quantum_processor"));
        CrystalAssemblerRecipeBuilder.assemble(AAEItems.QUANTUM_PROCESSOR, 4)
                .input(AAEItems.QUANTUM_PROCESSOR_PRINT, 4)
                .input(AEItems.SILICON_PRINT, 4)
                .input(ConventionTags.REDSTONE, 4)
                .save(c, AdvancedAE.makeId("quantum_processor_eae"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.QUANTUM_STORAGE_COMPONENT)
                .pattern("PSP")
                .pattern("CQC")
                .pattern("PCP")
                .define('P', AAEItems.QUANTUM_PROCESSOR)
                .define('S', AEItems.SPATIAL_2_CELL_COMPONENT)
                .define('C', AEItems.CELL_COMPONENT_256K)
                .define('Q', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_PROCESSOR))
                .save(c, AdvancedAE.makeId("quantum_storage_component"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.MONITOR_CONFIGURATOR)
                .pattern("  S")
                .pattern(" I ")
                .pattern("I  ")
                .define('I', Items.IRON_INGOT)
                .define('S', AEItems.SPEED_CARD)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_PROCESSOR))
                .save(c, AdvancedAE.makeId("throughput_monitor_configurator"));

        // Fluids
        ReactionChamberRecipeBuilder.react(AAEFluids.QUANTUM_INFUSION.source(), 1000, 20000)
                .input(AAEItems.QUANTUM_INFUSED_DUST)
                .fluid(Fluids.WATER, 4000)
                .save(c, "quantum_infusion");

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
                .save(Addons.EXTENDEDAE.conditionalRecipe(c), AdvancedAE.makeId("eaequantumunit"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AAEBlocks.QUANTUM_UNIT)
                .requires(AEBlocks.CRAFTING_UNIT)
                .requires(AEItems.SINGULARITY)
                .requires(AAEItems.SHATTERED_SINGULARITY, 2)
                .unlockedBy("hasItem", has(AEItems.SINGULARITY))
                .save(Addons.EXTENDEDAE.notConditionalRecipe(c), AdvancedAE.makeId("quantumunit"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.QUANTUM_ACCELERATOR)
                .pattern("ECE")
                .pattern("CUC")
                .pattern("ECE")
                .define('E', AAEItems.SHATTERED_SINGULARITY)
                .define('C', EAESingletons.CONCURRENT_PROCESSOR)
                .define('U', AAEBlocks.QUANTUM_UNIT)
                .unlockedBy("hasItem", has(AEItems.SINGULARITY))
                .save(Addons.EXTENDEDAE.conditionalRecipe(c), AdvancedAE.makeId("eaequantumaccel"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.QUANTUM_ACCELERATOR)
                .pattern("ESE")
                .pattern("SUS")
                .pattern("ESE")
                .define('E', AAEItems.SHATTERED_SINGULARITY)
                .define('S', AEItems.SINGULARITY)
                .define('U', AAEBlocks.QUANTUM_UNIT)
                .unlockedBy("hasItem", has(AEItems.SINGULARITY))
                .save(Addons.EXTENDEDAE.notConditionalRecipe(c), AdvancedAE.makeId("quantumaccel"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.QUANTUM_STORAGE_128M)
                .pattern("ECE")
                .pattern("CUC")
                .pattern("ECE")
                .define('E', AAEItems.SHATTERED_SINGULARITY)
                .define('C', AAEItems.QUANTUM_STORAGE_COMPONENT)
                .define('U', AAEBlocks.QUANTUM_UNIT)
                .unlockedBy("hasItem", has(AEItems.SINGULARITY))
                .save(c, AdvancedAE.makeId("quantumstorage128"));
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
                .input(AAEBlocks.QUANTUM_STORAGE_256M, 3)
                .save(Addons.EXTENDEDAE.conditionalRecipe(c), AdvancedAE.makeId("eaequantumdataentangler"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.DATA_ENTANGLER)
                .pattern("QQQ")
                .pattern("SUS")
                .pattern("SCS")
                .define('U', AAEBlocks.QUANTUM_UNIT)
                .define('C', AAEBlocks.QUANTUM_CORE)
                .define('S', AAEItems.SHATTERED_SINGULARITY)
                .define('Q', AAEBlocks.QUANTUM_STORAGE_256M)
                .unlockedBy("hasItem", has(AAEBlocks.QUANTUM_UNIT))
                .save(Addons.EXTENDEDAE.notConditionalRecipe(c), AdvancedAE.makeId("quantumdataentangler"));
        CrystalAssemblerRecipeBuilder.assemble(AAEBlocks.QUANTUM_MULTI_THREADER)
                .input(AAEBlocks.QUANTUM_UNIT)
                .input(AAEBlocks.QUANTUM_CORE)
                .input(AAEItems.SHATTERED_SINGULARITY, 8)
                .input(AAEBlocks.QUANTUM_ACCELERATOR, 4)
                .input(EAESingletons.CONCURRENT_PROCESSOR, 8)
                .save(Addons.EXTENDEDAE.conditionalRecipe(c), AdvancedAE.makeId("eaequantummultithreader"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEBlocks.QUANTUM_MULTI_THREADER)
                .pattern("SSS")
                .pattern("AUA")
                .pattern("ACA")
                .define('U', AAEBlocks.QUANTUM_UNIT)
                .define('C', AAEBlocks.QUANTUM_CORE)
                .define('S', AAEItems.SHATTERED_SINGULARITY)
                .define('A', AAEBlocks.QUANTUM_ACCELERATOR)
                .unlockedBy("hasItem", has(AAEBlocks.QUANTUM_UNIT))
                .save(Addons.EXTENDEDAE.notConditionalRecipe(c), AdvancedAE.makeId("quantummultithreader"));

        // Reaction Chamber
        ReactionChamberRecipeBuilder.react(AEItems.SINGULARITY, 1000000)
                .input(AEItems.MATTER_BALL, 64)
                .fluid(Fluids.LAVA, 100)
                .save(c, "singularity");
        ReactionChamberRecipeBuilder.react(AEItems.CERTUS_QUARTZ_CRYSTAL, 64, 50000)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 16)
                .input(ConventionTags.CERTUS_QUARTZ_DUST, 16)
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
                .save(Addons.EXTENDEDAE.conditionalRecipe(c), "entrocrystal");
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
                .save(Addons.EXTENDEDAE.conditionalRecipe(c), "entroingot");
        ReactionChamberRecipeBuilder.react(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 64, 1300000)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL, 64)
                .fluid(Fluids.WATER, 1000)
                .save(c, "certuscharger");

        // Quantum Armor
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.QUANTUM_HELMET)
                .pattern("PWP")
                .pattern("PNP")
                .pattern("AQA")
                .define('N', Items.NETHERITE_HELMET)
                .define('W', AEBlocks.WIRELESS_ACCESS_POINT)
                .define('Q', AAEItems.QUANTUM_STORAGE_COMPONENT)
                .define('A', AAEItems.QUANTUM_ALLOY_PLATE)
                .define('P', AAEItems.QUANTUM_PROCESSOR)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("quantum_helmet"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.QUANTUM_CHESTPLATE)
                .pattern("PWP")
                .pattern("PNP")
                .pattern("AQA")
                .define('N', Items.NETHERITE_CHESTPLATE)
                .define('W', AEBlocks.WIRELESS_ACCESS_POINT)
                .define('Q', AAEItems.QUANTUM_STORAGE_COMPONENT)
                .define('A', AAEItems.QUANTUM_ALLOY_PLATE)
                .define('P', AAEItems.QUANTUM_PROCESSOR)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("quantum_chest"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.QUANTUM_LEGGINGS)
                .pattern("PWP")
                .pattern("PNP")
                .pattern("AQA")
                .define('N', Items.NETHERITE_LEGGINGS)
                .define('W', AEBlocks.WIRELESS_ACCESS_POINT)
                .define('Q', AAEItems.QUANTUM_STORAGE_COMPONENT)
                .define('A', AAEItems.QUANTUM_ALLOY_PLATE)
                .define('P', AAEItems.QUANTUM_PROCESSOR)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("quantum_leggings"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.QUANTUM_BOOTS)
                .pattern("PWP")
                .pattern("PNP")
                .pattern("AQA")
                .define('N', Items.NETHERITE_BOOTS)
                .define('W', AEBlocks.WIRELESS_ACCESS_POINT)
                .define('Q', AAEItems.QUANTUM_STORAGE_COMPONENT)
                .define('A', AAEItems.QUANTUM_ALLOY_PLATE)
                .define('P', AAEItems.QUANTUM_PROCESSOR)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("quantum_boots"));

        // Quantum Armor Upgrade Cards
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.QUANTUM_UPGRADE_BASE)
                .pattern(" Q ")
                .pattern("QPQ")
                .pattern(" Q ")
                .define('Q', AAEItems.QUANTUM_PROCESSOR)
                .define('P', AAEItems.QUANTUM_ALLOY_PLATE)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("quantum_base_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.WALK_SPEED_CARD)
                .pattern(" R ")
                .pattern("ACA")
                .pattern(" R ")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('R', ConventionTags.REDSTONE)
                .define('A', AEItems.SPEED_CARD)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("walk_speed_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.SPRINT_SPEED_CARD)
                .pattern("ARA")
                .pattern("RCR")
                .pattern("ARA")
                .define('R', ConventionTags.REDSTONE)
                .define('A', AEItems.SPEED_CARD)
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("sprint_speed_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.STEP_ASSIST_CARD)
                .pattern(" S ")
                .pattern("SCS")
                .pattern(" S ")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('S', AAEBlocks.QUANTUM_ALLOY_STAIRS)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("step_assist_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.JUMP_HEIGHT_CARD)
                .pattern(" C ")
                .pattern(" G ")
                .pattern("BEB")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('G', Items.GUNPOWDER)
                .define('B', Items.BLAZE_POWDER)
                .define('E', Items.END_CRYSTAL)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("jump_height_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.LAVA_IMMUNITY_CARD)
                .pattern(" T ")
                .pattern(" C ")
                .pattern("QLQ")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('T', Items.TOTEM_OF_UNDYING)
                .define('L', Items.LAVA_BUCKET)
                .define('Q', AEBlocks.QUARTZ_GLASS)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("lava_immunity_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.FLIGHT_CARD)
                .pattern("PEP")
                .pattern("FCF")
                .pattern("PTP")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('E', Items.ELYTRA)
                .define('F', Items.FEATHER)
                .define('T', Items.TNT)
                .define('P', AAEItems.QUANTUM_ALLOY_PLATE)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("flight_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.WATER_BREATHING_CARD)
                .pattern(" E ")
                .pattern("PCP")
                .pattern(" H ")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('E', AEItems.ENTROPY_MANIPULATOR)
                .define('H', Items.HEART_OF_THE_SEA)
                .define('P', Items.PUFFERFISH)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("water_breathing_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.AUTO_FEED_CARD)
                .pattern(" W ")
                .pattern("ACM")
                .pattern(" E ")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('E', Items.ENCHANTED_GOLDEN_APPLE)
                .define('A', Items.GOLDEN_CARROT)
                .define('M', Items.GLISTERING_MELON_SLICE)
                .define('W', AEBlocks.WIRELESS_ACCESS_POINT)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("auto_feed_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.AUTO_STOCK_CARD)
                .pattern(" W ")
                .pattern("BCB")
                .pattern(" M ")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('W', AEBlocks.WIRELESS_ACCESS_POINT)
                .define('B', Items.SHULKER_BOX)
                .define('M', AEBlocks.CONTROLLER)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("auto_stock_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.MAGNET_CARD)
                .pattern("IIA")
                .pattern("IC ")
                .pattern("IIA")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('I', Items.IRON_INGOT)
                .define('A', AAEItems.QUANTUM_ALLOY)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("magnet_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.HP_BUFFER_CARD)
                .pattern(" T ")
                .pattern("ICO")
                .pattern(" E ")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('T', Items.TOTEM_OF_UNDYING)
                .define('I', AEParts.IMPORT_BUS)
                .define('O', AEParts.EXPORT_BUS)
                .define('E', AEItems.PORTABLE_ITEM_CELL64K)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("hp_buffer_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.EVASION_CARD)
                .pattern("PSP")
                .pattern("HCH")
                .pattern("PSP")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('H', Items.RABBIT_HIDE)
                .define('S', AEItems.SPEED_CARD)
                .define('P', AAEItems.QUANTUM_ALLOY_PLATE)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("evasion_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.REGENERATION_CARD)
                .pattern(" A ")
                .pattern("RCM")
                .pattern(" O ")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('A', Items.CAKE)
                .define('R', Items.COOKED_RABBIT)
                .define('M', Items.MUSHROOM_STEW)
                .define('O', Items.COOKIE)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("regeneration_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.STRENGTH_CARD)
                .pattern(" N ")
                .pattern("FCQ")
                .pattern(" D ")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('N', Items.NETHERITE_SWORD)
                .define('F', AEItems.FLUIX_SWORD)
                .define('Q', AEItems.CERTUS_QUARTZ_SWORD)
                .define('D', Items.DIAMOND_SWORD)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("strength_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.ATTACK_SPEED_CARD)
                .pattern("SSS")
                .pattern("QCQ")
                .pattern("SSS")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('S', AEItems.SPEED_CARD)
                .define('Q', AAEItems.QUANTUM_PROCESSOR)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("attack_speed_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.LUCK_CARD)
                .pattern("AFA")
                .pattern("NCN")
                .pattern("AFA")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('F', Items.RABBIT_FOOT)
                .define('A', Blocks.AMETHYST_BLOCK)
                .define('N', Items.NETHER_STAR)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("luck_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.REACH_CARD)
                .pattern("E E")
                .pattern("OCO")
                .pattern("S S")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('E', Items.END_ROD)
                .define('O', Items.LIGHTNING_ROD)
                .define('S', Items.ECHO_SHARD)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("reach_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.SWIM_SPEED_CARD)
                .pattern(" B ")
                .pattern("HCH")
                .pattern(" R ")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('B', Items.OAK_BOAT)
                .define('R', Items.FIREWORK_ROCKET)
                .define('H', Items.HEART_OF_THE_SEA)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("swim_speed_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.NIGHT_VISION_CARD)
                .pattern(" B ")
                .pattern("GCG")
                .pattern(" S ")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('B', Items.BEACON)
                .define('S', Items.SEA_LANTERN)
                .define('G', Items.GLOWSTONE_DUST)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("night_vision_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.FLIGHT_DRIFT_CARD)
                .pattern(" R ")
                .pattern("QCQ")
                .pattern(" L ")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('R', Items.RECOVERY_COMPASS)
                .define('L', Blocks.LODESTONE)
                .define('Q', AAEItems.QUANTUM_PROCESSOR)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("flight_drift_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.RECHARGING_CARD)
                .pattern("SWS")
                .pattern("QCQ")
                .pattern("EDE")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('W', AEBlocks.WIRELESS_ACCESS_POINT)
                .define('S', AEItems.CHARGED_STAFF)
                .define('Q', AAEItems.QUANTUM_PROCESSOR)
                .define('E', AEItems.ENERGY_CARD)
                .define('D', AEBlocks.DENSE_ENERGY_CELL)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("recharging_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.WORKBENCH_CARD)
                .pattern(" W ")
                .pattern("PCP")
                .pattern("ABA")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('W', AEBlocks.WIRELESS_ACCESS_POINT)
                .define('B', AEBlocks.CELL_WORKBENCH)
                .define('A', AAEItems.QUANTUM_ALLOY)
                .define('P', AAEItems.QUANTUM_PROCESSOR)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("portable_workbench_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.PICK_CRAFT_CARD)
                .pattern(" W ")
                .pattern("PCP")
                .pattern("ABA")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('W', AEBlocks.WIRELESS_ACCESS_POINT)
                .define('B', Blocks.CRAFTING_TABLE)
                .define('A', AAEItems.QUANTUM_ALLOY)
                .define('P', AAEItems.QUANTUM_PROCESSOR)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("pick_craft_card"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItems.CAMO_CARD)
                .pattern(" G ")
                .pattern("FCF")
                .pattern("NNN")
                .define('C', AAEItems.QUANTUM_UPGRADE_BASE)
                .define('G', Items.GOLDEN_CARROT)
                .define('F', Items.FERMENTED_SPIDER_EYE)
                .define('N', Items.NETHER_WART)
                .unlockedBy("hasItem", has(AAEItems.QUANTUM_ALLOY))
                .save(c, AdvancedAE.makeId("camo_card"));

        resetNbtRecipe(c, AAEItems.QUANTUM_HELMET);
        resetNbtRecipe(c, AAEItems.QUANTUM_CHESTPLATE);
        resetNbtRecipe(c, AAEItems.QUANTUM_LEGGINGS);
        resetNbtRecipe(c, AAEItems.QUANTUM_BOOTS);

        loadAppFluxRecipes(c);
        loadMegaCellsRecipes(c);
    }

    private void resetNbtRecipe(@NotNull RecipeOutput c, BlockDefinition<?> block) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, block)
                .requires(block)
                .unlockedBy("hasItem", has(block))
                .save(c, AdvancedAE.makeId(block.id().getPath() + "_block_reset"));
    }

    private void resetNbtRecipe(@NotNull RecipeOutput c, ItemDefinition<?> item) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item)
                .requires(item)
                .unlockedBy("hasItem", has(item))
                .save(c, AdvancedAE.makeId(item.id().getPath() + "_item_reset"));
    }

    private void loadAppFluxRecipes(@NotNull RecipeOutput c) {
        ReactionChamberRecipeBuilder.react(AFSingletons.REDSTONE_CRYSTAL, 64, 20000)
                .input(Blocks.REDSTONE_BLOCK, 16)
                .input(AEItems.FLUIX_CRYSTAL, 16)
                .input(Items.GLOWSTONE_DUST, 16)
                .fluid(Fluids.WATER, 500)
                .save(Addons.APPFLUX.conditionalRecipe(c), "redstonecrystal");
        ReactionChamberRecipeBuilder.react(AFSingletons.CHARGED_REDSTONE, 64, 1300000)
                .input(AFSingletons.REDSTONE_CRYSTAL, 64)
                .fluid(Fluids.WATER, 1000)
                .save(Addons.APPFLUX.conditionalRecipe(c), "chargedredstone");
    }

    private void loadMegaCellsRecipes(@NotNull RecipeOutput c) {
        ReactionChamberRecipeBuilder.react(MEGAItems.SKY_STEEL_INGOT, 64, 200000)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 16)
                .input(Items.IRON_INGOT, 16)
                .input(AEBlocks.SKY_STONE_BLOCK, 16)
                .fluid(Fluids.LAVA, 500)
                .save(Addons.MEGACELLS.conditionalRecipe(c), "skysteel");
        ReactionChamberRecipeBuilder.react(MEGAItems.SKY_BRONZE_INGOT, 64, 200000)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 16)
                .input(Items.COPPER_INGOT, 16)
                .input(AEBlocks.SKY_STONE_BLOCK, 16)
                .fluid(Fluids.LAVA, 500)
                .save(Addons.MEGACELLS.conditionalRecipe(c), "skybronze");

        ReactionChamberRecipeBuilder.react(MEGAItems.SKY_OSMIUM_INGOT, 64, 200000)
                .input(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED, 16)
                .input(AAEConventionTags.OSMIUM_INGOT, 16)
                .input(AEBlocks.SKY_STONE_BLOCK, 16)
                .fluid(Fluids.LAVA, 500)
                .save(
                        c.withConditions(new AndCondition(List.of(
                                new ModLoadedCondition(Addons.MEGACELLS.getModId()),
                                new ModLoadedCondition(Addons.MEKANISM.getModId())))),
                        "skyosmium");
    }

    private void slabRecipe(RecipeOutput consumer, BlockDefinition<?> block, BlockDefinition<?> slabs) {
        Block inputBlock = block.block();
        Block outputBlock = slabs.block();

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputBlock, 6)
                .pattern("###")
                .define('#', inputBlock)
                .unlockedBy("hasItem", has(inputBlock))
                .save(consumer, block.id() + "_slab");

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(inputBlock), RecipeCategory.MISC, outputBlock, 2)
                .unlockedBy("hasItem", has(inputBlock))
                .save(consumer, slabs.id());
    }

    private void stairRecipe(RecipeOutput consumer, BlockDefinition<?> block, BlockDefinition<?> stairs) {
        Block inputBlock = block.block();
        Block outputBlock = stairs.block();

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputBlock, 4)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###")
                .define('#', inputBlock)
                .unlockedBy("hasItem", has(inputBlock))
                .save(consumer, block.id() + "_stair");

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(inputBlock), RecipeCategory.MISC, outputBlock)
                .unlockedBy("hasItem", has(inputBlock))
                .save(consumer, stairs.id());
    }

    private void wallRecipe(RecipeOutput consumer, BlockDefinition<?> block, BlockDefinition<?> wall) {
        Block inputBlock = block.block();
        Block outputBlock = wall.block();

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputBlock, 6)
                .pattern("###")
                .pattern("###")
                .define('#', inputBlock)
                .unlockedBy("hasItem", has(inputBlock))
                .save(consumer, block.id() + "_wall");

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(inputBlock), RecipeCategory.MISC, outputBlock)
                .unlockedBy("hasItem", has(inputBlock))
                .save(consumer, wall.id());
    }
}
