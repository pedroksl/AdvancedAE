package net.pedroksl.advanced_ae.recipes;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pedroksl.advanced_ae.AdvancedAE;

public final class AAERecipeSerializers {

    private AAERecipeSerializers() {}

    public static final DeferredRegister<RecipeSerializer<?>> DR =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, AdvancedAE.MOD_ID);

    static {
        register("react", ReactionChamberRecipe.SERIALIZER);
    }

    private static void register(String id, RecipeSerializer<?> serializer) {
        DR.register(id, () -> serializer);
    }
}
