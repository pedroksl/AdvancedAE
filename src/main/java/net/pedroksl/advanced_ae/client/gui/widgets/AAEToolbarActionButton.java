package net.pedroksl.advanced_ae.client.gui.widgets;

import java.util.*;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.pedroksl.advanced_ae.common.definitions.AAEText;

import appeng.core.localization.LocalizationEnum;

public class AAEToolbarActionButton extends AAEIconButton {
    private static Map<AAEActionItems, AAEToolbarActionButton.ButtonAppearance> appearances;
    private final AAEActionItems action;

    public AAEToolbarActionButton(AAEActionItems action, Runnable onPress) {
        this(action, a -> onPress.run());
    }

    public AAEToolbarActionButton(AAEActionItems action, Consumer<AAEActionItems> onPress) {
        super(btn -> onPress.accept(action));

        this.action = action;
        if (appearances == null) {
            appearances = new HashMap<>();
            registerApp(
                    AAEIcon.DIRECTION_OUTPUT,
                    AAEActionItems.DIRECTIONAL_OUTPUT,
                    AAEText.DirectionalOutput,
                    AAEText.DirectionOutputHint);
        }
    }

    private static void registerApp(
            AAEIcon icon, AAEActionItems action, LocalizationEnum title, LocalizationEnum hint) {
        var lines = new ArrayList<Component>();
        lines.add(title.text());
        Collections.addAll(lines, hint.text());

        appearances.put(action, new AAEToolbarActionButton.ButtonAppearance(icon, null, lines));
    }

    public AAEActionItems getAction() {
        return this.action;
    }

    @Nullable
    private AAEToolbarActionButton.ButtonAppearance getAppearance() {
        if (this.action != null) {
            return appearances.get(action);
        }
        return null;
    }

    @Override
    protected AAEIcon getIcon() {
        var app = getAppearance();
        if (app != null && app.icon != null) {
            return app.icon;
        }
        return AAEIcon.TOOLBAR_BUTTON_BACKGROUND;
    }

    private record ButtonAppearance(@Nullable AAEIcon icon, @Nullable Item item, List<Component> tooltipLines) {}
}
