package net.pedroksl.advanced_ae.common.helpers;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.google.common.base.Preconditions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.pedroksl.advanced_ae.common.AAEItemAndBlock;

public final class PatternDetailsEncoder {


	/**
	 * Encodes a processing pattern which represents the ability to convert the given inputs into the given outputs
	 * using some process external to the ME system.
	 *
	 * @param out The first element is considered the primary output and must be present
	 * @return A new encoded pattern.
	 * @throws IllegalArgumentException If either in or out contain only empty ItemStacks, or no primary output
	 */
	public static ItemStack encodeProcessingPattern(GenericStack[] in, GenericStack[] out) {
		return AAEItemAndBlock.ADV_PROCESSING_PATTERN.encode(in, out);
	}

	/**
	 * Encodes a crafting pattern which represents a Vanilla crafting recipe.
	 *
	 * @param recipe                The Vanilla crafting recipe to be encoded.
	 * @param in                    The items in the crafting grid, which are used to determine what items are supplied
	 *                              from the ME system to craft using this pattern.
	 * @param out                   What is to be expected as the result of this crafting operation by the ME system.
	 * @param allowSubstitutes      Controls whether the ME system will allow the use of equivalent items to craft this
	 *                              recipe.
	 * @param allowFluidSubstitutes Controls whether the ME system will allow the use of equivalent fluids.
	 * @throws IllegalArgumentException If either in or out contain only empty ItemStacks.
	 */
	public static ItemStack encodeCraftingPattern(CraftingRecipe recipe, ItemStack[] in,
	                                              ItemStack out, boolean allowSubstitutes, boolean allowFluidSubstitutes) {
		return AAEItemAndBlock.ADV_CRAFTING_PATTERN.encode(recipe, in, out, allowSubstitutes,
				allowFluidSubstitutes);
	}

	/**
	 * Encodes a stonecutting pattern which represents a Vanilla Stonecutter recipe.
	 *
	 * @param recipe           The Vanilla stonecutter recipe to be encoded.
	 * @param in               The input item for the stonecutter, which is used to determine which item is supplied
	 *                         from the ME system to craft using this pattern.
	 * @param out              The selected output item from the stonecutter recipe. Used to restore the recipe if it is
	 *                         renamed later.
	 * @param allowSubstitutes Controls whether the ME system will allow the use of equivalent items to craft this
	 *                         recipe.
	 */
	public static ItemStack encodeStonecuttingPattern(StonecutterRecipe recipe, AEItemKey in, AEItemKey out,
	                                                  boolean allowSubstitutes) {
		Preconditions.checkNotNull(recipe, "recipe");
		Preconditions.checkNotNull(in, "in");
		Preconditions.checkNotNull(out, "out");
		return AAEItemAndBlock.ADV_STONECUTTING_PATTERN.encode(recipe, in, out, allowSubstitutes);
	}

	/**
	 * Encodes a smithing table pattern which represents a Vanilla Smithing Table recipe.
	 *
	 * @param recipe           The Vanilla smithing table recipe to be encoded.
	 * @param template         The template item for the smithing table.
	 * @param base             The base item for the smithing table, which is used to determine which item is supplied
	 *                         from the ME system to craft using this pattern.
	 * @param addition         The additional item for the smithing table, which is used to determine which item is
	 *                         supplied from the ME system to craft using this pattern.
	 * @param out              The selected output item from the smithing table recipe. Used to restore the recipe if it
	 *                         is renamed later.
	 * @param allowSubstitutes Controls whether the ME system will allow the use of equivalent items to craft this
	 *                         recipe.
	 */
	public static ItemStack encodeSmithingTablePattern(SmithingRecipe recipe,
	                                                   AEItemKey template,
	                                                   AEItemKey base,
	                                                   AEItemKey addition,
	                                                   AEItemKey out,
	                                                   boolean allowSubstitutes) {
		Preconditions.checkNotNull(recipe, "recipe");
		Preconditions.checkNotNull(recipe, "template");
		Preconditions.checkNotNull(base, "base");
		Preconditions.checkNotNull(addition, "addition");
		Preconditions.checkNotNull(out, "out");
		return AAEItemAndBlock.ADV_SMITHING_PATTERN.encode(recipe, template, base, addition, out,
				allowSubstitutes);
	}
}