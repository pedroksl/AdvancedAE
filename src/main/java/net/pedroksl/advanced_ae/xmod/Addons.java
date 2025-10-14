package net.pedroksl.advanced_ae.xmod;

import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;

public enum Addons {
    EXTENDEDAE("Extended AE"),
    APPMEK("Applied Mekanistics"),
    APPFLUX("Applied Flux"),
    MEGACELLS("MEGACells"),
    MEKANISM("Mekanism"),
    IRIS("Iris"),
    CURIOS("Curios"),
    ADVANCED_AE("Advanced AE"),
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

    public RecipeOutput conditionalRecipe(RecipeOutput output) {
        return output.withConditions(new ModLoadedCondition(getModId()));
    }

    public RecipeOutput notConditionalRecipe(RecipeOutput output) {
        return output.withConditions(new NotCondition(new ModLoadedCondition(getModId())));
    }
}
