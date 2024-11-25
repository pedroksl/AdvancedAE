package net.pedroksl.advanced_ae.network.packet;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidStack;

public class FluidTankStackUpdatePacket implements IMessage<FluidTankStackUpdatePacket> {
    private FluidStack input;
    private FluidStack output;

    public FluidTankStackUpdatePacket() {}

    public FluidTankStackUpdatePacket(FluidStack input, FluidStack output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        this.input.writeToPacket(buf);
        this.output.writeToPacket(buf);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        this.input = FluidStack.readFromPacket(buf);
        this.output = FluidStack.readFromPacket(buf);
    }

    @Override
    public void onMessage(Player player) {
        //        if (Minecraft.getInstance().screen instanceof ReactionChamberScree screen) {
        //            screen.updateFluidTankContents(input, output);
        //        }
    }

    @Override
    public Class<FluidTankStackUpdatePacket> getPacketClass() {
        return FluidTankStackUpdatePacket.class;
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
