package net.pedroksl.advanced_ae.client.widgets;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.pedroksl.advanced_ae.client.gui.QuantumArmorConfigScreen;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEIcon;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.network.packet.quantumarmor.QuantumArmorUpgradeTogglePacket;
import net.pedroksl.ae2addonlib.client.widgets.AddonIconButton;

import appeng.client.gui.Icon;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AECheckbox;
import appeng.client.gui.widgets.IconButton;

@OnlyIn(Dist.CLIENT)
public class QuantumUpgradeWidget {
    private final QuantumArmorConfigScreen host;
    private final int index;
    private final int x;
    private int y;
    private final ScreenStyle style;
    private UpgradeState state;
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
        if (state.type().getSettingType() != UpgradeType.SettingType.NONE) {
            configureButton = new ConfigButton(x + 82, y - 1, this::configRequested);
            host.addChildWidget("upgrade_config" + index, configureButton, children);
        }

        enableButton = new AECheckbox(x + 98, y, 16, 16, style, Component.empty());
        enableButton.setSelected(state.enabled());
        enableButton.setChangeListener(this::toggleEnable);
        host.addChildWidget("upgrade_enable" + index, enableButton, children);

        uninstallButton = new UninstallButton(x + 110, y - 1, this::uninstallRequested);
        host.addChildWidget("upgrade_uninstall" + index, uninstallButton, children);
    }

    public void hide() {
        children.values().forEach(w -> {
            if (w.visible) {
                w.visible = false;
            }
        });
    }

    public void show() {
        children.values().forEach(w -> {
            if (!w.visible) {
                w.visible = true;
            }
        });
    }

    public Collection<AbstractWidget> children() {
        return children.values();
    }

    public String getName() {
        return state.type().name;
    }

    public UpgradeType getType() {
        return state.type();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y, int topPos) {
        this.y = y;
        this.children.values().forEach(w -> {
            if (w instanceof AECheckbox) {
                w.setY(topPos + y);
            } else {
                w.setY(topPos + y - 1);
            }
        });
    }

    public void setState(UpgradeState state) {
        this.state = state;
        this.enableButton.setSelected(state.enabled());
    }

    private void configRequested(Button button) {
        host.openConfigDialog(state);
    }

    private void toggleEnable() {
        AAENetworkHandler.INSTANCE.sendToServer(
                new QuantumArmorUpgradeTogglePacket(state.type(), enableButton.isSelected()));
    }

    private void uninstallRequested(Button button) {
        host.requestUninstall(state.type());
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
            return isHoveredOrFocused() ? Icon.WRENCH : Icon.WRENCH_DISABLED;
        }
    }

    static class UninstallButton extends AddonIconButton {
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
