package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.pedroksl.advanced_ae.client.gui.QuantumCrafterTermScreen;

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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterTermScreen<?> screen) {
            screen.clear();
        }
    }
}
