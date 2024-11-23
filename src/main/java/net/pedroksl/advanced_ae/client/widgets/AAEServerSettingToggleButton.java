package net.pedroksl.advanced_ae.client.widgets;

import appeng.api.config.Setting;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.ConfigButtonPacket;

public class AAEServerSettingToggleButton<T extends Enum<T>> extends AAESettingToggleButton<T> {
    public AAEServerSettingToggleButton(Setting<T> setting, T val) {
        super(setting, val, AAEServerSettingToggleButton::sendToServer);
    }

    private static <T extends Enum<T>> void sendToServer(AAESettingToggleButton<T> button, boolean backwards) {
        NetworkHandler.instance().sendToServer(new ConfigButtonPacket(button.getSetting(), backwards));
    }
}
