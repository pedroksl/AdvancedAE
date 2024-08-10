package net.pedroksl.advanced_ae.common.patterns;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.AAEItemAndBlock;

public final class AdvPatternDetailsEncoder {

	public static ItemStack encodeCraftingPatternDirections(ItemStack pattern, Direction[] fromSides) {
		ItemStack craftingPattern = new ItemStack(AAEItemAndBlock.ADV_CRAFTING_PATTERN);
		craftingPattern.setTag(pattern.getOrCreateTag());
		AdvPatternEncoding.encodeDirectionList(craftingPattern.getOrCreateTag(), fromSides);
		return craftingPattern;
	}

	public static ItemStack encodeProcessingPatternDirections(ItemStack pattern, Direction[] fromSides) {
		ItemStack processingPattern = new ItemStack(AAEItemAndBlock.ADV_PROCESSING_PATTERN);
		processingPattern.setTag(pattern.getOrCreateTag());
		AdvPatternEncoding.encodeDirectionList(processingPattern.getOrCreateTag(), fromSides);
		return processingPattern;
	}

	public static ItemStack encodeSmithingPatternDirections(ItemStack pattern, Direction[] fromSides) {
		ItemStack smithingPattern = new ItemStack(AAEItemAndBlock.ADV_SMITHING_PATTERN);
		smithingPattern.setTag(pattern.getOrCreateTag());
		AdvPatternEncoding.encodeDirectionList(smithingPattern.getOrCreateTag(), fromSides);
		return smithingPattern;
	}

	public static ItemStack encodeStonecuttingPatternDirections(ItemStack pattern, Direction[] fromSides) {
		ItemStack stonecuttingPattern = new ItemStack(AAEItemAndBlock.ADV_STONECUTTING_PATTERN);
		stonecuttingPattern.setTag(pattern.getOrCreateTag());
		AdvPatternEncoding.encodeDirectionList(stonecuttingPattern.getOrCreateTag(), fromSides);
		return stonecuttingPattern;
	}
}