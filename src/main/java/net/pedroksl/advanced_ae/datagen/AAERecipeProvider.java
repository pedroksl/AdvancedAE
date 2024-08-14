package net.pedroksl.advanced_ae.datagen;

import appeng.core.definitions.AEItems;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
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
	}
}
