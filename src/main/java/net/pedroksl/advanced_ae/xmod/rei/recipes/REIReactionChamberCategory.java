package net.pedroksl.advanced_ae.xmod.rei.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.xmod.rei.REIPlugin;

import appeng.core.AppEng;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class REIReactionChamberCategory implements DisplayCategory<REIReactionChamberDisplay> {

    private static final int PADDING = 5;

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(AAEBlocks.REACTION_CHAMBER);
    }

    @Override
    public Component getTitle() {
        return AAEBlocks.REACTION_CHAMBER.block().getName();
    }

    @Override
    public CategoryIdentifier<REIReactionChamberDisplay> getCategoryIdentifier() {
        return REIReactionChamberDisplay.ID;
    }

    @Override
    public List<Widget> setupDisplay(REIReactionChamberDisplay recipeDisplay, Rectangle bounds) {
        ResourceLocation location = AppEng.makeId("textures/guis/reaction_chamber.png");

        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(location, bounds.x + PADDING, bounds.y + PADDING, 4, 13, 168, 80));

        var energyLabel = Widgets.createLabel(
                new Point(bounds.x + bounds.width / 2 + 4 + PADDING, bounds.y + 70 + PADDING),
                AAEText.ReactionChamberEnergy.text(recipeDisplay.getEnergy() / 1000));
        widgets.add(energyLabel);
        var energyLabelX = energyLabel.getBounds().getX();
        var energyLabelY = 72 + energyLabel.getBounds().getHeight() / 2;
        widgets.add(Widgets.createTexturedWidget(
                REIPlugin.TEXTURE, energyLabelX - 16, energyLabelY - 8, 10, 12, 0, 0, 10, 12, 32, 32));

        int index = 0;
        for (var in : recipeDisplay.getInputItems()) {
            var x = 37 + index % 3 * 18;
            var y = 10 + index / 3 * 18;
            if (!in.isEmpty()) {
                widgets.add(Widgets.createSlot(new Point(bounds.x + x + PADDING, bounds.y + y + PADDING))
                        .disableBackground()
                        .markInput()
                        .entries(in));
            }
            index++;
        }

        if (!recipeDisplay.getInputFluid().isEmpty()) {
            widgets.add(Widgets.createSlot(new Rectangle(bounds.x + 4 + PADDING, bounds.y + 7 + PADDING, 18, 60))
                    .disableBackground()
                    .markInput()
                    .entries(recipeDisplay.getInputFluid()));
        }

        var output = recipeDisplay.getOutputEntries();
        if (!output.getFirst().isEmpty()) {
            widgets.add(Widgets.createSlot(new Point(bounds.x + 99 + PADDING, bounds.y + 5 + PADDING))
                    .disableBackground()
                    .markOutput()
                    .entries(output.getFirst()));
        }
        if (!output.get(1).isEmpty()) {
            widgets.add(Widgets.createSlot(new Rectangle(bounds.x + 146 + PADDING, bounds.y + 7 + PADDING, 18, 60))
                    .disableBackground()
                    .markOutput()
                    .entries(output.get(1)));
        }
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 80 + 2 * PADDING;
    }

    @Override
    public int getDisplayWidth(REIReactionChamberDisplay display) {
        return 168 + 2 * PADDING;
    }
}
