package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidStack;
import net.pedroksl.advanced_ae.client.gui.ReactionChamberScreen;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

public record FluidTankStackUpdatePacket(FluidStack input, FluidStack output) implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidTankStackUpdatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    FluidStack.OPTIONAL_STREAM_CODEC,
                    FluidTankStackUpdatePacket::input,
                    FluidStack.OPTIONAL_STREAM_CODEC,
                    FluidTankStackUpdatePacket::output,
                    FluidTankStackUpdatePacket::new);

    public static final Type<FluidTankStackUpdatePacket> TYPE =
            CustomAppEngPayload.createType("aae_fluid_tank_stack_update");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof ReactionChamberScreen screen) {
            screen.updateFluidTankContents(input, output);
        }
    }
}
