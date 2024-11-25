package net.pedroksl.advanced_ae.xmod;

import java.util.function.Consumer;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public enum Addons {
    EXTENDEDAE("Extended AE"),
    APPFLUX("Applied Flux"),
    MEGACELLS("MEGACells"),
    MEKANISM("Mekanism"),
    IRIS("Iris"),
    CURIOS("Curios"),
    APOTHIC_ENCHANTING("Apothic Enchanting");

    private final String modName;

    Addons(String modName) {
        this.modName = modName;
    }

    public String getModId() {
        return name().toLowerCase();
    }

    public String getModName() {
        return this.modName;
    }

    public boolean isLoaded() {
        return ModList.get() != null
                ? ModList.get().isLoaded(getModId())
                : LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(getModId()::equals);
    }

    public void conditionalRecipe(Consumer<FinishedRecipe> output, RecipeBuilder recipe, ResourceLocation id) {
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(getModId()))
                .addRecipe(recipe::save)
                .build(output, id);
    }

    public void notConditionalRecipe(Consumer<FinishedRecipe> output, RecipeBuilder recipe, ResourceLocation id) {
        ConditionalRecipe.builder()
                .addCondition(new NotCondition(new ModLoadedCondition(getModId())))
                .addRecipe(recipe::save)
                .build(output, id);
    }
}
