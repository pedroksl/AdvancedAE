package net.pedroksl.advanced_ae.common;

import appeng.items.parts.PartItem;
import net.minecraft.world.item.Item;
import net.pedroksl.advanced_ae.common.blocks.AdvPatternProviderBlock;
import net.pedroksl.advanced_ae.common.entities.AdvPatternProviderEntity;
import net.pedroksl.advanced_ae.common.items.AdvPatternEncoderItem;
import net.pedroksl.advanced_ae.common.parts.AdvPatternProviderPart;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPatternItem;

public class AAEItemAndBlock {

	public static AdvPatternProviderBlock ADV_PATTERN_PROVIDER;
	public static PartItem<AdvPatternProviderPart> ADV_PATTERN_PROVIDER_PART;

	public static AdvProcessingPatternItem ADV_PROCESSING_PATTERN;
	public static AdvPatternEncoderItem ADV_PATTERN_ENCODER;

	public static void init(AAERegistryHandler handler) {
		ADV_PATTERN_PROVIDER = new AdvPatternProviderBlock();
		ADV_PATTERN_PROVIDER_PART = new PartItem<>(new Item.Properties(), AdvPatternProviderPart.class,
				AdvPatternProviderPart::new);

		ADV_PROCESSING_PATTERN = new AdvProcessingPatternItem(new Item.Properties().stacksTo(1));
		ADV_PATTERN_ENCODER = new AdvPatternEncoderItem();


		handler.block("adv_pattern_provider", ADV_PATTERN_PROVIDER, AdvPatternProviderEntity.class,
				AdvPatternProviderEntity::new);
		handler.item("adv_pattern_provider_part", ADV_PATTERN_PROVIDER_PART);

		handler.item("adv_processing_pattern", ADV_PROCESSING_PATTERN);
		handler.item("adv_pattern_encoder", ADV_PATTERN_ENCODER);
	}
}
