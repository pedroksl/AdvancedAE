package net.pedroksl.advanced_ae.client.gui.widgets;

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import net.pedroksl.advanced_ae.AdvancedAE;

public class AAESlider extends ForgeSlider {
    private static final ResourceLocation BUTTON = AdvancedAE.makeId("textures/guis/button.png");
    private static final ResourceLocation BUTTON_DISABLED = AdvancedAE.makeId("textures/guis/button_disabled.png");

    private final Consumer<Double> setter;

    public AAESlider(double minValue, double maxValue, double currentValue, float multiplier) {
        this(minValue, maxValue, currentValue, multiplier, value -> {});
    }

    public AAESlider(double minValue, double maxValue, double currentValue, float multiplier, Consumer<Double> setter) {
        super(
                0,
                0,
                100,
                20,
                Component.empty(),
                Component.empty(),
                minValue,
                maxValue,
                currentValue,
                multiplier,
                0,
                true);
        this.setter = setter;
    }

    public void setValues(double minValue, double maxValue, double currentValue, float multiplier) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = multiplier;
        setValue(currentValue);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        var minX = this.getX() + 4;
        var maxX = this.getX() + this.getWidth() - 4;
        var middleX = this.getX() + this.getWidth() / 2;
        var middleY = this.getY() + this.getHeight() / 2;
        var minY = this.getY() + this.getHeight() / 4;
        var maxY = this.getY() + 3 * this.getHeight() / 4;
        var backColor = 0XFF8B8B8B;
        var lineColor = 4276052 | Mth.ceil(this.alpha * 255.0F) << 24;

        guiGraphics.fill(minX - 6, minY - 2, maxX + 6, maxY + 1, 0xFF383838);
        guiGraphics.fill(minX - 5, minY - 1, maxX + 5, maxY, backColor);

        guiGraphics.hLine(minX, maxX, middleY, lineColor);
        guiGraphics.vLine(minX, minY, maxY, lineColor);
        guiGraphics.vLine(maxX, minY, maxY, lineColor);
        String minText = getValueText(this.minValue);
        guiGraphics.drawString(font, minText, minX - font.width(minText) / 2, this.getY() - 5, lineColor, false);
        String maxText = getValueText(this.maxValue);
        guiGraphics.drawString(font, maxText, maxX - font.width(maxText) / 2, this.getY() - 5, lineColor, false);

        String currentText = getValueText(this.getValue());
        guiGraphics.drawString(
                font, currentText, middleX - font.width(currentText) / 2, this.getY() - 5, lineColor, false);

        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        guiGraphics.blit(
                this.getHandleSprite(),
                this.getX() + (int) (this.value * (double) (this.width - 8)),
                this.getY() + 4,
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
    protected void applyValue() {
        setter.accept(this.getValue());
    }

    protected @NotNull ResourceLocation getHandleSprite() {
        return this.active ? BUTTON : BUTTON_DISABLED;
    }
}
