package net.pedroksl.advanced_ae.network.packet;

import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.pedroksl.advanced_ae.client.gui.OutputDirectionScreen;

import appeng.api.orientation.RelativeSide;
import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

public record OutputDirectionClientUpdatePacket(Set<RelativeSide> sides) implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, OutputDirectionClientUpdatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    NeoForgeStreamCodecs.enumCodec(RelativeSide.class)
                            .apply(ByteBufCodecs.list())
                            .map(Set::copyOf, List::copyOf),
                    OutputDirectionClientUpdatePacket::sides,
                    OutputDirectionClientUpdatePacket::new);

    public static final Type<OutputDirectionClientUpdatePacket> TYPE =
            CustomAppEngPayload.createType("output_direction_update_client");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof OutputDirectionScreen screen) {
            screen.update(this.sides);
        }
    }
}
