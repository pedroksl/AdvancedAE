package net.pedroksl.advanced_ae.xmod.emi.recipes;

import net.minecraft.ChatFormatting;
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
        super(CATEGORY, holder.id(), 168, 80);
        this.recipe = holder.value();
        for (var in : this.recipe.getInputs()) {
            if (!in.isEmpty()) {
                this.inputs.add(EMIPlugin.stackOf(in));
            }
        }
        if (this.recipe.getFluid() != null) {
            this.inputs.add(EMIPlugin.stackOf(this.recipe.getFluid()));
        }

        if (this.recipe.isItemOutput()) {
            this.outputs.add(EmiStack.of(this.recipe.getResultItem()));
        } else {
            this.outputs.add(EmiStack.of(
                    this.recipe.getResultFluid().getFluid(),
                    this.recipe.getResultFluid().getAmount()));
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        ResourceLocation background = AppEng.makeId("textures/guis/reaction_chamber.png");
        widgets.addTexture(background, 0, 0, 168, 80, 4, 13);
        widgets.addAnimatedTexture(background, 136, 29, 6, 18, 176, 0, 2000, false, true, false);

        var energyLabel = widgets.addText(
                        AAEText.ReactionChamberEnergy.text(this.recipe.getEnergy() / 1000),
                        width / 2 + 4,
                        70,
                        ChatFormatting.DARK_GRAY.getColor(),
                        false)
                .horizontalAlign(TextWidget.Alignment.CENTER);
        var energyLabelX = energyLabel.getBounds().x();
        var energyLabelY = 72 + energyLabel.getBounds().height() / 2;
        widgets.addTexture(EMIPlugin.TEXTURE, energyLabelX - 16, energyLabelY - 8, 10, 12, 0, 0, 10, 12, 32, 32);

        var index = 0;
        var inputs = this.recipe.getInputs();
        for (var in : inputs) {
            var x = 37 + index % 3 * 18;
            var y = 10 + index / 3 * 18;
            if (!in.isEmpty()) {
                widgets.addSlot(EMIPlugin.stackOf(in), x, y).drawBack(false);
            }
            index++;
        }
        if (this.recipe.getFluid() != null) {
            widgets.addTank(EMIPlugin.stackOf(this.recipe.getFluid()), 4, 7, 18, 60, 16000)
                    .drawBack(false);
        }
        if (recipe.isItemOutput()) {
            widgets.addSlot(EmiStack.of(recipe.getResultItem()), 113, 29)
                    .recipeContext(this)
                    .drawBack(false);
        } else {
            widgets.addTank(EmiStack.of(recipe.getResultFluid().getFluid()), 146, 7, 18, 60, 16000)
                    .recipeContext(this)
                    .drawBack(false);
        }
    }
}
