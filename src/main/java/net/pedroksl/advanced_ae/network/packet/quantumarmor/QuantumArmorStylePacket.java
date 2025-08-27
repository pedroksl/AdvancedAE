package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import java.util.ArrayList;
import java.util.List;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.gui.QuantumArmorStyleConfigMenu;

public class QuantumArmorStylePacket implements IMessage<QuantumArmorStylePacket> {

    private List<Integer> slots;
    private int color;

    public QuantumArmorStylePacket() {}

    public QuantumArmorStylePacket(List<Integer> slots, int color) {
        this.slots = slots;
        this.color = color;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(slots.size());
        for (var slotIndex : slots) {
            buf.writeInt(slotIndex);
        }

        buf.writeInt(color);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        var size = buf.readInt();
        List<Integer> list = new ArrayList<>();
        for (var i = 0; i < size; i++) {
            list.add(buf.readInt());
        }
        slots = list;

        color = buf.readInt();
    }

    @Override
    public void onMessage(Player serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumArmorStyleConfigMenu menu) {
            menu.updateItemColors(this.slots, this.color);
        }
    }

    @Override
    public Class<QuantumArmorStylePacket> getPacketClass() {
        return QuantumArmorStylePacket.class;
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
