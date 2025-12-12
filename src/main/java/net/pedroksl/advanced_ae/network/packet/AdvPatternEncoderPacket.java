package net.pedroksl.advanced_ae.network.packet;

import static appeng.api.stacks.AEKey.writeKey;

import java.util.LinkedHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.pedroksl.advanced_ae.client.gui.AdvPatternEncoderScreen;

import appeng.api.stacks.AEKey;
import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

public record AdvPatternEncoderPacket(LinkedHashMap<AEKey, Direction> dirMap) implements ClientboundPacket {

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
        }
    }

    public static AdvPatternEncoderPacket decode(RegistryFriendlyByteBuf buf) {
        var dirMap = new LinkedHashMap<AEKey, Direction>();

        int size = buf.readInt();
        for (var x = 0; x < size; x++) {
            AEKey key = AEKey.readKey(buf);
            Direction dir = buf.readBoolean() ? buf.readEnum(Direction.class) : null;
            dirMap.put(key, dir);
        }

        return new AdvPatternEncoderPacket(dirMap);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof AdvPatternEncoderScreen encoderGui) {
            encoderGui.update(this.dirMap);
        }
    }
}
