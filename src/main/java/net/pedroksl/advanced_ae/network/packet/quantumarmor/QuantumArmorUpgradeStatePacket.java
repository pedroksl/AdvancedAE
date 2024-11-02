package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.client.gui.QuantumArmorConfigScreen;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

public record QuantumArmorUpgradeStatePacket(int selectedIndex, ItemStack stack) implements ClientboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, QuantumArmorUpgradeStatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    QuantumArmorUpgradeStatePacket::selectedIndex,
                    ItemStack.STREAM_CODEC,
                    QuantumArmorUpgradeStatePacket::stack,
                    QuantumArmorUpgradeStatePacket::new);

    public static final Type<QuantumArmorUpgradeStatePacket> TYPE = CustomAppEngPayload.createType("aae_upgrade_state");

    @Override
    public Type<QuantumArmorUpgradeStatePacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumArmorConfigScreen screen) {
            screen.refreshList(selectedIndex, stack);
        }
    }
}
