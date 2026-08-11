package net.pedroksl.advanced_ae.network.packet;

import static appeng.api.stacks.AEKey.writeKey;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.gui.AdvPatternEncoderMenu;

import appeng.api.stacks.AEKey;
import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

/**
 * Sent by the Advanced Pattern Encoder screen when the player picks a target inventory slot for one
 * OCCURRENCE of one of the pattern's inputs (a repeated ingredient like two separate deepslate rows can
 * target two different slots, hence {@code occurrence}: 0 = first row of that item, 1 = second row, etc).
 * Mirrors {@link AdvPatternEncoderChangeDirectionPacket}; {@code slot < 0} means "no specific slot / auto".
 */
public record AdvPatternEncoderChangeSlotPacket(AEKey key, int occurrence, int slot) implements ServerboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvPatternEncoderChangeSlotPacket> STREAM_CODEC =
            StreamCodec.ofMember(AdvPatternEncoderChangeSlotPacket::write, AdvPatternEncoderChangeSlotPacket::decode);

    public static final Type<AdvPatternEncoderChangeSlotPacket> TYPE =
            CustomAppEngPayload.createType("encoder_change_slot_update");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        writeKey(buf, this.key);
        buf.writeInt(this.occurrence);
        buf.writeInt(this.slot);
    }

    public static AdvPatternEncoderChangeSlotPacket decode(RegistryFriendlyByteBuf buf) {
        var key = AEKey.readKey(buf);
        var occurrence = buf.readInt();
        var slot = buf.readInt();

        return new AdvPatternEncoderChangeSlotPacket(key, occurrence, slot);
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        if (player.containerMenu instanceof AdvPatternEncoderMenu encoderContainer) {
            encoderContainer.updateSlot(this.key, this.occurrence, this.slot);
        }
    }
}
