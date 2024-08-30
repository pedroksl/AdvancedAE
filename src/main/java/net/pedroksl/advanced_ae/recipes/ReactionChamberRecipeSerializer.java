package net.pedroksl.advanced_ae.recipes;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import com.glodblock.github.glodium.util.GlodCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ReactionChamberRecipeSerializer implements RecipeSerializer<ReactionChamberRecipe> {

    public static final ReactionChamberRecipeSerializer INSTANCE = new ReactionChamberRecipeSerializer();

    private ReactionChamberRecipeSerializer() {}

    public static final MapCodec<ReactionChamberRecipe> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder.group(
                    ItemStack.CODEC.fieldOf("output").forGetter((ir) -> ir.output),
                    IngredientStack.ITEM_CODEC.listOf().fieldOf("input_items").forGetter((ir) -> ir.inputs),
                    IngredientStack.FLUID_CODEC.optionalFieldOf("input_fluid").forGetter((ir) -> ir.fluid))
            .apply(builder, ReactionChamberRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ReactionChamberRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.STREAM_CODEC,
                    (r) -> r.output,
                    GlodCodecs.list(IngredientStack.ITEM_STREAM_CODEC),
                    (r) -> r.inputs,
                    GlodCodecs.optional(IngredientStack.FLUID_STREAM_CODEC),
                    (r) -> r.fluid,
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
