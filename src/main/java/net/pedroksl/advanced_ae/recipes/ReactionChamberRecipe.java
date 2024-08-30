package net.pedroksl.advanced_ae.recipes;

import java.util.List;
import java.util.Optional;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.AdvancedAE;

public class ReactionChamberRecipe implements Recipe<RecipeInput> {

    public static final ResourceLocation TYPE_ID = AdvancedAE.makeId("inscriber");
    public static final RecipeType<ReactionChamberRecipe> TYPE = InitRecipeTypes.register(TYPE_ID.toString());

    protected final List<IngredientStack.Item> inputs;
    protected final Optional<IngredientStack.Fluid> fluid;
    protected final ItemStack output;

    public ReactionChamberRecipe(ItemStack output, List<IngredientStack.Item> inputs, IngredientStack.Fluid fluid) {
        this.inputs = inputs;
        this.output = output;
        this.fluid = Optional.ofNullable(fluid);
    }

    public ReactionChamberRecipe(
            ItemStack output, List<IngredientStack.Item> inputs, Optional<IngredientStack.Fluid> fluid) {
        this.output = output;
        this.inputs = inputs;
        this.fluid = fluid;
    }

    @Override
    public boolean matches(@NotNull RecipeInput recipeInput, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput inv, HolderLookup.@NotNull Provider registries) {
        return getResultItem(registries).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return getResultItem();
    }

    public ItemStack getResultItem() {
        return this.output;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ReactionChamberRecipeSerializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return TYPE;
    }

    public List<IngredientStack.Item> getInputs() {
        return inputs;
    }

    @Nullable
    public IngredientStack.@Nullable Fluid getFluid() {
        return this.fluid.orElse(null);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
