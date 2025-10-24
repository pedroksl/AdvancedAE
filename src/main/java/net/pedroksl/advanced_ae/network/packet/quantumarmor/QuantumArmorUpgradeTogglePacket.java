package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.gui.QuantumArmorConfigMenu;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class QuantumArmorUpgradeTogglePacket extends AddonPacket {

    private final UpgradeType upgradeType;
    private final boolean state;

    public QuantumArmorUpgradeTogglePacket(FriendlyByteBuf stream) {
        upgradeType = stream.readEnum(UpgradeType.class);
        state = stream.readBoolean();
    }

    public QuantumArmorUpgradeTogglePacket(UpgradeType upgradeType, boolean state) {
        this.upgradeType = upgradeType;
        this.state = state;
    }

    @Override
    public void write(FriendlyByteBuf stream) {
        stream.writeEnum(upgradeType);
        stream.writeBoolean(state);
    }

    @Override
    public void serverPacketData(ServerPlayer serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumArmorConfigMenu menu) {
            menu.toggleUpgradeEnable(upgradeType, state);
        }
    }
}
