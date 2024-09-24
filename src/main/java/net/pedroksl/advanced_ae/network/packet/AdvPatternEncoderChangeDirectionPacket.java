package net.pedroksl.advanced_ae.network.packet;

import static appeng.api.stacks.AEKey.writeKey;

import javax.annotation.Nullable;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.gui.AdvPatternEncoderMenu;

import appeng.api.stacks.AEKey;

public class AdvPatternEncoderChangeDirectionPacket implements IMessage {

    private AEKey key;
    private Direction dir;

    public AdvPatternEncoderChangeDirectionPacket() {}

    public AdvPatternEncoderChangeDirectionPacket(AEKey key, @Nullable Direction dir) {
        this.key = key;
        this.dir = dir;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        writeKey(buf, this.key);
        if (this.dir == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeEnum(this.dir);
        }
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        this.key = AEKey.readKey(buf);
        this.dir = buf.readBoolean() ? buf.readEnum(Direction.class) : null;
    }

    @Override
    public void onMessage(Player player) {
        if (player.containerMenu instanceof AdvPatternEncoderMenu encoderContainer) {
            encoderContainer.update(this.key, this.dir);
        }
    }

    @Override
    public ResourceLocation id() {
        return AdvancedAE.makeId("encoder_change_direction_update");
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
