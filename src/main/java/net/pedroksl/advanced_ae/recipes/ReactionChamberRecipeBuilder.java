package net.pedroksl.advanced_ae.recipes;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

public class ReactionChamberRecipeBuilder {
    private final List<IngredientStack.Item> inputs = new ArrayList<>();
    private IngredientStack.Fluid fluid = null;
    private final int energy;
    private final ItemStackTemplate itemOutput;
    private final FluidStackTemplate fluidOutput;

    public ReactionChamberRecipeBuilder(@NotNull ItemStackTemplate output, int energy) {
        this.itemOutput = output;
        this.energy = energy;
        this.fluidOutput = null;
    }

    public ReactionChamberRecipeBuilder(@NotNull FluidStackTemplate output, int energy) {
        this.fluidOutput = output;
        this.energy = energy;
        this.itemOutput = null;
    }

    public static ReactionChamberRecipeBuilder react(ItemLike stack, int energy) {
        return new ReactionChamberRecipeBuilder(new ItemStackTemplate(stack.asItem(), 1), energy);
    }

    public static ReactionChamberRecipeBuilder react(ItemLike stack, int count, int energy) {
        return new ReactionChamberRecipeBuilder(new ItemStackTemplate(stack.asItem(), count), energy);
    }

    public static ReactionChamberRecipeBuilder react(Fluid stack, int count, int energy) {
        return new ReactionChamberRecipeBuilder(new FluidStackTemplate(stack, count), energy);
    }

    public ReactionChamberRecipeBuilder fluid(Fluid fluid, int amount) {
        this.fluid = IngredientStack.of(FluidIngredient.of(fluid), amount);
        return this;
    }

    public ReactionChamberRecipeBuilder input(ItemLike item) {
        this.inputs.add(IngredientStack.of(Ingredient.of(item), 1));
        return this;
    }

    public ReactionChamberRecipeBuilder input(ItemLike item, int count) {
        this.inputs.add(IngredientStack.of(Ingredient.of(item), count));
        return this;
    }

    public ReactionChamberRecipeBuilder input(HolderSet<Item> items) {
        this.inputs.add(IngredientStack.of(Ingredient.of(items), 1));
        return this;
    }

    public ReactionChamberRecipeBuilder input(HolderSet<Item> items, int count) {
        this.inputs.add(IngredientStack.of(Ingredient.of(items), count));
        return this;
    }

    public void save(RecipeOutput consumer, Identifier id) {
        var recipe = new ReactionChamberRecipe(this.itemOutput, this.fluidOutput, this.inputs, this.fluid, this.energy);
        consumer.accept(ResourceKey.create(Registries.RECIPE, id), recipe, null);
    }

    public void save(RecipeOutput consumer, String id) {
        this.save(consumer, AdvancedAE.makeId(id));
    }
}
