package net.pedroksl.advanced_ae.recipes;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;

public final class ReactionChamberRecipes {
    private ReactionChamberRecipes() {}

    public static Iterable<ReactionChamberRecipe> getRecipes(Level level) {
        return level.getRecipeManager().byType(ReactionChamberRecipe.TYPE).values();
    }

    @Nullable
    public static ReactionChamberRecipe findRecipe(Level level, List<ItemStack> inputs, GenericStack fluid) {
        List<ItemStack> machineInputs = new ArrayList<>();
        for (var stack : inputs)
            if (!stack.isEmpty()) {
                machineInputs.add(stack);
            }

        for (var recipe : getRecipes(level)) {
            var validInputs = recipe.getValidInputs();

            boolean failed = false;
            for (var input : validInputs) {
                boolean found = false;
                for (var machineInput : machineInputs) {
                    if (input.checkType(machineInput)) {
                        if (((IngredientStack.Item) input).test(machineInput)
                                && input.getAmount() <= machineInput.getCount()) {
                            found = true;
                            break;
                        }
                    }
                }

                if (input instanceof IngredientStack.Fluid fluidIn) {
                    if (fluid != null && fluid.what() instanceof AEFluidKey key) {
                        FluidStack fluidStack = key.toStack((int) fluid.amount());
                        if (fluidIn.test(fluidStack) && input.getAmount() <= fluid.amount()) {
                            found = true;
                        }
                    }
                }

                if (!found) {
                    failed = true;
                    break;
                }
            }
            if (failed) {
                continue;
            }

            return recipe;
        }

        return null;
    }

    public static boolean isValidIngredient(ItemStack stack, Level level) {
        for (var recipe : getRecipes(level)) {
            if (recipe.containsIngredient(stack)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidIngredient(FluidStack stack, Level level) {
        for (var recipe : getRecipes(level)) {
            if (recipe.containsIngredient(stack)) {
                return true;
            }
        }
        return false;
    }
}
