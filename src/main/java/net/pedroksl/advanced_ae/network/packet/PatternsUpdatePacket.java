package net.pedroksl.advanced_ae.network.packet;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.client.gui.QuantumCrafterScreen;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class PatternsUpdatePacket extends AddonPacket {
    private final List<Boolean> invalidPatterns;
    private final List<Boolean> enabledPatterns;

    public PatternsUpdatePacket(FriendlyByteBuf stream) {
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

    public PatternsUpdatePacket(List<Boolean> invalidPatterns, List<Boolean> enabledPatterns) {
        this.invalidPatterns = invalidPatterns;
        this.enabledPatterns = enabledPatterns;
    }

    @Override
    public void write(FriendlyByteBuf stream) {
        stream.writeInt(this.invalidPatterns.size());
        for (var entry : this.invalidPatterns) {
            stream.writeBoolean(entry);
        }

        stream.writeInt(this.enabledPatterns.size());
        for (var entry : this.enabledPatterns) {
            stream.writeBoolean(entry);
        }
    }

    @Override
    public void clientPacketData(Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterScreen screen) {
            screen.updateInvalidButtons(this.invalidPatterns);
            screen.updateEnabledButtons(this.enabledPatterns);
        }
    }
}
