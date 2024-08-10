package net.pedroksl.advanced_ae.common;

import appeng.items.parts.PartItem;
import net.minecraft.world.item.Item;
import net.pedroksl.advanced_ae.common.blocks.AdvPatternProviderBlock;
import net.pedroksl.advanced_ae.common.entities.AdvPatternProviderEntity;
import net.pedroksl.advanced_ae.common.items.AdvPatternEncoderItem;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPatternItem;
import net.pedroksl.advanced_ae.common.parts.AdvPatternEncodingTermPart;

public class AAEItemAndBlock {

	public static AdvPatternProviderBlock ADV_PATTERN_PROVIDER;

	public static AdvProcessingPatternItem ADV_PROCESSING_PATTERN;
	public static AdvPatternEncoderItem ADV_PATTERN_ENCODER;

	public static PartItem<AdvPatternEncodingTermPart> ADV_PATTERN_ENCODING_TERM;

	public static void init(AAERegistryHandler handler) {
		ADV_PATTERN_PROVIDER = new AdvPatternProviderBlock();

		ADV_PATTERN_ENCODING_TERM = new PartItem<>(new Item.Properties(), AdvPatternEncodingTermPart.class, AdvPatternEncodingTermPart::new);

		ADV_PROCESSING_PATTERN = new AdvProcessingPatternItem(new Item.Properties().stacksTo(1));
		ADV_PATTERN_ENCODER = new AdvPatternEncoderItem();


		handler.block("adv_pattern_provider", ADV_PATTERN_PROVIDER, AdvPatternProviderEntity.class,
				AdvPatternProviderEntity::new);

		handler.item("adv_pattern_encoding_terminal_part", ADV_PATTERN_ENCODING_TERM);

		handler.item("adv_processing_pattern", ADV_PROCESSING_PATTERN);
		handler.item("adv_pattern_encoder", ADV_PATTERN_ENCODER);
	}
}
