package net.pedroksl.advanced_ae.xmod.emi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.recipes.ReactionChamberRecipe;
import net.pedroksl.advanced_ae.xmod.emi.recipes.EMIReactionChamberRecipe;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;

@EmiEntrypoint
public class EMIPlugin implements EmiPlugin {
    public static final ResourceLocation TEXTURE = AdvancedAE.makeId("textures/guis/emi.png");

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(EMIReactionChamberRecipe.CATEGORY);
        registry.addWorkstation(EMIReactionChamberRecipe.CATEGORY, EmiStack.of(AAEBlocks.REACTION_CHAMBER.stack()));
        adaptRecipeType(registry, ReactionChamberRecipe.TYPE, EMIReactionChamberRecipe::new);
        addInfo(registry, AAEBlocks.ADV_PATTERN_PROVIDER, AAEText.AdvPatternProviderEmiDesc.text());
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

    public static EmiIngredient stackOf(IngredientStack.Item stack) {
        return !stack.isEmpty() ? EmiIngredient.of(stack.getIngredient(), stack.getAmount()) : EmiStack.EMPTY;
    }

    public static EmiIngredient stackOf(IngredientStack.Fluid stack) {
        FluidIngredient ingredient = stack.getIngredient();
        List<EmiIngredient> list = new ArrayList<>();
        FluidStack[] stacks = ingredient.getStacks();
        for (FluidStack fluid : stacks) {
            list.add(EmiStack.of(fluid.getFluid(), stack.getAmount()));
        }

        return EmiIngredient.of(list, stack.getAmount());
    }
}
