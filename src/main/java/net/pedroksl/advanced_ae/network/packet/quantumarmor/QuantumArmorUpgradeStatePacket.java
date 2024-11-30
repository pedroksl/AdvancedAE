package net.pedroksl.advanced_ae.network.packet.quantumarmor;

import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.client.gui.QuantumArmorConfigScreen;

public class QuantumArmorUpgradeStatePacket implements IMessage<QuantumArmorUpgradeStatePacket> {

    private int selectedIndex;
    private ItemStack stack;

    public QuantumArmorUpgradeStatePacket() {

    }

    public QuantumArmorUpgradeStatePacket(int selectedIndex, ItemStack stack) {
        this.selectedIndex = selectedIndex;
        this.stack = stack;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(selectedIndex);

        buf.writeJsonWithCodec(ItemStack.CODEC, stack);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        selectedIndex = buf.readInt();

        stack = buf.readJsonWithCodec(ItemStack.CODEC);
    }

    @Override
    public void onMessage(Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumArmorConfigScreen screen) {
            screen.refreshList(selectedIndex, stack);
        }
    }

    @Override
    public Class<QuantumArmorUpgradeStatePacket> getPacketClass() {
        return QuantumArmorUpgradeStatePacket.class;
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
