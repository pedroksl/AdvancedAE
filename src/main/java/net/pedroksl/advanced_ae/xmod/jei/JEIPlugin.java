package net.pedroksl.advanced_ae.xmod.jei;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.recipes.ReactionChamberRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static final ResourceLocation TEXTURE = AdvancedAE.makeId("textures/guis/emi.png");

    private static final ResourceLocation ID = new ResourceLocation(AdvancedAE.MOD_ID, "core");

    public JEIPlugin() {}

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        var jeiHelpers = registry.getJeiHelpers();
        registry.addRecipeCategories(new ReactionChamberCategory(jeiHelpers));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        registration.addRecipes(
                ReactionChamberCategory.RECIPE_TYPE,
                List.copyOf(recipeManager.byType(ReactionChamberRecipe.TYPE).values()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        var chamber = AAEBlocks.REACTION_CHAMBER.stack();
        registration.addRecipeCatalyst(chamber, ReactionChamberCategory.RECIPE_TYPE);
    }
}
