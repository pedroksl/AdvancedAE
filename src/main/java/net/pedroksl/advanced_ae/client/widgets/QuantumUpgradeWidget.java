package net.pedroksl.advanced_ae.client.widgets;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.pedroksl.advanced_ae.client.gui.QuantumArmorConfigScreen;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEIcon;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEIconButton;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import appeng.client.gui.Icon;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AECheckbox;
import appeng.client.gui.widgets.IconButton;

@OnlyIn(Dist.CLIENT)
public class QuantumUpgradeWidget {
    private final QuantumArmorConfigScreen host;
    private final int index;
    private final int x;
    private final int y;
    private final ScreenStyle style;
    private final UpgradeState state;
    private final Map<String, AbstractWidget> children = new HashMap<>();

    private ConfigButton configureButton;
    private AECheckbox enableButton;
    private UninstallButton uninstallButton;

    public QuantumUpgradeWidget(
            QuantumArmorConfigScreen host, int index, int x, int y, ScreenStyle style, UpgradeState state) {
        this.host = host;
        this.index = index;
        this.x = x;
        this.y = y;
        this.style = style;
        this.state = state;
    }

    public void add() {
        if (state.getType().getSettingType() != UpgradeType.SettingType.NONE) {
            configureButton = new ConfigButton(x + 69, y - 6, this::configRequested);
            host.addChildWidget("upgrade_config" + index, configureButton, children);
        }

        enableButton = new AECheckbox(x + 87, y - 3, 16, 16, style, Component.empty());
        enableButton.setSelected(state.isEnabled());
        enableButton.setChangeListener(this::toggleEnable);
        host.addChildWidget("upgrade_enable" + index, enableButton, children);

        uninstallButton = new UninstallButton(x + 110, y - 6, this::uninstallRequested);
        host.addChildWidget("upgrade_uninstall" + index, uninstallButton, children);
    }

    public void hide() {
        children.values().forEach(w -> w.visible = false);
    }

    public Collection<AbstractWidget> children() {
        return children.values();
    }

    public String getName() {
        return state.getType().name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private void configRequested(Button button) {
        // Request config window to open
        var a = 1;
        a *= 2;
        var b = a;
    }

    private void toggleEnable() {
        var a = 1;
        a *= 2;
        var b = a;
    }

    private void uninstallRequested(Button button) {
        var a = 1;
        a *= 2;
        var b = a;
    }

    static class ConfigButton extends IconButton {
        public ConfigButton(int x, int y, OnPress onPress) {
            super(onPress);
            setX(x);
            setY(y);
            setDisableBackground(true);
        }

        @Override
        protected Icon getIcon() {
            return isHoveredOrFocused() ? Icon.COG : Icon.COG_DISABLED;
        }
    }

    static class UninstallButton extends AAEIconButton {
        public UninstallButton(int x, int y, OnPress onPress) {
            super(onPress);
            setX(x);
            setY(y);
            setDisableBackground(true);
        }

        @Override
        protected AAEIcon getIcon() {
            return isHoveredOrFocused() ? AAEIcon.CLEAR : AAEIcon.CLEAR_DISABLED;
        }
    }
}
