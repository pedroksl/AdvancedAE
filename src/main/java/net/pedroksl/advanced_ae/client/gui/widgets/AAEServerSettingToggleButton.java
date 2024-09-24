package net.pedroksl.advanced_ae.client.gui.widgets;

import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.network.packet.AAEConfigButtonPacket;

import appeng.api.config.Setting;
import appeng.core.network.ServerboundPacket;

public class AAEServerSettingToggleButton<T extends Enum<T>> extends AAESettingToggleButton<T> {
    public AAEServerSettingToggleButton(Setting<T> setting, T val) {
        super(setting, val, AAEServerSettingToggleButton::sendToServer);
    }

    private static <T extends Enum<T>> void sendToServer(AAESettingToggleButton<T> button, boolean backwards) {
        ServerboundPacket message = new AAEConfigButtonPacket(button.getSetting(), backwards);
        PacketDistributor.sendToServer(message);
    }
}
