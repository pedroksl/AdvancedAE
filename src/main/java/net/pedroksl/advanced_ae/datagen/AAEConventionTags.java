package net.pedroksl.advanced_ae.datagen;

import java.util.Objects;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class AAEConventionTags {

    private AAEConventionTags() {}

    /**
     * This tag contains all data component types that should be cleared from a memory card when it is
     * shift+right-clicked.
     */
    public static final TagKey<Item> OSMIUM_INGOT = tag("c:ingots/osmium");

    public static final TagKey<Item> CURIOS = tag("curios:curio");
    public static final TagKey<Item> ENCODER_CURIO = tag("curios:adv_pattern_encoder");

    public static final TagKey<Block> QUANTUM_ALLOY_STORAGE_BLOCK_BLOCK = blockTag("c:storage_blocks/quantum_alloy");

    private static TagKey<Item> tag(String name) {
        return TagKey.create(Registries.ITEM, Objects.requireNonNull(ResourceLocation.tryParse(name)));
    }

    private static TagKey<Block> blockTag(String name) {
        return TagKey.create(Registries.BLOCK, Objects.requireNonNull(ResourceLocation.tryParse(name)));
    }
}
