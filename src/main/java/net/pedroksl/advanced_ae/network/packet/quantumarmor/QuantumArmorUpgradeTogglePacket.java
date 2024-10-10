package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.gui.QuantumArmorConfigMenu;

import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

public record QuantumArmorUpgradeTogglePacket(UpgradeType upgradeType, boolean state) implements ServerboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, QuantumArmorUpgradeTogglePacket> STREAM_CODEC =
            StreamCodec.ofMember(QuantumArmorUpgradeTogglePacket::write, QuantumArmorUpgradeTogglePacket::decode);

    public static final CustomPacketPayload.Type<QuantumArmorUpgradeTogglePacket> TYPE =
            CustomAppEngPayload.createType("aae_upgrade_toggle");

    @Override
    public CustomPacketPayload.Type<QuantumArmorUpgradeTogglePacket> type() {
        return TYPE;
    }

    public static QuantumArmorUpgradeTogglePacket decode(RegistryFriendlyByteBuf stream) {
        var upgradeType = stream.readEnum(UpgradeType.class);
        var state = stream.readBoolean();
        return new QuantumArmorUpgradeTogglePacket(upgradeType, state);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeEnum(upgradeType);
        data.writeBoolean(state);
    }

    @Override
    public void handleOnServer(ServerPlayer serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumArmorConfigMenu menu) {
            menu.toggleUpgradeEnable(upgradeType, state);
        }
    }
}
