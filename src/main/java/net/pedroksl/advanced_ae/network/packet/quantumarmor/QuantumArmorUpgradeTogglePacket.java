package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.gui.QuantumArmorConfigMenu;

public class QuantumArmorUpgradeTogglePacket implements IMessage<QuantumArmorUpgradeTogglePacket> {

    private UpgradeType upgradeType;
    private boolean state;

    public QuantumArmorUpgradeTogglePacket() {

    }

    public QuantumArmorUpgradeTogglePacket(UpgradeType upgradeType, boolean state) {
        this.upgradeType = upgradeType;
        this.state = state;
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        upgradeType = buf.readEnum(UpgradeType.class);
        state = buf.readBoolean();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeEnum(upgradeType);
        buf.writeBoolean(state);
    }

    @Override
    public void onMessage(Player serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumArmorConfigMenu menu) {
            menu.toggleUpgradeEnable(upgradeType, state);
        }
    }

    @Override
    public Class<QuantumArmorUpgradeTogglePacket> getPacketClass() {
        return QuantumArmorUpgradeTogglePacket.class;
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
