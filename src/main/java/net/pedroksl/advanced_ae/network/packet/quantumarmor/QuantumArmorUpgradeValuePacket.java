package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.gui.QuantumArmorConfigMenu;

public class QuantumArmorUpgradeValuePacket implements IMessage<QuantumArmorUpgradeValuePacket> {

    private UpgradeType upgradeType;
    private int state;

    public QuantumArmorUpgradeValuePacket() {

    }

    public QuantumArmorUpgradeValuePacket(UpgradeType upgradeType, int state) {
        this.upgradeType = upgradeType;
        this.state = state;
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        upgradeType = buf.readEnum(UpgradeType.class);
        state = buf.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeEnum(upgradeType);
        buf.writeInt(state);
    }

    @Override
    public void onMessage(Player serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumArmorConfigMenu menu) {
            menu.openNumInputConfigScreen(upgradeType, state);
        }
    }

    @Override
    public Class<QuantumArmorUpgradeValuePacket> getPacketClass() {
        return QuantumArmorUpgradeValuePacket.class;
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
