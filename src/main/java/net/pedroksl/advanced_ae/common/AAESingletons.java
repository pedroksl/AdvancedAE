package net.pedroksl.advanced_ae.common;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.items.parts.PartItem;
import com.glodblock.github.glodium.util.GlodCodecs;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.pedroksl.advanced_ae.common.blocks.AdvPatternProviderBlock;
import net.pedroksl.advanced_ae.common.entities.AdvPatternProviderEntity;
import net.pedroksl.advanced_ae.common.items.AdvPatternEncoderItem;
import net.pedroksl.advanced_ae.common.parts.AdvPatternProviderPart;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPattern;
import net.pedroksl.advanced_ae.common.patterns.EncodedAdvProcessingPattern;

public class AAESingletons {

	public static DataComponentType<EncodedAdvProcessingPattern> ENCODED_ADV_PROCESSING_PATTERN;
	public static DataComponentType<CompoundTag> STACK_TAG;

	public static AdvPatternProviderBlock ADV_PATTERN_PROVIDER;
	public static PartItem<AdvPatternProviderPart> ADV_PATTERN_PROVIDER_PART;

	public static Item ADV_PROCESSING_PATTERN;

	public static AdvPatternEncoderItem ADV_PATTERN_ENCODER;

	public static void init(AAERegistryHandler handler) {
		STACK_TAG = GlodUtil.getComponentType(CompoundTag.CODEC, GlodCodecs.NBT_STREAM_CODEC);
		ENCODED_ADV_PROCESSING_PATTERN = GlodUtil.getComponentType(EncodedAdvProcessingPattern.CODEC,
				EncodedAdvProcessingPattern.STREAM_CODEC);
		ADV_PATTERN_PROVIDER = new AdvPatternProviderBlock();
		ADV_PATTERN_PROVIDER_PART = new PartItem<>(new Item.Properties(), AdvPatternProviderPart.class,
				AdvPatternProviderPart::new);

		ADV_PROCESSING_PATTERN =
				PatternDetailsHelper.encodedPatternItemBuilder(AdvProcessingPattern::new)
						.invalidPatternTooltip(AdvProcessingPattern::getInvalidPatternTooltip).build();

		ADV_PATTERN_ENCODER = new AdvPatternEncoderItem();


		handler.comp("encoded_adv_processing_pattern", ENCODED_ADV_PROCESSING_PATTERN);
		handler.comp("generic_nbt", STACK_TAG);

		handler.block("adv_pattern_provider", ADV_PATTERN_PROVIDER, AdvPatternProviderEntity.class,
				AdvPatternProviderEntity::new);
		handler.item("adv_pattern_provider_part", ADV_PATTERN_PROVIDER_PART);

		handler.item("adv_processing_pattern", ADV_PROCESSING_PATTERN);
		handler.item("adv_pattern_encoder", ADV_PATTERN_ENCODER);
	}
}
