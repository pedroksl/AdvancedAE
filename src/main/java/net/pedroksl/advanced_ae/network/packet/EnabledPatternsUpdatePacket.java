package net.pedroksl.advanced_ae.network.packet;

import java.util.ArrayList;
import java.util.List;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.client.gui.QuantumCrafterScreen;

public class EnabledPatternsUpdatePacket implements IMessage<EnabledPatternsUpdatePacket> {
    private List<Boolean> enabledPatterns;

    public EnabledPatternsUpdatePacket() {}

    public EnabledPatternsUpdatePacket(List<Boolean> enabledPatterns) {
        this.enabledPatterns = enabledPatterns;
    }

    @Override
    public void fromBytes(FriendlyByteBuf stream) {
        List<Boolean> list = new ArrayList<>();

        var size = stream.readInt();
        for (var x = 0; x < size; x++) {
            list.add(stream.readBoolean());
        }
        this.enabledPatterns = list;
    }

    @Override
    public void toBytes(FriendlyByteBuf data) {
        data.writeInt(this.enabledPatterns.size());
        for (var entry : this.enabledPatterns) {
            data.writeBoolean(entry);
        }
    }

    @Override
    public void onMessage(Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterScreen screen) {
            screen.updateEnabledButtons(this.enabledPatterns);
        }
    }

    @Override
    public Class<EnabledPatternsUpdatePacket> getPacketClass() {
        return EnabledPatternsUpdatePacket.class;
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
