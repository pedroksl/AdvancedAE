package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

public record ClearQuantumCrafterTerminalPacket() implements ClientboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClearQuantumCrafterTerminalPacket> STREAM_CODEC =
            StreamCodec.ofMember(ClearQuantumCrafterTerminalPacket::write, ClearQuantumCrafterTerminalPacket::decode);

    public static final Type<ClearQuantumCrafterTerminalPacket> TYPE =
            CustomAppEngPayload.createType("clear_quantum_crafter_terminal");

    @Override
    public Type<ClearQuantumCrafterTerminalPacket> type() {
        return TYPE;
    }

    public static ClearQuantumCrafterTerminalPacket decode(RegistryFriendlyByteBuf data) {
        return new ClearQuantumCrafterTerminalPacket();
    }

    public void write(RegistryFriendlyByteBuf data) {}
}
