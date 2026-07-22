package net.pedroksl.advanced_ae.network.packet;

import static appeng.api.stacks.AEKey.writeKey;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.client.gui.AdvPatternEncoderScreen;

import appeng.api.stacks.AEKey;
import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

public record AdvPatternEncoderPacket(
        LinkedHashMap<AEKey, Direction> dirMap, LinkedHashMap<AEKey, List<Integer>> slotMap)
        implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvPatternEncoderPacket> STREAM_CODEC =
            StreamCodec.ofMember(AdvPatternEncoderPacket::write, AdvPatternEncoderPacket::decode);

    public static final Type<AdvPatternEncoderPacket> TYPE = CustomAppEngPayload.createType("pattern_encoder_update");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(dirMap.size());
        for (var entry : dirMap.entrySet()) {
            writeKey(buf, entry.getKey());
            Direction dir = entry.getValue();
            if (dir == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeEnum(entry.getValue());
            }
            List<Integer> slots = slotMap.getOrDefault(entry.getKey(), List.of());
            buf.writeInt(slots.size());
            for (var slot : slots) {
                buf.writeInt(slot == null ? -1 : slot);
            }
        }
    }

    public static AdvPatternEncoderPacket decode(RegistryFriendlyByteBuf buf) {
        var dirMap = new LinkedHashMap<AEKey, Direction>();
        var slotMap = new LinkedHashMap<AEKey, List<Integer>>();

        int size = buf.readInt();
        for (var x = 0; x < size; x++) {
            AEKey key = AEKey.readKey(buf);
            Direction dir = buf.readBoolean() ? buf.readEnum(Direction.class) : null;
            dirMap.put(key, dir);

            int slotCount = buf.readInt();
            var slots = new ArrayList<Integer>(slotCount);
            for (var y = 0; y < slotCount; y++) {
                slots.add(buf.readInt());
            }
            slotMap.put(key, slots);
        }

        return new AdvPatternEncoderPacket(dirMap, slotMap);
    }

    @Override
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof AdvPatternEncoderScreen encoderGui) {
            encoderGui.update(this.dirMap, this.slotMap);
        }
    }
}
