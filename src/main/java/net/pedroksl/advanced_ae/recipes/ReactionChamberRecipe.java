package net.pedroksl.advanced_ae.recipes;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.pedroksl.advanced_ae.AdvancedAE;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;

public class ReactionChamberRecipe implements Recipe<Container> {

    public static final ResourceLocation TYPE_ID = AdvancedAE.makeId("reaction");
    public static final RecipeType<ReactionChamberRecipe> TYPE = InitRecipeTypes.register(TYPE_ID.toString());

    public ResourceLocation id;
    public final List<IngredientStack.Item> inputs;
    public final IngredientStack.Fluid fluid;
    public final GenericStack output;

    protected final int energy;

    public ReactionChamberRecipe(
            ResourceLocation id,
            GenericStack output,
            List<IngredientStack.Item> inputs,
            IngredientStack.Fluid fluid,
            int energy) {
        this.id = id;
        this.inputs = inputs;
        this.output = output;
        this.fluid = fluid;
        this.energy = energy;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return false;
    }

    @Override
    public ItemStack assemble(Container inv, RegistryAccess registryAccess) {
        return getResultItem(registryAccess).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
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

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    public boolean containsIngredient(ItemStack stack) {
        for (var input : inputs) {
            if (!input.isEmpty() && input.test(stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsIngredient(FluidStack stack) {
        return this.fluid.test(stack);
    }
}
