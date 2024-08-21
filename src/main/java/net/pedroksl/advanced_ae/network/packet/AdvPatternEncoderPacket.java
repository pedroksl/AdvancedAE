package net.pedroksl.advanced_ae.network.packet;

import static appeng.api.stacks.AEKey.writeKey;

import java.util.HashMap;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.gui.patternencoder.AdvPatternEncoderGui;

import appeng.api.stacks.AEKey;

public class AdvPatternEncoderPacket implements IMessage {

    private HashMap<AEKey, Direction> dirMap;

    public AdvPatternEncoderPacket() {
        this.dirMap = new HashMap<>();
    }

    public AdvPatternEncoderPacket(HashMap<AEKey, Direction> dirMap) {
        this.dirMap = dirMap;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
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
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        dirMap = new HashMap<>();

        int size = buf.readInt();
        for (var x = 0; x < size; x++) {
            AEKey key = AEKey.readKey(buf);
            Direction dir = buf.readBoolean() ? buf.readEnum(Direction.class) : null;
            dirMap.put(key, dir);
        }
    }

    @Override
    public void onMessage(Player player) {
        if (Minecraft.getInstance().screen instanceof AdvPatternEncoderGui encoderGui) {
            encoderGui.update(this.dirMap);
        }
    }

    @Override
    public ResourceLocation id() {
        return AdvancedAE.id("pattern_encoder_update");
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
