package net.pedroksl.advanced_ae.client.widgets;

import java.util.*;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public class AAEToolbarActionButton extends AAEIconButton {
    private static Map<AAEActionItems, ButtonAppearance> appearances;
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
                    Component.translatable("gui.tooltips.advanced_ae.DirectionalOutput"),
                    Component.translatable("gui.tooltips.advanced_ae.DirectionOutputHint"));
        }
    }

    private static void registerApp(AAEIcon icon, AAEActionItems action, Component title, Component hint) {
        var lines = new ArrayList<Component>();
        lines.add(title);
        Collections.addAll(lines, hint);

        appearances.put(action, new ButtonAppearance(icon, null, lines));
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
