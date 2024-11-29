package net.pedroksl.advanced_ae.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.AdvancedAE;

import appeng.api.orientation.RelativeSide;

public class OutputDirectionButton extends Button {

    private final ResourceLocation buttonTexture = AdvancedAE.makeId("textures/guis/states.png");
    private ItemStack item;

    private RelativeSide side;
    private boolean enabled = false;

    private static final Rect2i DISABLED_BBOX = new Rect2i(176, 128, 256, 256);
    private static final Rect2i ENABLED_BBOX = new Rect2i(194, 128, 256, 256);

    public OutputDirectionButton(int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);
    }

    public void setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;
    }

    public void setSide(RelativeSide side) {
        this.side = side;
    }

    public void setItemStack(ItemStack item) {
        this.item = item;
    }

    public RelativeSide getSide() {
        return side;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, buttonTexture);
        if (enabled) {
            blit(pGuiGraphics, buttonTexture, ENABLED_BBOX);
        } else {
            blit(pGuiGraphics, buttonTexture, DISABLED_BBOX);
        }

        if (item != null) {
            pGuiGraphics.renderItem(item, this.getX() + 1, this.getY() + 1);
        }
    }

    private void blit(@NotNull GuiGraphics pGuiGraphics, ResourceLocation texture, Rect2i bbox) {
        pGuiGraphics.blit(
                texture,
                this.getX(),
                this.getY(),
                bbox.getX(),
                bbox.getY(),
                width,
                height,
                bbox.getWidth(),
                bbox.getHeight());
    }
}
