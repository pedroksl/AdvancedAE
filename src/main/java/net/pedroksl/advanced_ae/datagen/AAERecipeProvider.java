package net.pedroksl.advanced_ae.datagen;

import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
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
				.save(consumer, "app");
		ShapedRecipeBuilder
				.shaped(RecipeCategory.MISC, AAEItemAndBlock.ADV_BLANK_PATTERN, 8)
				.pattern("PPP")
				.pattern("PLP")
				.pattern("PPP")
				.define('P', AEItems.BLANK_PATTERN)
				.define('L', AEItems.LOGIC_PROCESSOR)
				.unlockedBy("hasItem", has(AAEItemAndBlock.ADV_PATTERN_PROVIDER))
				.save(consumer, "acp");
		ShapedRecipeBuilder
				.shaped(RecipeCategory.MISC, AAEItemAndBlock.ADV_PATTERN_ENCODING_TERM)
				.pattern("RLR")
				.pattern("ETE")
				.pattern("RLR")
				.define('L', AEItems.LOGIC_PROCESSOR)
				.define('E', AEItems.ENGINEERING_PROCESSOR)
				.define('R', Items.REDSTONE)
				.define('T', AEParts.PATTERN_ENCODING_TERMINAL)
				.unlockedBy("hasItem", has(AEParts.PATTERN_ENCODING_TERMINAL))
				.save(consumer, "apet");
	}
}
