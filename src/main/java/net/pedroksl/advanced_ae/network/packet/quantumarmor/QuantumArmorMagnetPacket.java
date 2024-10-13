package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.gui.QuantumArmorConfigMenu;

import appeng.api.stacks.GenericStack;
import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

public record QuantumArmorMagnetPacket(int currentValue, List<GenericStack> filter, boolean blacklist)
        implements ServerboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, QuantumArmorMagnetPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    QuantumArmorMagnetPacket::currentValue,
                    GenericStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    QuantumArmorMagnetPacket::filter,
                    ByteBufCodecs.BOOL,
                    QuantumArmorMagnetPacket::blacklist,
                    QuantumArmorMagnetPacket::new);

    public static final Type<QuantumArmorMagnetPacket> TYPE = CustomAppEngPayload.createType("aae_magnet_packet");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(ServerPlayer serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumArmorConfigMenu menu) {
            menu.openMagnetScreen(currentValue, filter, blacklist);
        }
    }
}
