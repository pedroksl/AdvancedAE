package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.gui.QuantumArmorStyleConfigMenu;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class QuantumArmorStylePacket extends AddonPacket {

    private final List<Integer> slots;
    private final int color;

    public QuantumArmorStylePacket(FriendlyByteBuf stream) {
        var size = stream.readInt();
        List<Integer> list = new ArrayList<>();
        for (var i = 0; i < size; i++) {
            list.add(stream.readInt());
        }
        slots = list;

        color = stream.readInt();
    }

    public QuantumArmorStylePacket(List<Integer> slots, int color) {
        this.slots = slots;
        this.color = color;
    }

    @Override
    public void write(FriendlyByteBuf stream) {
        stream.writeInt(slots.size());
        for (var slotIndex : slots) {
            stream.writeInt(slotIndex);
        }

        stream.writeInt(color);
    }

    @Override
    public void serverPacketData(ServerPlayer serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumArmorStyleConfigMenu menu) {
            menu.updateItemColors(this.slots, this.color);
        }
    }
}
