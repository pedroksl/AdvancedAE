package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.common.helpers.KeysPressed;

import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

public record KeysPressedPacket(KeysPressed data) implements ServerboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, KeysPressedPacket> STREAM_CODEC =
            StreamCodec.composite(KeysPressed.STREAM_CODEC, KeysPressedPacket::data, KeysPressedPacket::new);

    public static Type<KeysPressedPacket> TYPE = CustomAppEngPayload.createType("aae_keys_pressed_packet");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        player.getPersistentData().putByte(KeysPressed.KEYS_PRESSED, data.toByte());
    }
}
