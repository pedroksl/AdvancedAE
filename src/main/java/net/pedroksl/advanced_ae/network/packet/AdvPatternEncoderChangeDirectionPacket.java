package net.pedroksl.advanced_ae.network.packet;

import static appeng.api.stacks.AEKey.writeKey;

import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.gui.AdvPatternEncoderMenu;

import appeng.api.stacks.AEKey;
import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

public record AdvPatternEncoderChangeDirectionPacket(AEKey key, Direction dir) implements ServerboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvPatternEncoderChangeDirectionPacket> STREAM_CODEC =
            StreamCodec.ofMember(
                    AdvPatternEncoderChangeDirectionPacket::write, AdvPatternEncoderChangeDirectionPacket::decode);

    public static final Type<AdvPatternEncoderChangeDirectionPacket> TYPE =
            CustomAppEngPayload.createType("encoder_change_direction_update");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        writeKey(buf, this.key);
        if (this.dir == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeEnum(this.dir);
        }
    }

    public static AdvPatternEncoderChangeDirectionPacket decode(RegistryFriendlyByteBuf buf) {
        var key = AEKey.readKey(buf);
        var dir = buf.readBoolean() ? buf.readEnum(Direction.class) : null;

        return new AdvPatternEncoderChangeDirectionPacket(key, dir);
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        if (player.containerMenu instanceof AdvPatternEncoderMenu encoderContainer) {
            encoderContainer.update(this.key, this.dir);
        }
    }
}
