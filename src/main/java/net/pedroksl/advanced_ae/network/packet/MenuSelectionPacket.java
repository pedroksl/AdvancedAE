package net.pedroksl.advanced_ae.network.packet;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class MenuSelectionPacket implements IMessage<MenuSelectionPacket> {

    private String data;
    private int menuType;

    public MenuSelectionPacket() {}

    public MenuSelectionPacket(String data, int menuType) {
        this.data = data;
        this.menuType = menuType;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(data);
        buf.writeInt(menuType);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        data = buf.readUtf();
        menuType = buf.readInt();
    }

    @Override
    public void onMessage(Player player) {
        if (menuType == -1) {
            player.getPersistentData().remove(data);
        } else {
            player.getPersistentData().putInt(data, menuType);
        }
    }

    @Override
    public Class<MenuSelectionPacket> getPacketClass() {
        return MenuSelectionPacket.class;
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
