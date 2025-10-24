package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.gui.QuantumCrafterConfigPatternMenu;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class SetStockAmountPacket extends AddonPacket {
    private final int index;
    private final long amount;

    public SetStockAmountPacket(FriendlyByteBuf stream) {
        index = stream.readInt();
        amount = stream.readLong();
    }

    public SetStockAmountPacket(int index, long amount) {
        this.index = index;
        this.amount = amount;
    }

    @Override
    public void write(FriendlyByteBuf stream) {
        stream.writeInt(this.index);
        stream.writeLong(this.amount);
    }

    @Override
    public void serverPacketData(ServerPlayer player) {
        if (player.containerMenu instanceof QuantumCrafterConfigPatternMenu menu) {
            menu.setStockAmount(this.index, this.amount);
        }
    }
}
