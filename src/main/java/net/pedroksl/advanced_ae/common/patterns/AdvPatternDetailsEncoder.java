package net.pedroksl.advanced_ae.common.patterns;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.AAEItemAndBlock;

public final class AdvPatternDetailsEncoder {

	public static ItemStack encodeProcessingPatternDirections(ItemStack pattern, Direction[] fromSides) {
		ItemStack processingPattern = new ItemStack(AAEItemAndBlock.ADV_PROCESSING_PATTERN);
		processingPattern.setTag(pattern.getOrCreateTag());
		AdvPatternEncoding.encodeDirectionList(processingPattern.getOrCreateTag(), fromSides);
		return processingPattern;
	}
}