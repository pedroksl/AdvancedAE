package net.pedroksl.advanced_ae.common.items;

import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.ProcessingPatternItem;
import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class AdvProcessingPatternItem extends ProcessingPatternItem {
	private static final String NBT_INPUTS = "in";
	private static final String NBT_OUTPUTS = "out";

	public AdvProcessingPatternItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack encode(GenericStack[] sparseInputs, GenericStack[] sparseOutputs) {
		if (Arrays.stream(sparseInputs).noneMatch(Objects::nonNull)) {
			throw new IllegalArgumentException("At least one input must be non-null.");
		}
		Objects.requireNonNull(sparseOutputs[0],
				"The first (primary) output must be non-null.");

		var stack = new ItemStack(this);
		encodeProcessingPattern(stack.getOrCreateTag(), sparseInputs, sparseOutputs);
		return stack;
	}

	public static void encodeProcessingPattern(CompoundTag tag, GenericStack[] sparseInputs,
	                                           GenericStack[] sparseOutputs) {
		tag.put(NBT_INPUTS, encodeStackList(sparseInputs));
		tag.put(NBT_OUTPUTS, encodeStackList(sparseOutputs));
	}

	private static ListTag encodeStackList(GenericStack[] stacks) {
		ListTag tag = new ListTag();
		boolean foundStack = false;
		for (var stack : stacks) {
			tag.add(GenericStack.writeTag(stack));
			if (stack != null && stack.amount() > 0) {
				foundStack = true;
			}
		}
		Preconditions.checkArgument(foundStack, "List passed to pattern must contain at least one stack.");
		return tag;
	}
}
