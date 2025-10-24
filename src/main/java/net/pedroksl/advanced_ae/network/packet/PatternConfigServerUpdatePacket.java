package net.pedroksl.advanced_ae.network.packet;

import static appeng.api.stacks.AEKey.readKey;
import static appeng.api.stacks.AEKey.writeKey;

import java.util.LinkedHashMap;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.client.gui.QuantumCrafterConfigPatternScreen;
import net.pedroksl.ae2addonlib.network.AddonPacket;

import appeng.api.stacks.AEKey;

public class PatternConfigServerUpdatePacket extends AddonPacket {
    private final LinkedHashMap<AEKey, Long> inputs;
    private final Pair<AEKey, Long> output;

    public PatternConfigServerUpdatePacket(FriendlyByteBuf stream) {
        var inputs = new LinkedHashMap<AEKey, Long>();

        var size = stream.readInt();
        for (var x = 0; x < size; x++) {
            AEKey key = readKey(stream);
            inputs.put(key, stream.readLong());
        }
        this.inputs = inputs;

        AEKey key = readKey(stream);
        this.output = new Pair<>(key, stream.readLong());
    }

    public PatternConfigServerUpdatePacket(LinkedHashMap<AEKey, Long> inputs, Pair<AEKey, Long> output) {
        this.inputs = inputs;
        this.output = output;
    }

    @Override
    public void write(FriendlyByteBuf stream) {
        stream.writeInt(this.inputs.size());
        for (var entry : this.inputs.entrySet()) {
            writeKey(stream, entry.getKey());
            stream.writeLong(entry.getValue());
        }

        writeKey(stream, this.output.getFirst());
        stream.writeLong(this.output.getSecond());
    }

    @Override
    public void clientPacketData(Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterConfigPatternScreen screen) {
            screen.update(this.inputs, this.output);
        }
    }
}
