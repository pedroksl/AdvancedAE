package net.pedroksl.advanced_ae.client.gui.widgets;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.pedroksl.advanced_ae.common.definitions.AAEText;

import appeng.api.stacks.GenericStack;
import appeng.core.localization.Tooltips;

public class FluidTankSlot extends AbstractWidget {

    private TextureAtlasSprite fluidTexture;
    private FluidStack content = FluidStack.EMPTY;
    private final int maxLevel;
    private boolean disableRender = false;

    public FluidTankSlot(int x, int y, int width, int height, int maxLevel, Component message) {
        super(x, y, width, height, message);
        this.maxLevel = maxLevel;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (content == null || fluidTexture == null || this.disableRender) return;

        int color = IClientFluidTypeExtensions.of(this.content.getFluid()).getTintColor();
        guiGraphics.setColor(
                (float) FastColor.ARGB32.red(color) / 255.0F,
                (float) FastColor.ARGB32.green(color) / 255.0F,
                (float) FastColor.ARGB32.blue(color) / 255.0F,
                (float) FastColor.ARGB32.alpha(color) / 255.0F);

        float levels = content.getAmount() / 1000f / maxLevel;
        var usedY = (int) Math.ceil(levels * this.height);

        float tiles = (float) usedY / this.width;

        var currentY = this.getY() + this.height;
        for (var x = 0; x < tiles; x++) {
            if (tiles - x > 1) {
                var size = this.width;
                guiGraphics.blit(this.getX(), currentY - size, 0, size, size, this.fluidTexture);
                currentY -= size;
            } else {
                var size = (int) Math.ceil((tiles - x) * this.width);
                guiGraphics.blit(this.getX(), currentY - size, 0, this.width, size, this.fluidTexture);
            }
        }
        guiGraphics.setColor(1, 1, 1, 1);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {}

    public void setFluidStack(FluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            this.disableRender = true;
            setTooltip(Tooltip.create(Tooltips.of(
                    AAEText.TankEmpty.text(),
                    Component.literal("\n"),
                    AAEText.TankAmount.text(0, 16000).withStyle(Tooltips.NORMAL_TOOLTIP_TEXT))));
            return;
        }

        this.disableRender = false;
        boolean updateTexture = this.content.isEmpty() || fluidStack.getFluid() != this.content.getFluid();
        this.content = fluidStack;
        var genericStack = GenericStack.fromFluidStack(content);
        if (genericStack != null) {
            setTooltip(Tooltip.create(Tooltips.of(
                    genericStack.what().getDisplayName(), Component.literal(": "), Tooltips.ofAmount(genericStack))));
        }

        if (updateTexture && !this.content.isEmpty()) {
            IClientFluidTypeExtensions properties = IClientFluidTypeExtensions.of(this.content.getFluid());
            ResourceLocation texture = properties.getStillTexture(this.content);
            this.fluidTexture = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(texture);
        }
    }
}
