package net.pedroksl.advanced_ae.xmod.jei;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.recipes.AAERecipeTypes;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static final Identifier TEXTURE = AdvancedAE.makeId("textures/guis/emi.png");

    private static final Identifier ID = AdvancedAE.makeId("core");

    public JEIPlugin() {}

    @Override
    public Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        var jeiHelpers = registry.getJeiHelpers();
        registry.addRecipeCategories(new ReactionChamberCategory(jeiHelpers));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        addSyncedRecipes(registration, ReactionChamberCategory.RECIPE_TYPE, AAERecipeTypes.REACTION_CHAMBER);
    }

    private static <I extends RecipeInput, T extends Recipe<I>> void addSyncedRecipes(
            IRecipeRegistration registration,
            IRecipeType<RecipeHolder<T>> recipeType,
            RecipeType<T> vanillaRecipeType) {
        var recipes = AdvancedAE.instance().getRecipeMapForType(Minecraft.getInstance().level, vanillaRecipeType);
        var recipeHolders = List.copyOf(recipes.byType(vanillaRecipeType));
        registration.addRecipes(recipeType, recipeHolders);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        var chamber = AAEBlocks.REACTION_CHAMBER.stack();
        registration.addCraftingStation(ReactionChamberCategory.RECIPE_TYPE, chamber);
    }

    public static List<ItemStack> stackOf(IngredientStack.Item stack) {
        if (!stack.isEmpty()) {
            return stack.getIngredient().getValues().stream()
                    .map(item -> new ItemStack(item, stack.getAmount()))
                    .toList();
        }
        return List.of();
    }

    public static List<FluidStack> stackOf(IngredientStack.Fluid stack) {
        FluidIngredient ingredient = stack.getIngredient();
        return ingredient.fluids().stream()
                .map(fluid -> new FluidStack(fluid, stack.getAmount()))
                .toList();
    }
}
