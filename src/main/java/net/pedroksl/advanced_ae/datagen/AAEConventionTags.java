package net.pedroksl.advanced_ae.datagen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class AAEConventionTags {

    private AAEConventionTags() {}

    /**
     * This tag contains all data component types that should be cleared from a memory card when it is
     * shift+right-clicked.
     */
    public static final TagKey<Item> OSMIUM_INGOT = tag("c:ingots/osmium");

    public static final TagKey<Item> CURIOS = tag("curios:curio");

    private static TagKey<Item> tag(String name) {
        return net.minecraft.tags.TagKey.create(Registries.ITEM, ResourceLocation.parse(name));
    }
}
