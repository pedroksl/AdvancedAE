package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.api.IFluidTankHandler;

import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

public record FluidTankItemUsePacket(int index, int button) implements ServerboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidTankItemUsePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    FluidTankItemUsePacket::index,
                    ByteBufCodecs.INT,
                    FluidTankItemUsePacket::button,
                    FluidTankItemUsePacket::new);

    public static final Type<FluidTankItemUsePacket> TYPE = CustomAppEngPayload.createType("aae_fluid_tank_packet");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(ServerPlayer serverPlayer) {
        if (serverPlayer.containerMenu instanceof IFluidTankHandler handler) {
            handler.onItemUse(index, button);
        }
    }
}
