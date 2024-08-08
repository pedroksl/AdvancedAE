package net.pedroksl.advanced_ae.common.items;

import appeng.api.stacks.AEItemKey;
import appeng.crafting.pattern.StonecuttingPatternItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public class AdvStonecuttingPatternItem extends StonecuttingPatternItem {
	private static final String NBT_INPUT = "in";
	// Only used to attempt to recover the recipe in case it's ID has changed
	private static final String NBT_OUTPUT = "out";
	private static final String NBT_SUBSITUTE = "substitute";
	private static final String NBT_RECIPE_ID = "recipe";

	public AdvStonecuttingPatternItem(Properties properties) {
		super(properties);
	}

	public ItemStack encode(StonecutterRecipe recipe, AEItemKey in, AEItemKey out, boolean allowSubstitutes) {
		var stack = new ItemStack(this);
		encodeStonecuttingPattern(stack.getOrCreateTag(), recipe, in, out, allowSubstitutes);
		return stack;
	}

	public static void encodeStonecuttingPattern(CompoundTag tag, StonecutterRecipe recipe, AEItemKey input,
	                                             AEItemKey output, boolean allowSubstitution) {
		tag.put(NBT_INPUT, input.toTag());
		tag.put(NBT_OUTPUT, output.toTag());
		tag.putBoolean(NBT_SUBSITUTE, allowSubstitution);
		tag.putString(NBT_RECIPE_ID, recipe.getId().toString());
	}
}
