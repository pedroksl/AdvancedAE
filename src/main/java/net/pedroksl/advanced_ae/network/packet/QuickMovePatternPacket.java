package net.pedroksl.advanced_ae.network.packet;

import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.gui.QuantumCrafterTermMenu;

import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

public record QuickMovePatternPacket(int containerId, int clickedSlot, List<Long> allowedPatternContainers)
        implements ServerboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, QuickMovePatternPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    QuickMovePatternPacket::containerId,
                    ByteBufCodecs.VAR_INT,
                    QuickMovePatternPacket::clickedSlot,
                    ByteBufCodecs.VAR_LONG.apply(ByteBufCodecs.list()),
                    QuickMovePatternPacket::allowedPatternContainers,
                    QuickMovePatternPacket::new);
    public static final Type<QuickMovePatternPacket> TYPE = CustomAppEngPayload.createType("aae_quick_move_pattern");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        if (player.containerMenu.containerId == containerId
                && player.containerMenu instanceof QuantumCrafterTermMenu menu) {
            menu.quickMovePattern(player, clickedSlot, allowedPatternContainers);
        }
    }
}
