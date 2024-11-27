package net.pedroksl.advanced_ae.network.packet;

import static appeng.api.stacks.AEKey.writeKey;

import java.util.LinkedHashMap;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.client.gui.AdvPatternEncoderScreen;

import appeng.api.stacks.AEKey;

public class AdvPatternEncoderPacket implements IMessage<AdvPatternEncoderPacket> {

    private LinkedHashMap<AEKey, Direction> dirMap;

    public AdvPatternEncoderPacket() {
        this.dirMap = new LinkedHashMap<>();
    }

    public AdvPatternEncoderPacket(LinkedHashMap<AEKey, Direction> dirMap) {
        this.dirMap = dirMap;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
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

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        dirMap = new LinkedHashMap<>();

        int size = buf.readInt();
        for (var x = 0; x < size; x++) {
            AEKey key = AEKey.readKey(buf);
            Direction dir = buf.readBoolean() ? buf.readEnum(Direction.class) : null;
            dirMap.put(key, dir);
        }
    }

    @Override
    public void onMessage(Player player) {
        if (Minecraft.getInstance().screen instanceof AdvPatternEncoderScreen encoderGui) {
            encoderGui.update(this.dirMap);
        }
    }

    @Override
    public Class<AdvPatternEncoderPacket> getPacketClass() {
        return AdvPatternEncoderPacket.class;
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
