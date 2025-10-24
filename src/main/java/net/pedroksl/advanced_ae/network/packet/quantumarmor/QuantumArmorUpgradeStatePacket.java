package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.client.gui.QuantumArmorConfigScreen;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class QuantumArmorUpgradeStatePacket extends AddonPacket {

    private final int selectedIndex;
    private final ItemStack stack;

    public QuantumArmorUpgradeStatePacket(FriendlyByteBuf stream) {
        selectedIndex = stream.readInt();

        stack = stream.readJsonWithCodec(ItemStack.CODEC);
    }

    public QuantumArmorUpgradeStatePacket(int selectedIndex, ItemStack stack) {
        this.selectedIndex = selectedIndex;
        this.stack = stack;
    }

    @Override
    public void write(FriendlyByteBuf stream) {
        stream.writeInt(selectedIndex);

        stream.writeJsonWithCodec(ItemStack.CODEC, stack);
    }

    @Override
    public void clientPacketData(Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumArmorConfigScreen screen) {
            screen.refreshList(selectedIndex, stack);
        }
    }
}
