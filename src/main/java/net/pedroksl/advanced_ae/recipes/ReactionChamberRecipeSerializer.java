package net.pedroksl.advanced_ae.recipes;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

import appeng.api.stacks.GenericStack;

public class ReactionChamberRecipeSerializer implements RecipeSerializer<ReactionChamberRecipe> {

    public static final ReactionChamberRecipeSerializer INSTANCE = new ReactionChamberRecipeSerializer();

    private ReactionChamberRecipeSerializer() {}

    public static final MapCodec<ReactionChamberRecipe> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder.group(
                    GenericStack.CODEC.fieldOf("output").forGetter((ir) -> ir.output),
                    IngredientStack.ITEM_CODEC.listOf().fieldOf("input_items").forGetter((ir) -> ir.inputs),
                    IngredientStack.FLUID_CODEC.fieldOf("input_fluid").forGetter((ir) -> ir.fluid),
                    Codec.INT.fieldOf("input_energy").forGetter((ir) -> ir.energy))
            .apply(builder, ReactionChamberRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ReactionChamberRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    GenericStack.STREAM_CODEC,
                    (r) -> r.output,
                    IngredientStack.ITEM_STREAM_CODEC.apply(ByteBufCodecs.list()),
                    (r) -> r.inputs,
                    IngredientStack.FLUID_STREAM_CODEC,
                    (r) -> r.fluid,
                    StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt),
                    (r) -> r.energy,
                    ReactionChamberRecipe::new);

    @Override
    public @NotNull MapCodec<ReactionChamberRecipe> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, ReactionChamberRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
