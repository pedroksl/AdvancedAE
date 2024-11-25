package net.pedroksl.advanced_ae.client.widgets;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;
import java.util.*;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import com.google.common.primitives.Longs;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.client.gui.MathExpressionParser;
import appeng.client.gui.NumberEntryType;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ConfirmableTextField;
import appeng.core.localization.GuiText;

public class NumberTextField extends ConfirmableTextField {

    private static final int PADDING = 10;

    private static final int TEXT_COLOR = 0xFF_FFFF;
    private static final int ERROR_COLOR = 0xFF_0000;

    private final DecimalFormat decimalFormat;
    private NumberEntryType type = NumberEntryType.UNITLESS;
    private final long minValue = 0;
    private final long maxValue = Long.MAX_VALUE;
    private boolean isFluid;

    private long lastLongValue = 0;
    private boolean isOutput = false;

    public NumberTextField(ScreenStyle style, int x, int y, int width, int height, Consumer<Long> onConfirm) {
        super(style, Minecraft.getInstance().font, x, y, width, height);

        this.decimalFormat = new DecimalFormat("#.######", new DecimalFormatSymbols());
        this.decimalFormat.setParseBigDecimal(true);
        this.decimalFormat.setNegativePrefix("-");

        setBordered(false);
        setMaxLength(25);
        setTextColor(16777215);
        setSelectionColor(-16777088);
        setVisible(true);
        setResponder(text -> this.validate());
        setOnConfirm(() -> {
            if (getLongValue().isPresent()) {
                this.lastLongValue = getLongValue().getAsLong();
                onConfirm.accept(getLongValue().getAsLong());
            }
        });
        this.validate();
    }

    public boolean isChanged() {
        if (getLongValue().isPresent()) {
            return this.lastLongValue != getLongValue().getAsLong();
        }
        return true;
    }

    public void setAsOutput() {
        this.isOutput = true;
    }

    public OptionalInt getIntValue() {
        var value = getLongValue();
        if (value.isPresent()) {
            var longValue = value.getAsLong();
            if (longValue > Integer.MAX_VALUE) {
                return OptionalInt.empty();
            }
            return OptionalInt.of((int) longValue);
        }
        return OptionalInt.empty();
    }

    public OptionalLong getLongValue() {
        var internalValue = getValueInternal();
        if (internalValue.isEmpty()) {
            return OptionalLong.empty();
        }

        // Reject decimal values if the unit is integral
        if (type.amountPerUnit() == 1 && internalValue.get().scale() > 0) {
            return OptionalLong.empty();
        }

        var externalValue = convertToExternalValue(internalValue.get());
        if (externalValue < minValue) {
            return OptionalLong.empty();
        } else if (externalValue > maxValue) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(externalValue);
    }

    public void setLongValue(long value) {
        this.lastLongValue = value;
        var internalValue = convertToInternalValue(Longs.constrainToRange(value, minValue, maxValue));
        setValue(decimalFormat.format(internalValue));
        moveCursorToEnd();
        setHighlightPos(0);
        validate();
    }

    private Optional<BigDecimal> getValueInternal() {
        var textValue = getValue();
        if (textValue.startsWith("=")) {
            textValue = textValue.substring(1);
        }
        return MathExpressionParser.parse(textValue, decimalFormat);
    }

    private boolean isNumber() {
        var position = new ParsePosition(0);
        var textValue = getValue().trim();
        decimalFormat.parse(textValue, position);
        return position.getErrorIndex() == -1 && position.getIndex() == textValue.length();
    }

    private void setValueInternal(BigDecimal value) {
        setValue(decimalFormat.format(value));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partial);
        if (!isFluid) return;
        guiGraphics.drawString(Minecraft.getInstance().font, "B", getX() + width - PADDING, getY(), TEXT_COLOR, false);
    }

    private void validate() {
        List<Component> validationErrors = new ArrayList<>();
        List<Component> infoMessages = new ArrayList<>();

        var possibleValue = getValueInternal();
        if (possibleValue.isPresent()) {
            // Reject decimal values if the unit is integral
            if (type.amountPerUnit() == 1 && possibleValue.get().scale() > 0) {
                validationErrors.add(GuiText.NumberNonInteger.text());
            } else {
                var value = convertToExternalValue(possibleValue.get());
                if (value < minValue) {
                    var formatted = decimalFormat.format(convertToInternalValue(minValue));
                    validationErrors.add(GuiText.NumberLessThanMinValue.text(formatted));
                } else if (value > maxValue) {
                    var formatted = decimalFormat.format(convertToInternalValue(maxValue));
                    validationErrors.add(GuiText.NumberGreaterThanMaxValue.text(formatted));
                } else if (!isNumber()) { // is a mathematical expression
                    // displaying the evaluation of the expression
                    infoMessages.add(Component.literal("= " + decimalFormat.format(possibleValue.get())));
                }
            }
        } else {
            validationErrors.add(GuiText.InvalidNumber.text());
        }

        boolean valid = validationErrors.isEmpty();
        var tooltip = valid ? infoMessages : validationErrors;
        if (tooltip.isEmpty()) {
            if (!isOutput) {

                tooltip.add(Component.translatable(
                        "gui.tooltips.advanced_ae.NumberTextFieldInputHint",
                        InputConstants.getKey("key.keyboard" + ".enter").getDisplayName()));
            } else {

                tooltip.add(Component.translatable(
                        "gui.tooltips.advanced_ae.NumberTextFieldOutputHint",
                        InputConstants.getKey("key.keyboard" + ".enter").getDisplayName()));
            }
        }
        setTextColor(valid ? TEXT_COLOR : ERROR_COLOR);
        setTooltipMessage(tooltip);
    }

    private long convertToExternalValue(BigDecimal internalValue) {
        var multiplicand = BigDecimal.valueOf(type.amountPerUnit());
        var value = internalValue.multiply(multiplicand, MathContext.DECIMAL128);
        value = value.setScale(0, RoundingMode.UP);
        return value.longValue();
    }

    private BigDecimal convertToInternalValue(long externalValue) {
        var divisor = BigDecimal.valueOf(type.amountPerUnit());
        return BigDecimal.valueOf(externalValue).divide(divisor, MathContext.DECIMAL128);
    }

    void adjustToType(@Nullable AEKey key) {
        this.isFluid = key instanceof AEFluidKey;
        this.type = NumberEntryType.of(key);
        if (isFluid) {
            setWidth(width - PADDING - 10);
        } else {
            setWidth(width - PADDING);
        }
    }
}
