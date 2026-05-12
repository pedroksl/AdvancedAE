package net.pedroksl.advanced_ae.network.packet;

import java.util.UUID;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

public record ItemTrackingPacket(UUID thrower, int entityId, int pickupDelay) implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemTrackingPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(UUID::fromString, UUID::toString),
            ItemTrackingPacket::thrower,
            ByteBufCodecs.INT,
            ItemTrackingPacket::entityId,
            ByteBufCodecs.INT,
            ItemTrackingPacket::pickupDelay,
            ItemTrackingPacket::new);

    public static final Type<ItemTrackingPacket> TYPE = CustomAppEngPayload.createType("aae_item_tracking_packet");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
