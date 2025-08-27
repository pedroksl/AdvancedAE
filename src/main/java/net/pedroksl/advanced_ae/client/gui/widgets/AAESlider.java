package net.pedroksl.advanced_ae.client.gui.widgets;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.function.Consumer;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.helpers.AAEColor;

import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;

public class AAESlider implements ICompositeWidget {
    private static final ResourceLocation BUTTON = AdvancedAE.makeId("textures/guis/button.png");
    private static final ResourceLocation BUTTON_DISABLED = AdvancedAE.makeId("textures/guis/button_disabled.png");

    private static final int HANDLE_WIDTH = 8;
    private static final int HANDLE_HALF_WIDTH = 4;

    protected boolean isHovered;

    protected Point position = new Point(0, 0);
    protected int width = 100;
    protected int height = 20;
    protected float alpha = 1.0F;

    protected double value = 0;
    protected double minValue = 0;
    protected double maxValue = 1;
    protected double stepSize = 1;
    private final DecimalFormat format;

    private boolean isDragging = false;

    private boolean canChangeValue;

    private Consumer<Double> setter;

    public AAESlider() {
        this(value -> {});
    }

    public AAESlider(Consumer<Double> setter) {
        this(0, 1, 0, 1, setter);
    }

    public AAESlider(double minValue, double maxValue, double currentValue, double stepSize, Consumer<Double> setter) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = stepSize;
        this.value = this.snapToNearest((currentValue - minValue) / (maxValue - minValue));
        this.setter = setter;
        this.format = new DecimalFormat("0");
    }

    public double getValue() {
        return this.value * (this.maxValue - this.minValue) + this.minValue;
    }

    public long getValueLong() {
        return Math.round(this.getValue());
    }

    public int getValueInt() {
        return (int) this.getValueLong();
    }

    public void setValue(double value) {
        this.setFractionalValue((value - this.minValue) / (this.maxValue - this.minValue));
    }

    public String getValueString() {
        return this.format.format(this.getValue());
    }

    public void setValues(double minValue, double maxValue, double currentValue, float stepSize) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = stepSize;
        setValue(currentValue);
    }

    public void setCallback(Consumer<Double> setter) {
        this.setter = setter;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        var mouseX = bounds.getX() + mouse.getX();
        var mouseY = bounds.getY() + mouse.getY();

        var minX = bounds.getX() + this.position.getX();
        var minY = bounds.getY() + this.position.getY();

        this.isHovered = mouseX >= minX && mouseY >= minY && mouseX < minX + this.width && mouseY < minY + this.height;

        renderWidget(guiGraphics, new Point(minX, minY), mouse);
    }

    protected void renderWidget(GuiGraphics guiGraphics, Point topLeft, Point mouse) {
        var minX = topLeft.getX();
        var minY = topLeft.getY();

        var heightUsedForText = 10;
        var minSliderY = minY + heightUsedForText;
        var sliderHeight = this.height - heightUsedForText;

        var maxX = minX + this.width;
        var maxY = minY + this.height;

        var middleX = minX + this.width / 2;
        var middleY = minSliderY + sliderHeight / 2;

        var backColor = AAEColor.NEW_COLOR.argb((int) (this.alpha * 255f));
        var lineColor = AAEColor.DARK_GRAY.argb((int) (this.alpha * 255f));

        // Render background rectangles
        guiGraphics.fill(minX - 2, minSliderY - 2, maxX + 2, maxY + 2, Color.WHITE.getRGB());
        guiGraphics.fill(minX - 1, minSliderY - 1, maxX + 1, maxY + 1, backColor);

        // Render guide lines for the slider
        guiGraphics.hLine(minX, maxX - 1, middleY, lineColor);
        guiGraphics.vLine(minX, minSliderY, maxY, lineColor);
        guiGraphics.vLine(maxX - 1, minSliderY, maxY, lineColor);

        // Render min/max text values
        Font font = Minecraft.getInstance().font;
        String minText = getValueText(this.minValue);
        guiGraphics.drawString(font, minText, minX, minY, lineColor, false);
        String maxText = getValueText(this.maxValue);
        guiGraphics.drawString(font, maxText, maxX - font.width(maxText), minY, lineColor, false);

        // Render current value text
        String currentText = getValueText(this.getValue());
        guiGraphics.drawString(font, currentText, middleX - font.width(currentText) / 2, minY, lineColor, false);

        // Render the handle
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        guiGraphics.blit(
                this.getHandleSprite(),
                minX + (int) (this.value * (double) (this.width - HANDLE_WIDTH)),
                minSliderY - 1,
                0,
                0,
                8,
                12,
                8,
                12);
    }

    private String getValueText(double value) {
        if (this.stepSize < 1) return String.format("%.1f", value);
        else {
            return String.format("%.0f", value);
        }
    }

    @Override
    public boolean onMouseDown(Point mousePos, int button) {
        if (button != InputConstants.MOUSE_BUTTON_LEFT) {
            return false;
        }

        this.isDragging = true;
        this.playDownSound();
        setValueFromMouse(mousePos.getX());
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
            setValueFromMouse(mousePos.getX());
            return true;
        }
        return false;
    }

    private void setValueFromMouse(double mouseX) {
        this.setFractionalValue((mouseX - (double) (this.position.getX())) / (double) (this.width));
    }

    private void setFractionalValue(double fractionalValue) {
        double oldValue = this.value;
        this.value = this.snapToNearest(Math.min(Math.max(fractionalValue, 0f), 1f));
        if (!Mth.equal(oldValue, this.value)) {
            this.applyValue();
        }
    }

    private double snapToNearest(double value) {
        if (this.stepSize <= (double) 0.0F) {
            return Mth.clamp(value, 0.0F, 1.0F);
        } else {
            value = Mth.lerp(Mth.clamp(value, 0.0F, 1.0F), this.minValue, this.maxValue);
            value = this.stepSize * (double) Math.round(value / this.stepSize);
            if (this.minValue > this.maxValue) {
                value = Mth.clamp(value, this.maxValue, this.minValue);
            } else {
                value = Mth.clamp(value, this.minValue, this.maxValue);
            }

            return Mth.map(value, this.minValue, this.maxValue, 0.0F, 1.0F);
        }
    }

    protected void applyValue() {
        setter.accept(this.getValue());
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

    protected @NotNull ResourceLocation getHandleSprite() {
        return this.isHovered ? BUTTON : BUTTON_DISABLED;
    }
}
