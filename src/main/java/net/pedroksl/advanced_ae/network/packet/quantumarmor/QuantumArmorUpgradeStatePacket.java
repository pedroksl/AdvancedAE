package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.client.gui.QuantumArmorConfigScreen;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

public record QuantumArmorUpgradeStatePacket() implements ClientboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, QuantumArmorUpgradeStatePacket> STREAM_CODEC =
            StreamCodec.ofMember(QuantumArmorUpgradeStatePacket::write, QuantumArmorUpgradeStatePacket::decode);

    public static final Type<QuantumArmorUpgradeStatePacket> TYPE = CustomAppEngPayload.createType("aae_upgrade_state");

    @Override
    public Type<QuantumArmorUpgradeStatePacket> type() {
        return TYPE;
    }

    public static QuantumArmorUpgradeStatePacket decode(RegistryFriendlyByteBuf stream) {
        return new QuantumArmorUpgradeStatePacket();
    }

    public void write(RegistryFriendlyByteBuf data) {}

    @Override
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumArmorConfigScreen screen) {
            screen.refreshList();
        }
    }
}
