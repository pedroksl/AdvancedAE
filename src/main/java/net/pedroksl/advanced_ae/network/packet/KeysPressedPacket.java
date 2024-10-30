package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

public record KeysPressedPacket(String data, boolean noKey) implements ServerboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, KeysPressedPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            KeysPressedPacket::data,
            ByteBufCodecs.BOOL,
            KeysPressedPacket::noKey,
            KeysPressedPacket::new);

    public static Type<KeysPressedPacket> TYPE = CustomAppEngPayload.createType("aae_keys_pressed_packet");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        player.getPersistentData().putBoolean(data, noKey);
    }
}
