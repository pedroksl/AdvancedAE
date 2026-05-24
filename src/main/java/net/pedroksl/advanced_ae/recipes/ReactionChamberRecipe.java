package net.pedroksl.advanced_ae.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.display.FluidSlotDisplay;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import appeng.recipes.MechanicsRecipe;

public class ReactionChamberRecipe extends MechanicsRecipe<RecipeInput> {

    public static final MapCodec<ReactionChamberRecipe> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder.group(
                    ItemStackTemplate.CODEC
                            .optionalFieldOf("itemOutput")
                            .forGetter((ir) -> java.util.Optional.ofNullable(ir.itemOutput)),
                    FluidStackTemplate.CODEC
                            .optionalFieldOf("fluidOutput")
                            .forGetter((ir) -> java.util.Optional.ofNullable(ir.fluidOutput)),
                    IngredientStack.Item.CODEC.listOf().fieldOf("input_items").forGetter((ir) -> ir.inputs),
                    IngredientStack.Fluid.CODEC.fieldOf("input_fluid").forGetter((ir) -> ir.fluid),
                    Codec.INT.fieldOf("input_energy").forGetter((ir) -> ir.energy))
            .apply(builder, ReactionChamberRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ReactionChamberRecipe> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ReactionChamberRecipe decode(RegistryFriendlyByteBuf input) {
            boolean isItem = input.readBoolean();

            ItemStackTemplate itemOutput = null;
            FluidStackTemplate fluidOutput = null;

            if (isItem) {
                itemOutput = ItemStackTemplate.STREAM_CODEC.decode(input);
            } else {
                fluidOutput = FluidStackTemplate.STREAM_CODEC.decode(input);
            }

            var inputs = IngredientStack.Item.STREAM_CODEC
                    .apply(ByteBufCodecs.list())
                    .decode(input);
            var fluid = IngredientStack.Fluid.STREAM_CODEC.decode(input);
            var energy = ByteBufCodecs.INT.decode(input);
            return new ReactionChamberRecipe(itemOutput, fluidOutput, inputs, fluid, energy);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf output, ReactionChamberRecipe recipe) {
            output.writeBoolean(recipe.itemOutput != null);

            if (recipe.itemOutput != null) {
                ItemStackTemplate.STREAM_CODEC.encode(output, recipe.itemOutput);
            } else {
                FluidStackTemplate.STREAM_CODEC.encode(output, recipe.fluidOutput);
            }

            IngredientStack.Item.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(output, recipe.inputs);
            IngredientStack.Fluid.STREAM_CODEC.encode(output, recipe.fluid);
            ByteBufCodecs.INT.encode(output, recipe.energy);
        }
    };

    public static final RecipeSerializer<ReactionChamberRecipe> SERIALIZER =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);

    protected final List<IngredientStack.Item> inputs;
    protected final IngredientStack.Fluid fluid;
    public final ItemStackTemplate itemOutput;
    public final FluidStackTemplate fluidOutput;

    protected final int energy;

    public ReactionChamberRecipe(
            Optional<ItemStackTemplate> itemOutput,
            Optional<FluidStackTemplate> fluidOutput,
            List<IngredientStack.Item> inputs,
            IngredientStack.Fluid fluid,
            int energy) {
        this.inputs = inputs;
        this.itemOutput = itemOutput.orElse(null);
        this.fluidOutput = fluidOutput.orElse(null);
        this.fluid = fluid;
        this.energy = energy;
    }

    public ReactionChamberRecipe(
            ItemStackTemplate itemOutput,
            FluidStackTemplate fluidOutput,
            List<IngredientStack.Item> inputs,
            IngredientStack.Fluid fluid,
            int energy) {
        this.inputs = inputs;
        this.itemOutput = itemOutput;
        this.fluidOutput = fluidOutput;
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
        return this.itemOutput != null;
    }

    public ItemStack getResultItem() {
        if (this.itemOutput != null) {
            return itemOutput.create();
        }
        return ItemStack.EMPTY;
    }

    public FluidStack getResultFluid() {
        if (this.fluidOutput != null) {
            return this.fluidOutput.create();
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
        return List.of(new ReactionChamberRecipeDisplay(
                this.inputs.stream()
                        .map(IngredientStack::getIngredient)
                        .map(Ingredient::display)
                        .toList(),
                this.fluid.getIngredient().display(),
                this.itemOutput != null
                        ? new SlotDisplay.ItemStackSlotDisplay(this.itemOutput)
                        : SlotDisplay.Empty.INSTANCE,
                this.fluidOutput != null
                        ? new FluidSlotDisplay(this.fluidOutput.fluid())
                        : new FluidSlotDisplay(FluidStack.EMPTY.typeHolder()),
                new SlotDisplay.ItemSlotDisplay(AAEBlocks.REACTION_CHAMBER.asItem()),
                this.itemOutput != null));
    }
}
