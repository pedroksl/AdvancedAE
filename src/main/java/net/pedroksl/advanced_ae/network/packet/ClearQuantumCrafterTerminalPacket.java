package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.client.gui.QuantumCrafterTermScreen;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class ClearQuantumCrafterTerminalPacket extends AddonPacket {

    public ClearQuantumCrafterTerminalPacket(FriendlyByteBuf stream) {}

    public ClearQuantumCrafterTerminalPacket() {}

    public void write(FriendlyByteBuf data) {}

    @Override
    public void clientPacketData(Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterTermScreen<?> screen) {
            screen.clear();
        }
    }
}
