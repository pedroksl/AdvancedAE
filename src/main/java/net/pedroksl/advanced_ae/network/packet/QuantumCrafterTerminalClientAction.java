package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.gui.QuantumCrafterTermMenu;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class QuantumCrafterTerminalClientAction extends AddonPacket {

    private final boolean isConfigAction;
    private final long serverId;
    private final int slot;

    public QuantumCrafterTerminalClientAction(FriendlyByteBuf stream) {
        isConfigAction = stream.readBoolean();
        serverId = stream.readLong();
        slot = stream.readInt();
    }

    public QuantumCrafterTerminalClientAction(boolean isConfigAction, long serverId, int slot) {
        this.isConfigAction = isConfigAction;
        this.serverId = serverId;
        this.slot = slot;
    }

    @Override
    protected void write(FriendlyByteBuf stream) {
        stream.writeBoolean(isConfigAction);
        stream.writeLong(serverId);
        stream.writeInt(slot);
    }

    @Override
    public void serverPacketData(ServerPlayer player) {
        if (player.containerMenu instanceof QuantumCrafterTermMenu menu) {
            if (isConfigAction) {
                menu.configPattern(serverId, slot);
            } else {
                menu.toggleEnabledPattern(serverId, slot);
            }
        }
    }
}
