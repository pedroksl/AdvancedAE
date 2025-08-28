package net.pedroksl.advanced_ae.client.gui.widgets;

import java.util.function.BiConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.pedroksl.advanced_ae.common.helpers.AAEColor;

import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.style.ScreenStyle;

public class AAEColorPicker extends AAECompositeWidgetContainer {

    private enum UpdateTrigger {
        SLIDERS,
        HEX_CODE
    }

    private static final int HUE_SLIDER_HEIGHT = 10;
    private static final int PREVIEW_ROW_HEIGHT = 10;
    private static final int HEX_ROW_HEIGHT = 10;
    private static final int WIDGET_SPACING = 3;
    private static final int PADDING = 2;
    private static final int HEX_LABEL_WIDTH = 23;

    private float hue;
    private float saturation;
    private float value;
    private int color;
    private UpdateTrigger updateTrigger = null;

    private final AAEHueSlider hueSlider;
    private final AAESaturationValuePicker saturationValuePicker;
    private final AAEHexColorInput hexInput;

    public AAEColorPicker(BiConsumer<String, ICompositeWidget> addWidget, int color, ScreenStyle style, String id) {
        super(addWidget, style, id);

        AAEColor.HSV hsv = AAEColor.ofRgb(color).hsv();
        this.color = color;
        this.hue = hsv.hue();
        this.saturation = hsv.saturation();
        this.value = hsv.value();

        Point hueSliderPosition = new Point(0, 0);
        this.hueSlider = new AAEHueSlider(hsv.hue(), hue -> this.setHue(hue.floatValue()));
        this.hueSlider.setSize(this.width, HUE_SLIDER_HEIGHT);
        this.add(hueSliderPosition, hueSlider);

        Point saturationValuePickerPosition = new Point(0, HUE_SLIDER_HEIGHT + WIDGET_SPACING);
        this.saturationValuePicker =
                new AAESaturationValuePicker(hsv.hue(), hsv.saturation(), hsv.value(), this::setSaturationAndValue);
        this.saturationValuePicker.setSize(
                this.width,
                this.height - HUE_SLIDER_HEIGHT - PREVIEW_ROW_HEIGHT - HEX_ROW_HEIGHT - 3 * WIDGET_SPACING - 1);
        this.add(saturationValuePickerPosition, saturationValuePicker);

        Point hexInputPosition = new Point(HEX_LABEL_WIDTH - PADDING, this.height - HEX_ROW_HEIGHT - PADDING);
        this.hexInput = new AAEHexColorInput(
                style,
                Minecraft.getInstance().font,
                0,
                0,
                this.width - HEX_LABEL_WIDTH + PADDING + 1,
                HEX_ROW_HEIGHT,
                this::setColor);
        this.add(hexInputPosition, hexInput);
        this.hexInput.setColor(color);
    }

    public AAEColor color() {
        return AAEColor.ofHsv(this.hue, this.saturation, this.value);
    }

    public void setHue(float hue) {
        if (this.updateTrigger == null) this.updateTrigger = UpdateTrigger.SLIDERS;

        this.hue = hue / 360f;

        if (this.updateTrigger == UpdateTrigger.SLIDERS) {
            this.color = AAEColor.ofHsv(this.hue, this.saturation, this.value).argb();
            this.hexInput.setColor(this.color);
            this.updateTrigger = null;
        } else {
            this.hueSlider.setValue(this.hue * 360f);
        }
        this.saturationValuePicker.setHue(this.hue);
    }

    public void setSaturationAndValue(float saturation, float value) {
        if (this.updateTrigger == null) this.updateTrigger = UpdateTrigger.SLIDERS;

        this.saturation = saturation;
        this.value = value;

        if (this.updateTrigger == UpdateTrigger.SLIDERS) {
            this.color = AAEColor.ofHsv(this.hue, this.saturation, this.value).argb();
            this.hexInput.setColor(this.color);
            this.updateTrigger = null;
        } else {
            this.saturationValuePicker.setValues(this.saturation, this.value);
        }
    }

    public void setColor(int value) {
        this.updateTrigger = UpdateTrigger.HEX_CODE;
        AAEColor.HSV hsv = AAEColor.ofRgb(value).hsv();
        setHue(hsv.hue() * 360f);
        setSaturationAndValue(hsv.saturation(), hsv.value());
        this.color = AAEColor.ofHsv(this.hue, this.saturation, this.value).argb();
        this.updateTrigger = null;
    }

    public void setColorAndUpdate(int value) {
        if (this.color == value) return;

        setColor(value);
        this.hexInput.setColor(this.color);
    }

    @Override
    public void updateBeforeRender() {
        super.updateBeforeRender();
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        super.drawBackgroundLayer(guiGraphics, bounds, mouse);

        Point pos = this.style.resolve(bounds);
        var minX = pos.getX();
        var minY = pos.getY() + this.height - PREVIEW_ROW_HEIGHT - HEX_ROW_HEIGHT - WIDGET_SPACING - 1;

        // guiGraphics.fill(0, 0, 1, 1, -1);
        guiGraphics.fill(
                minX - 1,
                minY - 1,
                minX + this.width + 1,
                minY + PREVIEW_ROW_HEIGHT + 1,
                AAEColor.DARK_GRAY_BLUE.argb());

        guiGraphics.fill(minX, minY, minX + this.width, minY + PREVIEW_ROW_HEIGHT, this.color);

        guiGraphics.drawString(
                Minecraft.getInstance().font,
                "Hex:",
                pos.getX(),
                pos.getY() + this.height - HEX_ROW_HEIGHT,
                AAEColor.DARK_GRAY_BLUE.rgb(),
                false);
    }
}
