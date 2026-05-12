package net.pedroksl.advanced_ae.recipes;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.neoforged.neoforge.fluids.FluidStack;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.recipes.MechanicsRecipe;

public class ReactionChamberRecipe extends MechanicsRecipe<RecipeInput> {

    public static final MapCodec<ReactionChamberRecipe> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder.group(
                    GenericStack.CODEC.fieldOf("output").forGetter((ir) -> ir.output),
                    IngredientStack.Item.CODEC.listOf().fieldOf("input_items").forGetter((ir) -> ir.inputs),
                    IngredientStack.Fluid.CODEC.fieldOf("input_fluid").forGetter((ir) -> ir.fluid),
                    Codec.INT.fieldOf("input_energy").forGetter((ir) -> ir.energy))
            .apply(builder, ReactionChamberRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ReactionChamberRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    GenericStack.STREAM_CODEC,
                    (r) -> r.output,
                    IngredientStack.Item.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    (r) -> r.inputs,
                    IngredientStack.Fluid.STREAM_CODEC,
                    (r) -> r.fluid,
                    ByteBufCodecs.INT,
                    (r) -> r.energy,
                    ReactionChamberRecipe::new);

    public static final RecipeSerializer<ReactionChamberRecipe> SERIALIZER =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);

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
    public RecipeSerializer<ReactionChamberRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<ReactionChamberRecipe> getType() {
        return AAERecipeTypes.REACTION_CHAMBER;
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

    @Override
    public List<RecipeDisplay> display() {
        return List.of();
    }
}
