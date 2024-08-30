package net.pedroksl.advanced_ae.recipes;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;

public final class ReactionChamberRecipes {
    private ReactionChamberRecipes() {}

    public static Iterable<RecipeHolder<ReactionChamberRecipe>> getRecipes(Level level) {
        return level.getRecipeManager().byType(ReactionChamberRecipe.TYPE);
    }

    @Nullable
    public static ReactionChamberRecipe findRecipe(
            Level level, ItemStack input1, ItemStack input2, ItemStack input3, GenericStack fluid) {
        List<ItemStack> machineInputs = new ArrayList<>();
        if (!input1.isEmpty()) machineInputs.add(input1);
        if (!input2.isEmpty()) machineInputs.add(input2);
        if (!input3.isEmpty()) machineInputs.add(input3);

        for (var holder : getRecipes(level)) {
            var recipe = holder.value();

            var inputs = recipe.getInputs();

            boolean failed = false;
            for (var input : inputs) {
                boolean found = false;
                for (var machineInput : machineInputs) {
                    if (!input.getIngredient().test(machineInput) || input.getAmount() > machineInput.getCount())
                        continue;
                    found = true;
                }
                if (!found) {
                    failed = true;
                    break;
                }
            }
            if (failed) {
                continue;
            }

            if (!recipe.getFluid().isEmpty()) {
                if (fluid == null) {
                    continue;
                }

                AEKey aeKey = fluid.what();
                if (!(aeKey instanceof AEFluidKey key)) {
                    continue;
                }

                FluidStack fluidStack = key.toStack((int) fluid.amount());
                if (!recipe.getFluid().getIngredient().test(fluidStack)) {
                    continue;
                }
            }

            return recipe;
        }

        return null;
    }
}
