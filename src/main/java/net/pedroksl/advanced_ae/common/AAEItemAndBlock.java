package net.pedroksl.advanced_ae.common;

import appeng.items.materials.MaterialItem;
import appeng.items.parts.PartItem;
import net.minecraft.world.item.Item;
import net.pedroksl.advanced_ae.common.blocks.AdvPatternProviderBlock;
import net.pedroksl.advanced_ae.common.entities.AdvPatternProviderEntity;
import net.pedroksl.advanced_ae.common.patterns.AdvCraftingPatternItem;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPatternItem;
import net.pedroksl.advanced_ae.common.patterns.AdvSmithingPatternItem;
import net.pedroksl.advanced_ae.common.patterns.AdvStonecuttingPatternItem;
import net.pedroksl.advanced_ae.common.parts.AdvPatternEncodingTermPart;

public class AAEItemAndBlock {

	public static AdvPatternProviderBlock ADV_PATTERN_PROVIDER;

	public static MaterialItem ADV_BLANK_PATTERN;
	public static AdvCraftingPatternItem ADV_CRAFTING_PATTERN;
	public static AdvProcessingPatternItem ADV_PROCESSING_PATTERN;
	public static AdvSmithingPatternItem ADV_SMITHING_PATTERN;
	public static AdvStonecuttingPatternItem ADV_STONECUTTING_PATTERN;

	public static PartItem<AdvPatternEncodingTermPart> ADV_PATTERN_ENCODING_TERM;

	public static void init(AAERegistryHandler handler) {
		ADV_PATTERN_PROVIDER = new AdvPatternProviderBlock();

		ADV_PATTERN_ENCODING_TERM = new PartItem<>(new Item.Properties(), AdvPatternEncodingTermPart.class, AdvPatternEncodingTermPart::new);

		ADV_BLANK_PATTERN = new MaterialItem(new Item.Properties());
		ADV_CRAFTING_PATTERN = new AdvCraftingPatternItem(new Item.Properties().stacksTo(1));
		ADV_PROCESSING_PATTERN = new AdvProcessingPatternItem(new Item.Properties().stacksTo(1));
		ADV_SMITHING_PATTERN = new AdvSmithingPatternItem(new Item.Properties().stacksTo(1));
		ADV_STONECUTTING_PATTERN = new AdvStonecuttingPatternItem(new Item.Properties().stacksTo(1));


		handler.block("adv_pattern_provider", ADV_PATTERN_PROVIDER, AdvPatternProviderEntity.class,
				AdvPatternProviderEntity::new);

		handler.item("adv_pattern_encoding_terminal_part", ADV_PATTERN_ENCODING_TERM);

		handler.item("adv_blank_pattern", ADV_BLANK_PATTERN);
		handler.item("adv_crafting_pattern", ADV_CRAFTING_PATTERN);
		handler.item("adv_processing_pattern", ADV_PROCESSING_PATTERN);
		handler.item("adv_smithing_pattern", ADV_SMITHING_PATTERN);
		handler.item("adv_stonecutting_pattern", ADV_STONECUTTING_PATTERN);
	}
}
