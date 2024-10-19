package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.events.AAEPlayerEvents;

import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

public record NoKeyPressedPacket(boolean noKey) implements ServerboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, NoKeyPressedPacket> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.BOOL, NoKeyPressedPacket::noKey, NoKeyPressedPacket::new);

    public static Type<NoKeyPressedPacket> TYPE = CustomAppEngPayload.createType("aae_no_key_pressed_packet");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        player.getPersistentData().putBoolean(AAEPlayerEvents.NO_KEY_DATA, noKey);
    }
}
