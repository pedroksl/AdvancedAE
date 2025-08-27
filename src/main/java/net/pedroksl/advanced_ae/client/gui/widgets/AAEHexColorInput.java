package net.pedroksl.advanced_ae.client.gui.widgets;

import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.pedroksl.advanced_ae.common.definitions.AAEText;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ConfirmableTextField;

public class AAEHexColorInput extends ConfirmableTextField {

    private final HexFormat hexFormat = HexFormat.of();

    public AAEHexColorInput(
            ScreenStyle style, Font fontRenderer, int x, int y, int width, int height, Consumer<Integer> onConfirm) {
        super(style, fontRenderer, x, y, width, height);

        setBordered(false);
        setMaxLength(7);
        setTextColor(16777215);
        setSelectionColor(-16777088);
        setVisible(true);
        setResponder(text -> this.validate());
        setOnConfirm(() -> {
            var opt = getIntValue();
            if (opt.isPresent()) {
                onConfirm.accept(opt.getAsInt());
            }
        });
        this.validate();
    }

    public OptionalInt getIntValue() {
        var value = getValue();
        if (value.startsWith("#")) {
            value = value.substring(1);
        }
        try {
            var intValue = Integer.parseInt(value, 16);
            return OptionalInt.of(intValue);
        } catch (NumberFormatException exception) {
            return OptionalInt.empty();
        }
    }

    public void setColor(int color) {
        setValue(hexFormat.toHexDigits(color).substring(2));
    }

    private void validate() {
        List<Component> validation = new ArrayList<>();
        if (getIntValue().isEmpty()) {
            validation.add(AAEText.InvalidHexInput.text());
        }
        setTooltipMessage(validation);
    }
}
