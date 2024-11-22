package net.pedroksl.advanced_ae.datagen;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.datagen.providers.tags.ConventionTags;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.AAEItemAndBlock;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class AAERecipeProvider extends RecipeProvider {
	public AAERecipeProvider(PackOutput pOutput) {
		super(pOutput);
	}

	@Override
	protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder
				.shaped(RecipeCategory.MISC, AAEItemAndBlock.ADV_PATTERN_PROVIDER)
				.pattern("PR")
				.pattern("EL")
				.define('P', EPPItemAndBlock.EX_PATTERN_PROVIDER)
				.define('R', Items.REDSTONE)
				.define('E', Items.ENDER_PEARL)
				.define('L', AEItems.LOGIC_PROCESSOR)
				.unlockedBy("hasItem", has(EPPItemAndBlock.EX_PATTERN_PROVIDER))
				.save(consumer, "advpatpro");
		ShapelessRecipeBuilder
				.shapeless(RecipeCategory.MISC, AAEItemAndBlock.ADV_PATTERN_PROVIDER)
				.requires(AAEItemAndBlock.ADV_PATTERN_PROVIDER_PART)
				.unlockedBy("hasItem", has(EPPItemAndBlock.EX_PATTERN_PROVIDER))
				.save(consumer, "advpatpro2");
		ShapelessRecipeBuilder
				.shapeless(RecipeCategory.MISC, AAEItemAndBlock.ADV_PATTERN_PROVIDER_PART)
				.requires(AAEItemAndBlock.ADV_PATTERN_PROVIDER)
				.unlockedBy("hasItem", has(EPPItemAndBlock.EX_PATTERN_PROVIDER))
				.save(consumer, "advpatpropart");
		ShapedRecipeBuilder
				.shaped(RecipeCategory.MISC, AAEItemAndBlock.ADV_PATTERN_ENCODER)
				.pattern("QRQ")
				.pattern("RER")
				.pattern("QRQ")
				.define('Q', AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED)
				.define('R', AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED)
				.define('E', AEItems.ENGINEERING_PROCESSOR)
				.unlockedBy("hasItem", has(AEItems.BLANK_PATTERN))
				.save(consumer, "advpartenc");
		ShapedRecipeBuilder
				.shaped(RecipeCategory.MISC, AAEItemAndBlock.SHATTERED_SINGULARITY)
				.pattern("DED")
				.pattern("ESE")
				.pattern("DED")
				.define('S', AEItems.SINGULARITY)
				.define('E', ConventionTags.ENDER_PEARL_DUST)
				.define('D', AEItems.SKY_DUST)
				.unlockedBy("hasItem", has(AEItems.SINGULARITY))
				.save(consumer, "shattered_singularity");

		// Quantum Computer
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItemAndBlock.QUANTUM_STRUCTURE)
				.pattern("QSQ")
				.pattern("S S")
				.pattern("QSQ")
				.define('Q', AEBlocks.QUARTZ_GLASS)
				.define('S', AEBlocks.SKY_STONE_BLOCK)
				.unlockedBy("hasItem", has(AEItems.SINGULARITY))
				.save(consumer, "quantumstructure");
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AAEItemAndBlock.QUANTUM_UNIT)
				.requires(AEBlocks.CRAFTING_UNIT)
				.requires(AEItems.SINGULARITY)
				.requires(Items.REDSTONE)
				.requires(Items.GLOWSTONE)
				.unlockedBy("hasItem", has(AEItems.SINGULARITY))
				.save(consumer, "quantumunit");
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItemAndBlock.QUANTUM_ACCELERATOR)
				.pattern("ESE")
				.pattern("SUS")
				.pattern("ENE")
				.define('E', AAEItemAndBlock.SHATTERED_SINGULARITY)
				.define('U', AAEItemAndBlock.QUANTUM_UNIT)
				.define('S', AEItems.SINGULARITY)
				.define('N', Items.NETHER_STAR)
				.unlockedBy("hasItem", has(AEItems.SINGULARITY))
				.save(consumer, "quantumaccel");
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItemAndBlock.QUANTUM_STORAGE_128M)
				.pattern("ECE")
				.pattern("CUC")
				.pattern("ECE")
				.define('E', AAEItemAndBlock.SHATTERED_SINGULARITY)
				.define('C', AEItems.CELL_COMPONENT_256K)
				.define('U', AAEItemAndBlock.QUANTUM_UNIT)
				.unlockedBy("hasItem", has(AEItems.SINGULARITY))
				.save(consumer, "quantumstorage128");
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AAEItemAndBlock.QUANTUM_STORAGE_256M)
				.requires(AAEItemAndBlock.SHATTERED_SINGULARITY)
				.requires(AAEItemAndBlock.QUANTUM_STORAGE_128M)
				.requires(AAEItemAndBlock.QUANTUM_STORAGE_128M)
				.requires(AAEItemAndBlock.QUANTUM_UNIT)
				.unlockedBy("hasItem", has(AEItems.SINGULARITY))
				.save(consumer, "quantumstorage256");
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItemAndBlock.QUANTUM_CORE)
				.pattern("SES")
				.pattern("AUT")
				.pattern("SES")
				.define('S', AEItems.SINGULARITY)
				.define('E', AAEItemAndBlock.SHATTERED_SINGULARITY)
				.define('U', AAEItemAndBlock.QUANTUM_UNIT)
				.define('A', AAEItemAndBlock.QUANTUM_ACCELERATOR)
				.define('T', AAEItemAndBlock.QUANTUM_STORAGE_256M)
				.unlockedBy("hasItem", has(AEItems.SINGULARITY))
				.save(consumer, "quantumcore");
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItemAndBlock.DATA_ENTANGLER)
				.pattern("SCS")
				.pattern("QUQ")
				.pattern("QSQ")
				.define('U', AAEItemAndBlock.QUANTUM_UNIT)
				.define('C', AAEItemAndBlock.QUANTUM_CORE)
				.define('S', AEItems.SINGULARITY)
				.define('Q', AAEItemAndBlock.QUANTUM_STORAGE_256M)
				.unlockedBy("hasItem", has(AEItems.SINGULARITY))
				.save(consumer, AdvancedAE.id("quantumdataentangler"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AAEItemAndBlock.QUANTUM_MULTI_THREADER)
				.pattern("SCS")
				.pattern("AUA")
				.pattern("ASA")
				.define('U', AAEItemAndBlock.QUANTUM_UNIT)
				.define('C', AAEItemAndBlock.QUANTUM_CORE)
				.define('S', AEItems.SINGULARITY)
				.define('A', AAEItemAndBlock.QUANTUM_ACCELERATOR)
				.unlockedBy("hasItem", has(AEItems.SINGULARITY))
				.save(consumer, AdvancedAE.id("quantummultithreader"));
	}
}
