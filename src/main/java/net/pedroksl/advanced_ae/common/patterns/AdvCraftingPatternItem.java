package net.pedroksl.advanced_ae.common.patterns;

import appeng.crafting.pattern.CraftingPatternItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;

public class AdvCraftingPatternItem extends CraftingPatternItem {
	public AdvCraftingPatternItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack encode(CraftingRecipe recipe, ItemStack[] in, ItemStack out, boolean allowSubstitutes,
	                        boolean allowFluidSubstitutes) {
		var stack = new ItemStack(this);
		AdvCraftingPatternEncoding.encodeCraftingPattern(stack.getOrCreateTag(), recipe, in, out, allowSubstitutes,
				allowFluidSubstitutes);
		return stack;
	}

	public ItemStack encode(CraftingRecipe recipe, ItemStack[] in, ItemStack out, boolean allowSubstitutes,
	                        boolean allowFluidSubstitutes, Direction[] sides) {
		var stack = new ItemStack(this);
		AdvCraftingPatternEncoding.encodeCraftingPattern(stack.getOrCreateTag(), recipe, in, out, allowSubstitutes,
				allowFluidSubstitutes, sides);
		return stack;
	}
}
