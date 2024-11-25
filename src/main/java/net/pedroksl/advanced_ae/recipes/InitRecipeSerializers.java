package net.pedroksl.advanced_ae.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.IForgeRegistry;

public final class InitRecipeSerializers {

    private InitRecipeSerializers() {}

    public static void init(IForgeRegistry<RecipeSerializer<?>> registry) {
        register(registry, ReactionChamberRecipe.TYPE_ID, ReactionChamberRecipeSerializer.INSTANCE);
    }

    private static void register(
            IForgeRegistry<RecipeSerializer<?>> registry, ResourceLocation id, RecipeSerializer<?> serializer) {
        registry.register(id, serializer);
    }
}
