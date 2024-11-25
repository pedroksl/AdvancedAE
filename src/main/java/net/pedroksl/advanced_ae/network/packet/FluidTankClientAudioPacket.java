package net.pedroksl.advanced_ae.network.packet;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.api.IFluidTankScreen;

public class FluidTankClientAudioPacket implements IMessage<FluidTankClientAudioPacket> {
    private boolean isInsert;

    public FluidTankClientAudioPacket() {}

    public FluidTankClientAudioPacket(boolean isInsert) {
        this.isInsert = isInsert;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.isInsert);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        this.isInsert = buf.readBoolean();
    }

    @Override
    public void onMessage(Player player) {
        if (Minecraft.getInstance().screen instanceof IFluidTankScreen screen) {
            screen.playSoundFeedback(isInsert);
        }
    }

    @Override
    public Class<FluidTankClientAudioPacket> getPacketClass() {
        return FluidTankClientAudioPacket.class;
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
