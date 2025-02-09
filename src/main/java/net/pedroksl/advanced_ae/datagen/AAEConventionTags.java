package net.pedroksl.advanced_ae.datagen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class AAEConventionTags {

    private AAEConventionTags() {}

    public static final TagKey<Item> OSMIUM_INGOT = tag("c:ingots/osmium");

    public static final TagKey<Item> CURIOS = tag("curios:curio");
    public static final TagKey<Item> ENCODER_CURIO = tag("curios:adv_pattern_encoder");

    public static final TagKey<Block> QUANTUM_ALLOY_STORAGE_BLOCK_BLOCK = blockTag("c:storage_blocks/quantum_alloy");
    public static final TagKey<Item> QUANTUM_ALLOY_STORAGE_BLOCK_ITEM = tag("c:storage_blocks/quantum_alloy");

    public static final TagKey<Item> QUANTUM_ALLOY = tag("c:ingots/quantum_alloy");
    public static final TagKey<Item> PLATES = tag("c:plates");
    public static final TagKey<Item> QUANTUM_ALLOY_PLATE = tag("c:plates/quantum_alloy");
    public static final TagKey<Item> SHATTERED_SINGULARITY = tag("c:dusts/shattered_singularity");

    private static TagKey<Item> tag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(name));
    }

    private static TagKey<Block> blockTag(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.parse(name));
    }
}
