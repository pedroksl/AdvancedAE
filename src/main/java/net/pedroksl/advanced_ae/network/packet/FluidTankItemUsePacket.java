package net.pedroksl.advanced_ae.network.packet;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.api.IFluidTankHandler;

public class FluidTankItemUsePacket implements IMessage<FluidTankItemUsePacket> {
    private int index;
    private int button;

    public FluidTankItemUsePacket() {}

    public FluidTankItemUsePacket(int index, int button) {
        this.index = index;
        this.button = button;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.index);
        buf.writeInt(this.button);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        this.index = buf.readInt();
        this.button = buf.readInt();
    }

    @Override
    public void onMessage(Player player) {
        if (player.containerMenu instanceof IFluidTankHandler handler) {
            handler.onItemUse(index, button);
        }
    }

    @Override
    public Class<FluidTankItemUsePacket> getPacketClass() {
        return FluidTankItemUsePacket.class;
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
