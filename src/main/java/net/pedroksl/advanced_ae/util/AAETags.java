package net.pedroksl.advanced_ae.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.pedroksl.advanced_ae.AdvancedAE;

public class AAETags {

	public static final TagKey<Item> ADV_PATTERN_PROVIDER = TagKey.create(Registries.ITEM, AdvancedAE.id(
			"adv_pattern_provider"));
}
