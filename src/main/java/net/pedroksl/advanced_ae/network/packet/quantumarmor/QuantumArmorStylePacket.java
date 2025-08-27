package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.gui.QuantumArmorStyleConfigMenu;

import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

public record QuantumArmorStylePacket(List<Integer> slots, int color) implements ServerboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, QuantumArmorStylePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT.apply(ByteBufCodecs.list()),
                    QuantumArmorStylePacket::slots,
                    ByteBufCodecs.INT,
                    QuantumArmorStylePacket::color,
                    QuantumArmorStylePacket::new);

    public static final Type<QuantumArmorStylePacket> TYPE = CustomAppEngPayload.createType("aae_armor_style");

    @Override
    public Type<QuantumArmorStylePacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(ServerPlayer serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumArmorStyleConfigMenu menu) {
            menu.updateItemColors(this.slots, this.color);
        }
    }
}
