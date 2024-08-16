package net.pedroksl.advanced_ae.common.patterns;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.AAESingletons;

import java.util.HashMap;
import java.util.List;

public final class AdvPatternDetailsEncoder {

	public static ItemStack encodeProcessingPattern(List<GenericStack> sparseInputs, List<GenericStack> sparseOutputs,
	                                                HashMap<AEKey, Direction> dirMap) {
		ItemStack stack = new ItemStack(AAESingletons.ADV_PROCESSING_PATTERN);
		AdvProcessingPattern.encode(stack, sparseInputs, sparseOutputs, dirMap);
		return stack;
	}
}