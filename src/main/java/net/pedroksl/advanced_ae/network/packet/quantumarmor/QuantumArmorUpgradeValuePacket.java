package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.gui.QuantumArmorConfigMenu;

import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

public record QuantumArmorUpgradeValuePacket(UpgradeType upgradeType, int state) implements ServerboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, QuantumArmorUpgradeValuePacket> STREAM_CODEC =
            StreamCodec.ofMember(QuantumArmorUpgradeValuePacket::write, QuantumArmorUpgradeValuePacket::decode);

    public static final Type<QuantumArmorUpgradeValuePacket> TYPE = CustomAppEngPayload.createType("aae_upgrade_value");

    @Override
    public Type<QuantumArmorUpgradeValuePacket> type() {
        return TYPE;
    }

    public static QuantumArmorUpgradeValuePacket decode(RegistryFriendlyByteBuf stream) {
        var upgradeType = stream.readEnum(UpgradeType.class);
        var state = stream.readInt();
        return new QuantumArmorUpgradeValuePacket(upgradeType, state);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeEnum(upgradeType);
        data.writeInt(state);
    }

    @Override
    public void handleOnServer(ServerPlayer serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumArmorConfigMenu menu) {
            menu.updateUpgradeValue(upgradeType, state);
        }
    }
}
