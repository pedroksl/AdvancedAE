package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.api.AAESettings;

import appeng.api.config.Setting;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.core.AELog;
import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;
import appeng.menu.AEBaseMenu;
import appeng.util.EnumCycler;

public record AAEConfigButtonPacket(Setting<?> option, boolean rotationDirection) implements ServerboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, AAEConfigButtonPacket> STREAM_CODEC =
            StreamCodec.ofMember(AAEConfigButtonPacket::write, AAEConfigButtonPacket::decode);

    public static final Type<AAEConfigButtonPacket> TYPE = CustomAppEngPayload.createType("aae_config_button");

    @Override
    public Type<AAEConfigButtonPacket> type() {
        return TYPE;
    }

    public static AAEConfigButtonPacket decode(RegistryFriendlyByteBuf stream) {
        var option = AAESettings.getOrThrow(stream.readUtf());
        var rotationDirection = stream.readBoolean();
        return new AAEConfigButtonPacket(option, rotationDirection);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeUtf(option.getName());
        data.writeBoolean(rotationDirection);
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
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
}
