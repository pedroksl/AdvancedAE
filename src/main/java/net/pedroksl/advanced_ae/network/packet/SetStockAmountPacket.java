package net.pedroksl.advanced_ae.network.packet;

import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.gui.QuantumCrafterConfigPatternMenu;

public class SetStockAmountPacket implements IMessage<SetStockAmountPacket> {
    private int index;
    private long amount;

    public SetStockAmountPacket() {

    }

    public SetStockAmountPacket(int index, long amount) {
        this.index = index;
        this.amount = amount;
    }

    @Override
    public void toBytes(FriendlyByteBuf data) {
        data.writeInt(this.index);
        data.writeLong(this.amount);
    }

    @Override
    public void fromBytes(FriendlyByteBuf stream) {
        var index = stream.readInt();
        var amount = stream.readLong();
    }

    @Override
    public void onMessage(Player player) {
        if (player.containerMenu instanceof QuantumCrafterConfigPatternMenu menu) {
            menu.setStockAmount(this.index, this.amount);
        }
    }

    @Override
    public Class<SetStockAmountPacket> getPacketClass() {
        return SetStockAmountPacket.class;
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
