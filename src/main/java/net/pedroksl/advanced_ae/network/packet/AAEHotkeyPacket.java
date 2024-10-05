package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.client.Hotkeys;
import net.pedroksl.advanced_ae.common.definitions.AAEHotkeys;

import appeng.core.AELog;
import appeng.core.localization.PlayerMessages;
import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

public record AAEHotkeyPacket(String hotkey) implements ServerboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, AAEHotkeyPacket> STREAM_CODEC =
            StreamCodec.ofMember(AAEHotkeyPacket::write, AAEHotkeyPacket::decode);

    public static final Type<AAEHotkeyPacket> TYPE = CustomAppEngPayload.createType("aae_hotkey");

    @Override
    public Type<AAEHotkeyPacket> type() {
        return TYPE;
    }

    public AAEHotkeyPacket(Hotkeys.AAEHotkey hotkey) {
        this(hotkey.name());
    }

    public static AAEHotkeyPacket decode(RegistryFriendlyByteBuf stream) {
        var hotkey = stream.readUtf();
        return new AAEHotkeyPacket(hotkey);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeUtf(this.hotkey);
    }

    public void handleOnServer(ServerPlayer player) {
        var actions = AAEHotkeys.REGISTRY.get(hotkey);
        if (actions == null) {
            player.sendSystemMessage(PlayerMessages.UnknownHotkey.text()
                    .copy()
                    .append(Component.translatable("key.advanced_ae." + hotkey)));
            AELog.warn("Player %s tried using unknown hotkey \"%s\"", player, hotkey);
            return;
        }

        for (var action : actions) {
            if (action.run(player)) {
                break;
            }
        }
    }
}
