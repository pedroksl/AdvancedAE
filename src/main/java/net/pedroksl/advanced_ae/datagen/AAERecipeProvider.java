package net.pedroksl.advanced_ae.datagen;

import appeng.core.definitions.AEItems;
import appeng.datagen.providers.tags.ConventionTags;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.AAESingletons;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AAERecipeProvider extends RecipeProvider {
	public AAERecipeProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> provider) {
		super(p, provider);
	}

	@Override
	protected void buildRecipes(@NotNull RecipeOutput c) {
		ShapedRecipeBuilder
				.shaped(RecipeCategory.MISC, AAESingletons.ADV_PATTERN_PROVIDER)
				.pattern("PR")
				.pattern("EL")
				.define('P', EAESingletons.EX_PATTERN_PROVIDER)
				.define('R', Items.REDSTONE)
				.define('E', Items.ENDER_PEARL)
				.define('L', AEItems.LOGIC_PROCESSOR)
				.unlockedBy("hasItem", has(EAESingletons.EX_PATTERN_PROVIDER))
				.save(c, "advpatpro");
		ShapelessRecipeBuilder
				.shapeless(RecipeCategory.MISC, AAESingletons.ADV_PATTERN_PROVIDER)
				.requires(AAESingletons.ADV_PATTERN_PROVIDER_PART)
				.unlockedBy("hasItem", has(EAESingletons.EX_PATTERN_PROVIDER))
				.save(c, "advpatpro2");
		ShapelessRecipeBuilder
				.shapeless(RecipeCategory.MISC, AAESingletons.ADV_PATTERN_PROVIDER_PART)
				.requires(AAESingletons.ADV_PATTERN_PROVIDER)
				.unlockedBy("hasItem", has(EAESingletons.EX_PATTERN_PROVIDER))
				.save(c, "advpatpropart");
		ShapedRecipeBuilder
				.shaped(RecipeCategory.MISC, AAESingletons.ADV_PATTERN_ENCODER)
				.pattern("QRQ")
				.pattern("RER")
				.pattern("QRQ")
				.define('Q', AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED)
				.define('R', Items.REDSTONE)
				.define('E', AEItems.ENGINEERING_PROCESSOR)
				.unlockedBy("hasItem", has(AEItems.BLANK_PATTERN))
				.save(c, "advpartenc");
		ShapedRecipeBuilder
				.shaped(RecipeCategory.MISC, AAESingletons.ADV_PATTERN_PROVIDER_UPGRADE)
				.pattern("IR")
				.pattern("EL")
				.define('I', Tags.Items.INGOTS)
				.define('R', Items.REDSTONE)
				.define('E', Items.ENDER_PEARL)
				.define('L', AEItems.LOGIC_PROCESSOR)
				.unlockedBy("hasItem", has(AEItems.BLANK_PATTERN))
				.save(c, "smallappupgrade");
		CrystalAssemblerRecipeBuilder
				.assemble(AAESingletons.ADV_PATTERN_PROVIDER_CAPACITY_UPGRADE)
				.input(Tags.Items.INGOTS)
				.input(AEItems.CAPACITY_CARD, 3)
				.input(Items.CRAFTING_TABLE, 3)
				.input(EAESingletons.CONCURRENT_PROCESSOR)
				.input(ConventionTags.GLASS_CABLE, 6)
				.save(c, AdvancedAE.id("largeappupgrade"));
	}
}
