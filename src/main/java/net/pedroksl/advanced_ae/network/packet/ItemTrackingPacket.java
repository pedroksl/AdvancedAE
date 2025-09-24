package net.pedroksl.advanced_ae.network.packet;

import java.util.UUID;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

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

    @Override
    public void handleOnClient(IPayloadContext context) {
        Player player = context.player();
        if (player != null && player.level() != null) {
            context.enqueueWork(() -> {
                Entity entity = player.level().getEntity(this.entityId);
                if (entity instanceof ItemEntity) {
                    ((ItemEntity) entity).thrower = this.thrower;
                    ((ItemEntity) entity).setPickUpDelay(this.pickupDelay);
                }
            });
        }
    }
}
