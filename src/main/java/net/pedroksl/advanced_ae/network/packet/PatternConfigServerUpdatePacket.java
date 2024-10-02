package net.pedroksl.advanced_ae.network.packet;

import static appeng.api.stacks.AEKey.readKey;
import static appeng.api.stacks.AEKey.writeKey;

import java.util.LinkedHashMap;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.client.gui.QuantumCrafterConfigPatternScreen;

import appeng.api.stacks.AEKey;
import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

public record PatternConfigServerUpdatePacket(LinkedHashMap<AEKey, Long> inputs, Pair<AEKey, Long> output)
        implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, PatternConfigServerUpdatePacket> STREAM_CODEC =
            StreamCodec.ofMember(PatternConfigServerUpdatePacket::write, PatternConfigServerUpdatePacket::decode);

    public static final Type<PatternConfigServerUpdatePacket> TYPE =
            CustomAppEngPayload.createType("aae_crafter_pattern_update");

    @Override
    public Type<PatternConfigServerUpdatePacket> type() {
        return TYPE;
    }

    @SuppressWarnings("unchecked")
    public PatternConfigServerUpdatePacket(LinkedHashMap<AEKey, Long> inputs, Pair<AEKey, Long> output) {
        this.inputs = (LinkedHashMap<AEKey, Long>) inputs.clone();
        this.output = new Pair<>(output.getFirst(), output.getSecond());
    }

    public static PatternConfigServerUpdatePacket decode(RegistryFriendlyByteBuf stream) {
        var inputs = new LinkedHashMap<AEKey, Long>();

        var size = stream.readInt();
        for (var x = 0; x < size; x++) {
            AEKey key = readKey(stream);
            inputs.put(key, stream.readLong());
        }

        AEKey key = readKey(stream);
        var output = new Pair<>(key, stream.readLong());

        return new PatternConfigServerUpdatePacket(inputs, output);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeInt(this.inputs.size());
        for (var entry : this.inputs.entrySet()) {
            writeKey(data, entry.getKey());
            data.writeLong(entry.getValue());
        }

        writeKey(data, this.output.getFirst());
        data.writeLong(this.output.getSecond());
    }

    @Override
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterConfigPatternScreen screen) {
            screen.update(this.inputs, this.output);
        }
    }
}
