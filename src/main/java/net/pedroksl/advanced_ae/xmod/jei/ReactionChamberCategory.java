package net.pedroksl.advanced_ae.xmod.jei;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.recipes.ReactionChamberRecipe;

import appeng.core.AppEng;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

public class ReactionChamberCategory implements IRecipeCategory<ReactionChamberRecipe> {

    public static final RecipeType<ReactionChamberRecipe> RECIPE_TYPE =
            RecipeType.create(AdvancedAE.MOD_ID, "reaction_chamber", ReactionChamberRecipe.class);

    private static final ResourceLocation BACKGROUND = AppEng.makeId("textures/guis/reaction_chamber.png");

    private final IDrawable icon;

    private final IDrawable background;

    private final IDrawableAnimated progress;

    private final IDrawableStatic bolt;

    public ReactionChamberCategory(IJeiHelpers helpers) {
        IGuiHelper guiHelper = helpers.getGuiHelper();
        background = guiHelper.createDrawable(BACKGROUND, 5, 15, 168, 75);
        icon = guiHelper.createDrawableItemStack(AAEBlocks.REACTION_CHAMBER.stack());

        IDrawableStatic progressDrawable = guiHelper.createDrawable(BACKGROUND, 176, 0, 6, 18);
        this.progress =
                guiHelper.createAnimatedDrawable(progressDrawable, 40, IDrawableAnimated.StartDirection.BOTTOM, false);

        bolt = guiHelper
                .drawableBuilder(JEIPlugin.TEXTURE, 0, 0, 10, 12)
                .setTextureSize(32, 32)
                .build();
    }

    @Override
    public RecipeType<ReactionChamberRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public Component getTitle() {
        return AAEText.EmiReactionChamber.text();
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ReactionChamberRecipe recipe, IFocusGroup focuses) {
        var index = 0;
        var inputs = recipe.getInputs();
        for (var in : inputs) {
            var x = 37 + index % 3 * 18;
            var y = 9 + index / 3 * 18;
            if (!in.isEmpty()) {
                builder.addInputSlot(x, y).addIngredients(JEIPlugin.stackOf(in));
            }
            index++;
        }

        if (recipe.getFluid() != null) {
            var fluid = recipe.getFluid();
            var slot = builder.addInputSlot(4, 6).setFluidRenderer(16000, false, 16, 58);
            slot.addIngredients(NeoForgeTypes.FLUID_STACK, JEIPlugin.stackOf(fluid));
        }

        if (recipe.isItemOutput()) {
            builder.addOutputSlot(113, 28).addItemStack(recipe.getResultItem());
        } else {
            var slot = builder.addOutputSlot(146, 6).setFluidRenderer(16000, false, 16, 58);
            slot.addFluidStack(
                    recipe.getResultFluid().getFluid(), recipe.getResultFluid().getAmount());
        }
    }

    @Override
    public void draw(
            ReactionChamberRecipe recipe,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphics guiGraphics,
            double mouseX,
            double mouseY) {
        this.background.draw(guiGraphics);
        this.progress.draw(guiGraphics, 135, 27);

        var font = Minecraft.getInstance().font;
        var text = AAEText.ReactionChamberEnergy.text(recipe.getEnergy() / 1000);
        FormattedCharSequence formattedcharsequence = text.getVisualOrderText();
        var textX = getWidth() / 2 + 4 - font.width(formattedcharsequence) / 2;
        guiGraphics.drawString(font, text, textX, 66, ChatFormatting.DARK_GRAY.getColor(), false);

        bolt.draw(guiGraphics, textX - 16, 64);
    }
}
