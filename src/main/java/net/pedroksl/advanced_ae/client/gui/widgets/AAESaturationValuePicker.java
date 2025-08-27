package net.pedroksl.advanced_ae.client.gui.widgets;

import java.awt.*;
import java.util.function.BiConsumer;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.pedroksl.advanced_ae.common.helpers.AAEColor;

import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;

public class AAESaturationValuePicker implements ICompositeWidget {

    private Point position;
    private int width;
    private int height;

    private float hue;
    private float saturation;
    private float value;

    private final BiConsumer<Float, Float> saturationAndValueSetter;

    private boolean isDragging = false;

    public AAESaturationValuePicker() {
        this(0f, 0f, 0f, (s, v) -> {});
    }

    public AAESaturationValuePicker(
            float hue, float saturation, float value, BiConsumer<Float, Float> saturationAndValueSetter) {
        this.hue = hue;
        this.saturation = saturation;
        this.value = value;

        this.saturationAndValueSetter = saturationAndValueSetter;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        var minX = bounds.getX() + this.position.getX();
        var minY = bounds.getY() + this.position.getY();
        var w = this.width;
        var h = this.height;

        guiGraphics.fill(minX - 1, minY - 1, minX + w + 1, minY + h + 1, AAEColor.DARK_GRAY.argb());

        renderGradient(
                guiGraphics,
                minX,
                minY,
                w,
                h,
                AAEColor.ofHsv(hue, 0f, 1f).argb(),
                AAEColor.ofHsv(hue, 1f, 1f).argb(),
                AAEColor.ofHsv(hue, 0f, 0f).argb(),
                AAEColor.ofHsv(hue, 1f, 0f).argb());

        int hsvX = minX + (int) (this.saturation * (this.width - 1));
        int hsvY = minY + (int) ((1f - this.value) * (this.height - 1));
        int color = AAEColor.WHITE.argb();
        guiGraphics.hLine(hsvX - 1, hsvX + 1, hsvY - 1, color);
        guiGraphics.hLine(hsvX - 1, hsvX + 1, hsvY - 1, color);
        guiGraphics.hLine(hsvX - 1, hsvX + 1, hsvY + 1, color);
        guiGraphics.vLine(hsvX - 1, hsvY - 1, hsvY + 1, color);
        guiGraphics.vLine(hsvX + 1, hsvY - 1, hsvY + 1, color);
    }

    private void renderGradient(
            GuiGraphics guiGraphics,
            int x,
            int y,
            int width,
            int height,
            int tlColor,
            int trColor,
            int blColor,
            int brColor) {
        RenderType type = RenderType.gui();

        var buffer = guiGraphics.bufferSource().getBuffer(type);
        var matrix = guiGraphics.pose().last().pose();

        buffer.vertex(matrix, x + width, y, 0).color(trColor);
        buffer.vertex(matrix, x, y, 0).color(tlColor);
        buffer.vertex(matrix, x, y + height, 0).color(blColor);
        buffer.vertex(matrix, x + width, y + height, 0).color(brColor);
    }

    @Override
    public boolean onMouseDown(Point mousePos, int button) {
        if (button != InputConstants.MOUSE_BUTTON_LEFT) {
            return false;
        }

        this.isDragging = true;
        this.playDownSound();
        setValueFromMouse(mousePos.getX(), mousePos.getY());
        return true;
    }

    private void playDownSound() {
        var handler = Minecraft.getInstance().getSoundManager();
        handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public boolean onMouseUp(Point mousePos, int button) {
        this.isDragging = false;
        return false;
    }

    @Override
    public boolean wantsAllMouseUpEvents() {
        return true;
    }

    @Override
    public boolean onMouseDrag(Point mousePos, int button) {
        if (this.isDragging) {
            setValueFromMouse(mousePos.getX(), mousePos.getY());
            return true;
        }
        return false;
    }

    private void setValueFromMouse(double mouseX, double mouseY) {
        double x = ((mouseX - (double) (this.position.getX())) / (double) (this.width));
        double y = ((mouseY - (double) (this.position.getY())) / (double) (this.height));

        double newSaturation = Math.min(Math.max(x, 0f), 1f);
        double newValue = Math.min(Math.max((1f - y), 0f), 1f);

        this.applyValue((float) newSaturation, (float) newValue);
    }

    protected void applyValue(float newSaturation, float newValue) {
        if (!Mth.equal(this.saturation, newSaturation) || !Mth.equal(this.value, newValue)) {
            this.saturation = newSaturation;
            this.value = newValue;
            this.saturationAndValueSetter.accept(this.saturation, this.value);
        }
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    public void setValues(float saturation, float value) {
        this.saturation = saturation;
        this.value = value;
    }

    @Override
    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Rect2i getBounds() {
        return new Rect2i(this.position.getX(), this.position.getY(), this.width, this.height);
    }
}
