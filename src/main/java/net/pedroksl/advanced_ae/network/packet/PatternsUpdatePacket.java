package net.pedroksl.advanced_ae.network.packet;

import java.util.ArrayList;
import java.util.List;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.client.gui.QuantumCrafterScreen;

public class PatternsUpdatePacket implements IMessage<PatternsUpdatePacket> {
    private List<Boolean> invalidPatterns;
    private List<Boolean> enabledPatterns;

    public PatternsUpdatePacket() {}

    public PatternsUpdatePacket(List<Boolean> invalidPatterns, List<Boolean> enabledPatterns) {
        this.invalidPatterns = invalidPatterns;
        this.enabledPatterns = enabledPatterns;
    }

    @Override
    public void fromBytes(FriendlyByteBuf stream) {
        List<Boolean> invalidList = new ArrayList<>();
        List<Boolean> enabledList = new ArrayList<>();

        var size = stream.readInt();
        for (var x = 0; x < size; x++) {
            invalidList.add(stream.readBoolean());
        }

        size = stream.readInt();
        for (var x = 0; x < size; x++) {
            enabledList.add(stream.readBoolean());
        }

        this.invalidPatterns = invalidList;
        this.enabledPatterns = enabledList;
    }

    @Override
    public void toBytes(FriendlyByteBuf data) {
        data.writeInt(this.invalidPatterns.size());
        for (var entry : this.invalidPatterns) {
            data.writeBoolean(entry);
        }

        data.writeInt(this.enabledPatterns.size());
        for (var entry : this.enabledPatterns) {
            data.writeBoolean(entry);
        }
    }

    @Override
    public void onMessage(Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterScreen screen) {
            screen.updateInvalidButtons(this.invalidPatterns);
            screen.updateEnabledButtons(this.enabledPatterns);
        }
    }

    @Override
    public Class<PatternsUpdatePacket> getPacketClass() {
        return PatternsUpdatePacket.class;
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
