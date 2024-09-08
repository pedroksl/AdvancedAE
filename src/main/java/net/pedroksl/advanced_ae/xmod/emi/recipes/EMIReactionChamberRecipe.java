package net.pedroksl.advanced_ae.xmod.emi.recipes;

import com.glodblock.github.extendedae.xmod.emi.recipes.EMIStackUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.recipes.ReactionChamberRecipe;
import net.pedroksl.advanced_ae.xmod.emi.EMIPlugin;

import appeng.core.AppEng;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;

public class EMIReactionChamberRecipe extends BasicEmiRecipe {

    public static final EmiRecipeCategory CATEGORY = new AAERecipeCategory(
            "reaction",
            EmiStack.of(AAEBlocks.REACTION_CHAMBER),
            Component.translatable(AAEText.EmiReactionChamber.getTranslationKey()));
    private final ReactionChamberRecipe recipe;

    public EMIReactionChamberRecipe(RecipeHolder<ReactionChamberRecipe> holder) {
        super(CATEGORY, holder.id(), 135, 58);
        this.recipe = holder.value();
        for (var in : this.recipe.getInputs()) {
            if (!in.isEmpty()) {
                this.inputs.add(EMIStackUtil.of(in));
            }
        }
        if (this.recipe.getFluid() != null) {
            this.inputs.add(EMIStackUtil.of(this.recipe.getFluid()));
        }
        this.outputs.add(EmiStack.of(this.recipe.getResultItem()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        ResourceLocation background = AppEng.makeId("textures/guis/reaction_chamber.png");
        widgets.addTexture(background, 0, 0, 135, 58, 20, 22);
        widgets.addAnimatedTexture(background, 122, 5, 6, 18, 176, 0, 2000, false, true, false);

        var energyLabel = widgets.addText(
                        AAEText.ReactionChamberEnergy.text(this.recipe.getEnergy() / 1000),
                        width / 2 + 4,
                        46,
                        AAEText.TOOLTIP_DEFAULT_COLOR,
                        false)
                .horizontalAlign(TextWidget.Alignment.CENTER);
        var energyLabelX = energyLabel.getBounds().x();
        var energyLabelY = 48 + energyLabel.getBounds().height() / 2;
        widgets.addTexture(EMIPlugin.TEXTURE, energyLabelX - 16, energyLabelY - 8, 10, 12, 0, 0, 10, 12, 32, 32);

        int x = 11;
        for (var in : this.recipe.getInputs()) {
            if (!in.isEmpty()) {
                widgets.addSlot(EMIStackUtil.of(in), x, 5).drawBack(false);
                x += 18;
            }
        }
        if (this.recipe.getFluid() != null) {
            widgets.addSlot(EMIStackUtil.of(this.recipe.getFluid()), 29, 24).drawBack(false);
        }
        widgets.addSlot(EmiStack.of(recipe.getResultItem()), 99, 5)
                .recipeContext(this)
                .drawBack(false);
    }
}
