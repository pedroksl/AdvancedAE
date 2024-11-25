package net.pedroksl.advanced_ae.client.widgets;

import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.network.packet.AAEConfigButtonPacket;

import appeng.api.config.Setting;

public class AAEServerSettingToggleButton<T extends Enum<T>> extends AAESettingToggleButton<T> {
    public AAEServerSettingToggleButton(Setting<T> setting, T val) {
        super(setting, val, AAEServerSettingToggleButton::sendToServer);
    }

    private static <T extends Enum<T>> void sendToServer(AAESettingToggleButton<T> button, boolean backwards) {
        AAENetworkHandler.INSTANCE.sendToServer(new AAEConfigButtonPacket(button.getSetting(), backwards));
    }
}
