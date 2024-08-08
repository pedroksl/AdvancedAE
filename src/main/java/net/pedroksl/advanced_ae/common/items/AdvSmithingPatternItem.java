package net.pedroksl.advanced_ae.common.items;

import appeng.api.stacks.AEItemKey;
import appeng.crafting.pattern.SmithingTablePatternItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipe;

public class AdvSmithingPatternItem extends SmithingTablePatternItem {
	private static final String NBT_TEMPLATE = "template";
	private static final String NBT_BASE = "base";
	private static final String NBT_ADDITION = "addition";
	// Only used to attempt to recover the recipe in case it's ID has changed
	private static final String NBT_OUTPUT = "out";
	private static final String NBT_SUBSITUTE = "substitute";
	private static final String NBT_RECIPE_ID = "recipe";

	public AdvSmithingPatternItem(Properties properties) {
		super(properties);
	}

	public ItemStack encode(SmithingRecipe recipe, AEItemKey template, AEItemKey base, AEItemKey addition,
	                        AEItemKey out, boolean allowSubstitutes) {
		var stack = new ItemStack(this);
		encodeSmithingPattern(stack.getOrCreateTag(), recipe, template, base, addition,
				out,
				allowSubstitutes);
		return stack;
	}

	public static void encodeSmithingPattern(CompoundTag tag, SmithingRecipe recipe, AEItemKey template, AEItemKey base,
	                                         AEItemKey addition, AEItemKey output, boolean allowSubstitution) {
		tag.put(NBT_TEMPLATE, template.toTag());
		tag.put(NBT_BASE, base.toTag());
		tag.put(NBT_ADDITION, addition.toTag());
		tag.put(NBT_OUTPUT, output.toTag());
		tag.putBoolean(NBT_SUBSITUTE, allowSubstitution);
		tag.putString(NBT_RECIPE_ID, recipe.getId().toString());
	}
}
