package net.pedroksl.advanced_ae.client.gui.widgets;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import appeng.api.stacks.AEKey;
import appeng.client.gui.style.Blitter;

public class DirectionInputButton extends Button {

    private final Pair<Identifier, Identifier> textures;
    private AEKey key;
    private int index;
    private boolean highlighted;

    public DirectionInputButton(
            int x, int y, int width, int height, Pair<Identifier, Identifier> textures, OnPress onPress) {
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
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (highlighted) {
            Blitter.texture(textures.getSecond(), 16, 16)
                    .src(0, 0, width, height)
                    .dest(this.getX(), this.getY())
                    .blit(guiGraphics);
        } else {
            Blitter.texture(textures.getFirst(), 16, 16)
                    .src(0, 0, width, height)
                    .dest(this.getX(), this.getY())
                    .blit(guiGraphics);
        }
    }
}
