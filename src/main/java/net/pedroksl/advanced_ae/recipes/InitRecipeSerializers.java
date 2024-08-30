package net.pedroksl.advanced_ae.recipes;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class InitRecipeSerializers {

    private InitRecipeSerializers() {}

    public static void init(Registry<RecipeSerializer<?>> registry) {
        register(registry, ReactionChamberRecipe.TYPE_ID, ReactionChamberRecipeSerializer.INSTANCE);
    }

    private static void register(
            Registry<RecipeSerializer<?>> registry, ResourceLocation id, RecipeSerializer<?> serializer) {
        Registry.register(registry, id, serializer);
    }
}
