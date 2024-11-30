package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import appeng.api.stacks.GenericStack;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.gui.QuantumArmorConfigMenu;

import java.util.ArrayList;
import java.util.List;

public class QuantumArmorMagnetPacket implements IMessage<QuantumArmorMagnetPacket> {

    private int currentValue;
    private List<GenericStack> filter;
    private boolean blacklist;

    public QuantumArmorMagnetPacket() {

    }

    public QuantumArmorMagnetPacket(int currentValue, List<GenericStack> filter, boolean blacklist) {
        this.currentValue = currentValue;
        this.filter = filter;
        this.blacklist = blacklist;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(currentValue);

        buf.writeInt(filter.size());
	    for (GenericStack genericStack : filter) {
		    GenericStack.writeBuffer(genericStack, buf);
	    }

        buf.writeBoolean(blacklist);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        currentValue = buf.readInt();

        var size = buf.readInt();
        List<GenericStack> list = new ArrayList<>();
        for (var i = 0; i < size; i++) {
            list.add(GenericStack.readBuffer(buf));
        }
        filter = list;

        blacklist = buf.readBoolean();
    }

    @Override
    public void onMessage(Player serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumArmorConfigMenu menu) {
            menu.openMagnetScreen(currentValue, filter, blacklist);
        }
    }

    @Override
    public Class<QuantumArmorMagnetPacket> getPacketClass() {
        return QuantumArmorMagnetPacket.class;
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
