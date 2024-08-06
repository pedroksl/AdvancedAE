package net.pedroksl.advanced_ae.common;

import net.pedroksl.advanced_ae.common.blocks.AdvPatternProviderBlock;
import net.pedroksl.advanced_ae.common.entities.AdvPatternProviderEntity;

public class AAEItemAndBlock {

	public static AdvPatternProviderBlock ADV_PATTERN_PROVIDER;

	public static void init(AAERegistryHandler handler) {
		ADV_PATTERN_PROVIDER = new AdvPatternProviderBlock();


		handler.block("adv_pattern_provider", ADV_PATTERN_PROVIDER, AdvPatternProviderEntity.class,
				AdvPatternProviderEntity::new);
	}
}
