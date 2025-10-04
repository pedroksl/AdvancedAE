package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.gui.QuantumCrafterTermMenu;

import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

public record QuantumCrafterTerminalClientAction(boolean isConfigAction, long serverId, int slot)
        implements ServerboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, QuantumCrafterTerminalClientAction> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    QuantumCrafterTerminalClientAction::isConfigAction,
                    ByteBufCodecs.VAR_LONG,
                    QuantumCrafterTerminalClientAction::serverId,
                    ByteBufCodecs.INT,
                    QuantumCrafterTerminalClientAction::slot,
                    QuantumCrafterTerminalClientAction::new);

    public static final Type<QuantumCrafterTerminalClientAction> TYPE =
            CustomAppEngPayload.createType("aae_quantum_crafter_terminal_client_action");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        if (player.containerMenu instanceof QuantumCrafterTermMenu menu) {
            if (isConfigAction) {
                menu.configPattern(serverId, slot);
            } else {
                menu.toggleEnabledPattern(serverId, slot);
            }
        }
    }
}
