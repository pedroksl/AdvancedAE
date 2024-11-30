package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import appeng.api.stacks.GenericStack;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.gui.QuantumArmorConfigMenu;

import java.util.ArrayList;
import java.util.List;

public class QuantumArmorUpgradeFilterPacket implements IMessage<QuantumArmorUpgradeFilterPacket> {

    private UpgradeType upgradeType;
    private List<GenericStack> filter;

    public QuantumArmorUpgradeFilterPacket() {

    }

    public QuantumArmorUpgradeFilterPacket(UpgradeType upgradeType, List<GenericStack> filter) {
        this.upgradeType = upgradeType;
        this.filter = filter;
    }

    public void fromBytes(FriendlyByteBuf stream) {
        upgradeType = stream.readEnum(UpgradeType.class);

        var size = stream.readInt();
        List<GenericStack> list = new ArrayList<>();
        for (var i = 0; i < size; i++) {
            list.add(GenericStack.readBuffer(stream));
        }
        filter = list;
    }

    public void toBytes(FriendlyByteBuf data) {
        data.writeEnum(upgradeType);

        data.writeInt(filter.size());
        for (GenericStack genericStack : filter) {
            GenericStack.writeBuffer(genericStack, data);
        }
    }

    @Override
    public void onMessage(Player serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumArmorConfigMenu menu) {
            menu.openFilterConfigScreen(upgradeType, filter);
        }
    }

    @Override
    public Class<QuantumArmorUpgradeFilterPacket> getPacketClass() {
        return QuantumArmorUpgradeFilterPacket.class;
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
