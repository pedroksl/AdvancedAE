package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.pedroksl.advanced_ae.gui.OutputDirectionMenu;

import appeng.api.orientation.RelativeSide;
import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

public record UpdateSideStatusPacket(RelativeSide side) implements ServerboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSideStatusPacket> STREAM_CODEC =
            StreamCodec.composite(
                    NeoForgeStreamCodecs.enumCodec(RelativeSide.class),
                    UpdateSideStatusPacket::side,
                    UpdateSideStatusPacket::new);

    public static final Type<UpdateSideStatusPacket> TYPE = CustomAppEngPayload.createType("update_side_status");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        if (player.containerMenu instanceof OutputDirectionMenu menu) {
            menu.updateSideStatus(side);
        }
    }
}
