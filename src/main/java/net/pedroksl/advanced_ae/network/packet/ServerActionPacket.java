package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEActionItems;

import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

public record ServerActionPacket(AAEActionItems action) implements ServerboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerActionPacket> STREAM_CODEC =
            StreamCodec.ofMember(ServerActionPacket::write, ServerActionPacket::decode);

    public static final CustomPacketPayload.Type<ServerActionPacket> TYPE =
            CustomAppEngPayload.createType("aae_server_action");

    @Override
    public CustomPacketPayload.Type<ServerActionPacket> type() {
        return TYPE;
    }

    public ServerActionPacket(AAEActionItems action) {
        this.action = action;
    }

    public static ServerActionPacket decode(RegistryFriendlyByteBuf stream) {
        AAEActionItems action = stream.readEnum(AAEActionItems.class);
        return new ServerActionPacket(action);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeEnum(this.action);
    }

    @Override
    public void handleOnServer(ServerPlayer serverPlayer) {}

    public AAEActionItems action() {
        return this.action;
    }
}
