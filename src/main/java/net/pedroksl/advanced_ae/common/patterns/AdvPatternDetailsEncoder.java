package net.pedroksl.advanced_ae.common.patterns;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.AAEItemAndBlock;

import java.util.HashMap;

public final class AdvPatternDetailsEncoder {

	public static ItemStack encodeProcessingPattern(GenericStack[] sparseInputs, GenericStack[] sparseOutputs,
	                                                HashMap<AEKey, Direction> dirMap) {
		ItemStack pattern = new ItemStack(AAEItemAndBlock.ADV_PROCESSING_PATTERN);
		AdvPatternEncoding.encodeProcessingPattern(pattern.getOrCreateTag(), sparseInputs, sparseOutputs, dirMap);
		return pattern;
	}
}