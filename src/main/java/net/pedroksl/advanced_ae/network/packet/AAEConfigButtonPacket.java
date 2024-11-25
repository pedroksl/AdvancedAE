package net.pedroksl.advanced_ae.network.packet;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.api.AAESettings;

import appeng.api.config.Setting;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.core.AELog;
import appeng.menu.AEBaseMenu;
import appeng.util.EnumCycler;

public class AAEConfigButtonPacket implements IMessage<AAEConfigButtonPacket> {

    private Setting<?> option;
    private boolean rotationDirection;

    public AAEConfigButtonPacket() {}

    public AAEConfigButtonPacket(Setting<?> option, boolean rotationDirection) {
        this.option = option;
        this.rotationDirection = rotationDirection;
    }

    @Override
    public void fromBytes(FriendlyByteBuf stream) {
        this.option = AAESettings.getOrThrow(stream.readUtf());
        this.rotationDirection = stream.readBoolean();
    }

    @Override
    public void toBytes(FriendlyByteBuf data) {
        data.writeUtf(option.getName());
        data.writeBoolean(rotationDirection);
    }

    @Override
    public void onMessage(Player player) {
        if (player.containerMenu instanceof AEBaseMenu baseMenu) {
            if (baseMenu.getTarget() instanceof IConfigurableObject configurableObject) {
                var cm = configurableObject.getConfigManager();
                if (cm.hasSetting(option)) {
                    cycleSetting(cm, option);
                } else {
                    AELog.info("Ignoring unsupported setting %s sent by client on %s", option, baseMenu.getTarget());
                }
            }
        }
    }

    private <T extends Enum<T>> void cycleSetting(IConfigManager cm, Setting<T> setting) {
        var currentValue = cm.getSetting(setting);
        var nextValue = EnumCycler.rotateEnum(currentValue, rotationDirection, setting.getValues());
        cm.putSetting(setting, nextValue);
    }

    @Override
    public Class<AAEConfigButtonPacket> getPacketClass() {
        return AAEConfigButtonPacket.class;
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
