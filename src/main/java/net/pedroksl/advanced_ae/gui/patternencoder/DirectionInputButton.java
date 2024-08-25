package net.pedroksl.advanced_ae.gui.patternencoder;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import appeng.api.stacks.AEKey;

public class DirectionInputButton extends Button {

    private final Pair<ResourceLocation, ResourceLocation> textures;
    private AEKey key;
    private int index;
    private boolean highlighted;

    public DirectionInputButton(
            int x, int y, int width, int height, Pair<ResourceLocation, ResourceLocation> textures, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);

        this.textures = textures;
    }

    public void setHighlighted(boolean isHighlighted) {
        this.highlighted = isHighlighted;
    }

    public void setKey(AEKey key) {
        this.key = key;
    }

    public AEKey getKey() {
        return this.key;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Direction getDirection() {
        return switch (index) {
            case 1 -> Direction.NORTH;
            case 2 -> Direction.EAST;
            case 3 -> Direction.SOUTH;
            case 4 -> Direction.WEST;
            case 5 -> Direction.UP;
            case 6 -> Direction.DOWN;
            default -> null;
        };
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        if (highlighted) {
            RenderSystem.setShaderTexture(0, textures.getSecond());
            pGuiGraphics.blit(textures.getSecond(), this.getX(), this.getY(), 0, 0, width, height, 16, 16);
        } else {
            RenderSystem.setShaderTexture(0, textures.getFirst());
            pGuiGraphics.blit(textures.getFirst(), this.getX(), this.getY(), 0, 0, width, height, 16, 16);
        }
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        super.onClick(p_onClick_1_, p_onClick_3_);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        return super.mouseClicked(x, y, button);
    }
}
