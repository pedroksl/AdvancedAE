package net.pedroksl.advanced_ae.xmod.emi.recipes;

import com.glodblock.github.extendedae.ExtendedAE;

import net.minecraft.network.chat.Component;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;

public class AAERecipeCategory extends EmiRecipeCategory {
    private final Component name;

    public AAERecipeCategory(String id, EmiRenderable icon, Component name) {
        super(ExtendedAE.id(id), icon);
        this.name = name;
    }

    @Override
    public Component getName() {
        return name;
    }
}
