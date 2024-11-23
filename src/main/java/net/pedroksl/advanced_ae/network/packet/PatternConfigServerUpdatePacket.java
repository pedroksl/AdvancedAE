package net.pedroksl.advanced_ae.network.packet;

import appeng.api.stacks.AEKey;
import com.glodblock.github.glodium.network.packet.IMessage;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.client.QuantumCrafterConfigPatternScreen;

import java.util.LinkedHashMap;

import static appeng.api.stacks.AEKey.readKey;
import static appeng.api.stacks.AEKey.writeKey;

public class PatternConfigServerUpdatePacket implements IMessage<PatternConfigServerUpdatePacket> {
    private LinkedHashMap<AEKey, Long> inputs;
    private Pair<AEKey, Long> output;

    public PatternConfigServerUpdatePacket() {
    }

    public PatternConfigServerUpdatePacket(LinkedHashMap<AEKey, Long> inputs, Pair<AEKey, Long> output) {
        this.inputs = inputs;
        this.output = output;
    }

    @Override
    public void toBytes(FriendlyByteBuf data) {
        data.writeInt(this.inputs.size());
        for (var entry : this.inputs.entrySet()) {
            writeKey(data, entry.getKey());
            data.writeLong(entry.getValue());
        }

        writeKey(data, this.output.getFirst());
        data.writeLong(this.output.getSecond());
    }

    @Override
    public void fromBytes(FriendlyByteBuf stream) {
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

    @Override
    public void onMessage(Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterConfigPatternScreen screen) {
            screen.update(this.inputs, this.output);
        }
    }

    @Override
    public Class<PatternConfigServerUpdatePacket> getPacketClass() {
        return PatternConfigServerUpdatePacket.class;
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
