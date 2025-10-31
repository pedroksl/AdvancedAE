package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.client.gui.QuantumArmorConfigScreen;
import net.pedroksl.advanced_ae.client.widgets.UpgradeState;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

public record QuantumArmorUpgradeStatePacket(int selectedIndex, List<UpgradeState> states)
        implements ClientboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, QuantumArmorUpgradeStatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    QuantumArmorUpgradeStatePacket::selectedIndex,
                    UpgradeState.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    QuantumArmorUpgradeStatePacket::states,
                    QuantumArmorUpgradeStatePacket::new);

    public static final Type<QuantumArmorUpgradeStatePacket> TYPE = CustomAppEngPayload.createType("aae_upgrade_state");

    @Override
    public Type<QuantumArmorUpgradeStatePacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumArmorConfigScreen screen) {
            screen.refreshList(selectedIndex, states);
        }
    }
}
