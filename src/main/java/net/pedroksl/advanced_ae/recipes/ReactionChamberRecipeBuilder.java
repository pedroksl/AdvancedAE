package net.pedroksl.advanced_ae.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import appeng.api.stacks.GenericStack;

public class ReactionChamberRecipeBuilder {
    private final List<IngredientStack.Item> inputs = new ArrayList<>();
    private IngredientStack.Fluid fluid = null;
    private final int energy;
    private final GenericStack output;

    public ReactionChamberRecipeBuilder(@NotNull GenericStack output, int energy) {
        this.output = output;
        this.energy = energy;
    }

    public static ReactionChamberRecipeBuilder react(ItemStack stack, int energy) {
        return new ReactionChamberRecipeBuilder(Objects.requireNonNull(GenericStack.fromItemStack(stack)), energy);
    }

    public static ReactionChamberRecipeBuilder react(ItemLike stack, int energy) {
        return react(new ItemStack(stack), energy);
    }

    public static ReactionChamberRecipeBuilder react(ItemLike stack, int count, int energy) {
        return react(new ItemStack(stack, count), energy);
    }

    public static ReactionChamberRecipeBuilder react(FluidStack stack, int energy) {
        return new ReactionChamberRecipeBuilder(Objects.requireNonNull(GenericStack.fromFluidStack(stack)), energy);
    }

    public static ReactionChamberRecipeBuilder react(Fluid stack, int energy) {
        return react(new FluidStack(stack, 1000), energy);
    }

    public static ReactionChamberRecipeBuilder react(Fluid stack, int count, int energy) {
        return react(new FluidStack(stack, count), energy);
    }

    public ReactionChamberRecipeBuilder fluid(FluidStack fluid) {
        this.fluid = IngredientStack.of(fluid);
        return this;
    }

    public ReactionChamberRecipeBuilder fluid(Fluid fluid, int amount) {
        this.fluid = IngredientStack.of(new FluidStack(fluid, amount));
        return this;
    }

    public ReactionChamberRecipeBuilder input(ItemStack item) {
        this.inputs.add(IngredientStack.of(item));
        return this;
    }

    public ReactionChamberRecipeBuilder input(ItemLike item) {
        this.inputs.add(IngredientStack.of(new ItemStack(item)));
        return this;
    }

    public ReactionChamberRecipeBuilder input(ItemLike item, int count) {
        this.inputs.add(IngredientStack.of(new ItemStack(item, count)));
        return this;
    }

    public ReactionChamberRecipeBuilder input(TagKey<Item> tag) {
        this.inputs.add(IngredientStack.of(Ingredient.of(tag), 1));
        return this;
    }

    public ReactionChamberRecipeBuilder input(TagKey<Item> tag, int count) {
        this.inputs.add(IngredientStack.of(Ingredient.of(tag), count));
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new Result(id));
    }

    public void save(Consumer<FinishedRecipe> consumer, String id) {
        this.save(consumer, AdvancedAE.makeId(id));
    }

    class Result implements FinishedRecipe {
        private final ResourceLocation id;

        public Result(ResourceLocation id) {
            this.id = id;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {

            var result = ReactionChamberRecipeSerializer.GENERIC_STACK_CODEC
                    .encodeStart(JsonOps.INSTANCE, output)
                    .result()
                    .get()
                    .getAsJsonObject();
            json.add("output", result.get("output"));

            JsonArray ingredients = new JsonArray(inputs.size());
            for (var input : inputs) {
                ingredients.add(input.toJson());
            }
            json.add("input_items", ingredients);

            json.add("fluid", fluid.toJson());

            json.addProperty("energy", energy);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ReactionChamberRecipeSerializer.INSTANCE;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
