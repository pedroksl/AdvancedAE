package net.pedroksl.advanced_ae.client.gui.widgets;

import java.util.*;
import java.util.function.Consumer;

import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.ae2addonlib.client.widgets.IActionEnum;
import net.pedroksl.ae2addonlib.client.widgets.ToolbarActionButton;

public class AAEToolbarActionButton extends ToolbarActionButton {
    public AAEToolbarActionButton(IActionEnum action, Runnable onPress) {
        this(action, a -> onPress.run());
    }

    public AAEToolbarActionButton(IActionEnum action, Consumer<IActionEnum> onPress) {
        super(action, onPress);
    }

    @Override
    protected void registerAppearances() {
        registerApp(
                AAEIcon.DIRECTION_OUTPUT,
                AAEActionItems.DIRECTIONAL_OUTPUT,
                AAEText.DirectionalOutput,
                AAEText.DirectionOutputHint);
    }
}
