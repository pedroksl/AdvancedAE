package net.pedroksl.advanced_ae.network.packet;

import static appeng.api.stacks.AEKey.writeKey;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.gui.AdvPatternEncoderMenu;

import appeng.api.stacks.AEKey;
import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

/**
 * Sent by the "Slots" mode text field in the Advanced Pattern Encoder screen: replaces the whole target slot
 * list for one input key at once (the player types a comma-separated list like "0,1" instead of editing one
 * occurrence at a time).
 */
public record AdvPatternEncoderChangeSlotListPacket(AEKey key, List<Integer> slots) implements ServerboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvPatternEncoderChangeSlotListPacket> STREAM_CODEC =
            StreamCodec.ofMember(
                    AdvPatternEncoderChangeSlotListPacket::write, AdvPatternEncoderChangeSlotListPacket::decode);

    public static final Type<AdvPatternEncoderChangeSlotListPacket> TYPE =
            CustomAppEngPayload.createType("encoder_change_slot_list_update");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        writeKey(buf, this.key);
        buf.writeInt(slots.size());
        for (var slot : slots) {
            buf.writeInt(slot);
        }
    }

    public static AdvPatternEncoderChangeSlotListPacket decode(RegistryFriendlyByteBuf buf) {
        var key = AEKey.readKey(buf);
        int size = buf.readInt();
        var slots = new ArrayList<Integer>(size);
        for (var i = 0; i < size; i++) {
            slots.add(buf.readInt());
        }
        return new AdvPatternEncoderChangeSlotListPacket(key, slots);
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        if (player.containerMenu instanceof AdvPatternEncoderMenu encoderContainer) {
            encoderContainer.updateSlotList(this.key, this.slots);
        }
    }
}
