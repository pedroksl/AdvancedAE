package net.pedroksl.advanced_ae.common.items;

import appeng.crafting.pattern.CraftingPatternItem;
import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;

public class AdvCraftingPatternItem extends CraftingPatternItem {
	private static final String NBT_INPUTS = "in";
	private static final String NBT_OUTPUTS = "out";
	private static final String NBT_SUBSITUTE = "substitute";
	private static final String NBT_SUBSITUTE_FLUIDS = "substituteFluids";
	private static final String NBT_RECIPE_ID = "recipe";

	public AdvCraftingPatternItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack encode(CraftingRecipe recipe, ItemStack[] in, ItemStack out, boolean allowSubstitutes,
	                        boolean allowFluidSubstitutes) {
		var stack = new ItemStack(this);
		encodeCraftingPattern(stack.getOrCreateTag(), recipe, in, out, allowSubstitutes,
				allowFluidSubstitutes);
		return stack;
	}

	public static void encodeCraftingPattern(CompoundTag tag, CraftingRecipe recipe, ItemStack[] sparseInputs,
	                                         ItemStack output, boolean allowSubstitution, boolean allowFluidSubstitution) {
		tag.put(NBT_INPUTS, encodeItemStackList(sparseInputs));
		tag.putBoolean(NBT_SUBSITUTE, allowSubstitution);
		tag.putBoolean(NBT_SUBSITUTE_FLUIDS, allowFluidSubstitution);
		tag.put(NBT_OUTPUTS, output.save(new CompoundTag()));
		tag.putString(NBT_RECIPE_ID, recipe.getId().toString());
	}

	private static ListTag encodeItemStackList(ItemStack[] stacks) {
		ListTag tag = new ListTag();
		boolean foundStack = false;
		for (var stack : stacks) {
			if (stack.isEmpty()) {
				tag.add(new CompoundTag());
			} else {
				tag.add(stack.save(new CompoundTag()));
				foundStack = true;
			}
		}
		Preconditions.checkArgument(foundStack, "List passed to pattern must contain at least one stack.");
		return tag;
	}
}
