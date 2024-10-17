package net.pedroksl.advanced_ae.recipes;

import java.util.ArrayList;
import java.util.List;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.pedroksl.advanced_ae.AdvancedAE;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;

public class ReactionChamberRecipe implements Recipe<RecipeInput> {

    public static final ResourceLocation TYPE_ID = AdvancedAE.makeId("reaction");
    public static final RecipeType<ReactionChamberRecipe> TYPE = InitRecipeTypes.register(TYPE_ID.toString());

    protected final List<IngredientStack.Item> inputs;
    protected final IngredientStack.Fluid fluid;
    public final GenericStack output;

    protected final int energy;

    public ReactionChamberRecipe(
            GenericStack output, List<IngredientStack.Item> inputs, IngredientStack.Fluid fluid, int energy) {
        this.inputs = inputs;
        this.output = output;
        this.fluid = fluid;
        this.energy = energy;
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

    public boolean isItemOutput() {
        return this.output.what() instanceof AEItemKey;
    }

    public ItemStack getResultItem() {
        if (this.output.what() instanceof AEItemKey key) {
            return key.toStack((int) this.output.amount());
        }
        return ItemStack.EMPTY;
    }

    public FluidStack getResultFluid() {
        if (this.output.what() instanceof AEFluidKey key) {
            return key.toStack((int) this.output.amount());
        }
        return FluidStack.EMPTY;
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

    public List<IngredientStack<?, ?>> getValidInputs() {
        List<IngredientStack<?, ?>> validInputs = new ArrayList<>();

        for (var input : this.inputs) {
            if (!input.isEmpty()) {
                validInputs.add(input.sample());
            }
        }

        validInputs.add(this.fluid.sample());
        return validInputs;
    }

    @Nullable
    public IngredientStack.Fluid getFluid() {
        return this.fluid;
    }

    public int getEnergy() {
        return this.energy;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public boolean containsIngredient(ItemStack stack) {
        for (var input : inputs) {
            if (!input.isEmpty() && input.getIngredient().test(stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsIngredient(FluidStack stack) {
        return this.fluid.getIngredient().test(stack);
    }
}
