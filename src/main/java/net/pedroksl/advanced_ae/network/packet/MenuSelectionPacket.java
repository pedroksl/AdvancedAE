package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class MenuSelectionPacket extends AddonPacket {

    private final String data;
    private final int menuType;

    public MenuSelectionPacket(FriendlyByteBuf stream) {
        data = stream.readUtf();
        menuType = stream.readInt();
    }

    public MenuSelectionPacket(String data, int menuType) {
        this.data = data;
        this.menuType = menuType;
    }

    @Override
    public void write(FriendlyByteBuf stream) {
        stream.writeUtf(data);
        stream.writeInt(menuType);
    }

    @Override
    public void clientPacketData(Player player) {
        if (menuType == -1) {
            player.getPersistentData().remove(data);
        } else {
            player.getPersistentData().putInt(data, menuType);
        }
    }
}
