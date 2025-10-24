package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.gui.QuantumArmorConfigMenu;
import net.pedroksl.ae2addonlib.network.AddonPacket;

import appeng.api.stacks.GenericStack;

public class QuantumArmorUpgradeFilterPacket extends AddonPacket {

    private final UpgradeType upgradeType;
    private final List<GenericStack> filter;

    public QuantumArmorUpgradeFilterPacket(FriendlyByteBuf stream) {
        upgradeType = stream.readEnum(UpgradeType.class);

        var size = stream.readInt();
        List<GenericStack> list = new ArrayList<>();
        for (var i = 0; i < size; i++) {
            list.add(GenericStack.readBuffer(stream));
        }
        filter = list;
    }

    public QuantumArmorUpgradeFilterPacket(UpgradeType upgradeType, List<GenericStack> filter) {
        this.upgradeType = upgradeType;
        this.filter = filter;
    }

    public void write(FriendlyByteBuf stream) {
        stream.writeEnum(upgradeType);

        stream.writeInt(filter.size());
        for (GenericStack genericStack : filter) {
            GenericStack.writeBuffer(genericStack, stream);
        }
    }

    @Override
    public void serverPacketData(ServerPlayer serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumArmorConfigMenu menu) {
            menu.openFilterConfigScreen(upgradeType, filter);
        }
    }
}
