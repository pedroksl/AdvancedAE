package net.pedroksl.advanced_ae.xmod.emi.recipes;

import net.minecraft.network.chat.Component;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import net.pedroksl.advanced_ae.AdvancedAE;

public class AAERecipeCategory extends EmiRecipeCategory {
    private final Component name;

    public AAERecipeCategory(String id, EmiRenderable icon, Component name) {
        super(AdvancedAE.makeId(id), icon);
        this.name = name;
    }

    @Override
    public Component getName() {
        return name;
    }
}
