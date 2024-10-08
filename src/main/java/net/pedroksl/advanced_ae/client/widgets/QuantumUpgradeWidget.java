package net.pedroksl.advanced_ae.client.widgets;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.components.AbstractWidget;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.pedroksl.advanced_ae.client.gui.QuantumArmorConfigScreen;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEActionButton;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ToggleButton;

@OnlyIn(Dist.CLIENT)
public class QuantumUpgradeWidget {
    private final QuantumArmorConfigScreen host;
    private final int index;
    private final int x;
    private final int y;
    private final ScreenStyle style;
    private final UpgradeType type;
    private final Map<String, AbstractWidget> children = new HashMap<>();

    private AAEActionButton configureButton;
    private ToggleButton enableButton;
    private AAEActionButton uninstallButton;

    public QuantumUpgradeWidget(
            QuantumArmorConfigScreen host, int index, int x, int y, ScreenStyle style, UpgradeType type) {
        this.host = host;
        this.index = index;
        this.x = x;
        this.y = y;
        this.style = style;
        this.type = type;
    }
}
