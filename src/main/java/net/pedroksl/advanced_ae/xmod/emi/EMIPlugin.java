package net.pedroksl.advanced_ae.xmod.emi;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.recipes.ReactionChamberRecipe;
import net.pedroksl.advanced_ae.xmod.emi.recipes.EMIReactionChamberRecipe;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;

@EmiEntrypoint
public class EMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(EMIReactionChamberRecipe.CATEGORY);
        registry.addWorkstation(EMIReactionChamberRecipe.CATEGORY, EmiStack.of(AAEBlocks.REACTION_CHAMBER.stack()));
        adaptRecipeType(registry, ReactionChamberRecipe.TYPE, EMIReactionChamberRecipe::new);
    }

    private static <C extends RecipeInput, T extends Recipe<C>> void adaptRecipeType(
            EmiRegistry registry, RecipeType<T> recipeType, Function<RecipeHolder<T>, ? extends EmiRecipe> adapter) {
        registry.getRecipeManager().getAllRecipesFor(recipeType).stream()
                .map(adapter)
                .forEach(registry::addRecipe);
    }

    private static void addInfo(EmiRegistry registry, ItemLike item, Component... desc) {
        registry.addRecipe(new EmiInfoRecipe(
                List.of(EmiStack.of(item)), Arrays.stream(desc).toList(), null));
    }
}
