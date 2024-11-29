package net.pedroksl.advanced_ae.client.gui.widgets;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.pedroksl.advanced_ae.common.definitions.AAEText;

public class AAEActionButton extends AAEIconButton {
    private static final Pattern PATTERN_NEW_LINE = Pattern.compile("\\n", Pattern.LITERAL);
    private final AAEIcon icon;

    public AAEActionButton(AAEActionItems action, Runnable onPress) {
        this(action, a -> onPress.run());
    }

    public AAEActionButton(AAEActionItems action, Consumer<AAEActionItems> onPress) {
        super(btn -> onPress.accept(action));

        AAEText displayName;
        AAEText displayValue;
        switch (action) {
            case F_FLUSH -> {
                icon = AAEIcon.CLEAR_SMALL;
                displayName = AAEText.ClearButton;
                displayValue = AAEText.ClearFluidButtonHint;
            }
            case CLEAR -> {
                icon = AAEIcon.CLEAR_SMALL;
                displayName = AAEText.ClearButton;
                displayValue = AAEText.ClearSidesButtonHint;
            }
            default -> throw new IllegalArgumentException("Unknown ActionItem: " + action);
        }

        setMessage(buildMessage(displayName, displayValue));
    }

    @Override
    protected AAEIcon getIcon() {
        return icon;
    }

    private Component buildMessage(AAEText displayName, @Nullable AAEText displayValue) {
        String name = displayName.text().getString();
        if (displayValue == null) {
            return Component.literal(name);
        }
        String value = displayValue.text().getString();

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
