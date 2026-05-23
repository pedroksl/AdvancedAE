package net.pedroksl.advanced_ae.recipes;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public record ReactionChamberRecipeDisplay(
        List<SlotDisplay> inputs,
        SlotDisplay fluidInput,
        SlotDisplay itemOutput,
        SlotDisplay fluidOutput,
        SlotDisplay craftingStation,
        boolean isItemRecipe)
        implements RecipeDisplay {

    public static final MapCodec<ReactionChamberRecipeDisplay> MAP_CODEC =
            RecordCodecBuilder.mapCodec(builder -> builder.group(
                            SlotDisplay.CODEC
                                    .listOf()
                                    .fieldOf("inputs")
                                    .forGetter(ReactionChamberRecipeDisplay::inputs),
                            SlotDisplay.CODEC.fieldOf("fluidInput").forGetter(ReactionChamberRecipeDisplay::fluidInput),
                            SlotDisplay.CODEC.fieldOf("itemOutput").forGetter(ReactionChamberRecipeDisplay::itemOutput),
                            SlotDisplay.CODEC
                                    .fieldOf("fluidOutput")
                                    .forGetter(ReactionChamberRecipeDisplay::fluidOutput),
                            SlotDisplay.CODEC
                                    .fieldOf("crafting_station")
                                    .forGetter(ReactionChamberRecipeDisplay::craftingStation),
                            Codec.BOOL.fieldOf("isItemRecipe").forGetter(ReactionChamberRecipeDisplay::isItemRecipe))
                    .apply(builder, ReactionChamberRecipeDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReactionChamberRecipeDisplay> STREAM_CODEC =
            StreamCodec.composite(
                    SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    ReactionChamberRecipeDisplay::inputs,
                    SlotDisplay.STREAM_CODEC,
                    ReactionChamberRecipeDisplay::fluidInput,
                    SlotDisplay.STREAM_CODEC,
                    ReactionChamberRecipeDisplay::itemOutput,
                    SlotDisplay.STREAM_CODEC,
                    ReactionChamberRecipeDisplay::fluidOutput,
                    SlotDisplay.STREAM_CODEC,
                    ReactionChamberRecipeDisplay::craftingStation,
                    ByteBufCodecs.BOOL,
                    ReactionChamberRecipeDisplay::isItemRecipe,
                    ReactionChamberRecipeDisplay::new);

    public static final RecipeDisplay.Type<ReactionChamberRecipeDisplay> TYPE =
            new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public SlotDisplay result() {
        return isItemRecipe ? itemOutput : fluidOutput;
    }

    @Override
    public Type<ReactionChamberRecipeDisplay> type() {
        return TYPE;
    }
}
