package net.pedroksl.advanced_ae.client.widgets;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;

public class AAEActionButton extends IconButton {
    private static final Pattern PATTERN_NEW_LINE = Pattern.compile("\\n", Pattern.LITERAL);
    private final Icon icon;

    public AAEActionButton(AAEActionItems action, Runnable onPress) {
        this(action, a -> onPress.run());
    }

    public AAEActionButton(AAEActionItems action, Consumer<AAEActionItems> onPress) {
        super(btn -> onPress.accept(action));

        Component displayName;
        Component displayValue;
        switch (action) {
            case F_FLUSH -> {
                icon = Icon.CLEAR;
                displayName = Component.translatable("gui.tooltips.advanced_ae.ClearButton");
                ;
                displayValue = Component.translatable("gui.tooltips.advanced_ae.ClearFluidButtonHint");
            }
            case CLEAR -> {
                icon = Icon.CLEAR;
                displayName = Component.translatable("gui.tooltips.advanced_ae.ClearButton");
                ;
                displayValue = Component.translatable("gui.tooltips.advanced_ae.ClearSidesButtonHint");
            }
            default -> throw new IllegalArgumentException("Unknown ActionItem: " + action);
        }

        setMessage(buildMessage(displayName, displayValue));
    }

    @Override
    protected Icon getIcon() {
        return icon;
    }

    private Component buildMessage(Component displayName, @Nullable Component displayValue) {
        String name = displayName.getString();
        if (displayValue == null) {
            return Component.literal(name);
        }
        String value = displayValue.getString();

        value = PATTERN_NEW_LINE.matcher(value).replaceAll("\n");
        final StringBuilder sb = new StringBuilder(value);

        int i = sb.lastIndexOf("\n");
        if (i <= 0) {
            i = 0;
        }
        while (i + 30 < sb.length() && (i = sb.lastIndexOf(" ", i + 30)) != -1) {
            sb.replace(i, i + 1, "\n");
        }

        return Component.literal(name + '\n' + sb);
    }
}
