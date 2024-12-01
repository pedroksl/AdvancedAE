package net.pedroksl.advanced_ae.network.packet;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class KeysPressedPacket implements IMessage<KeysPressedPacket> {

    private String data;
    private boolean noKey;

    public KeysPressedPacket() {}

    public KeysPressedPacket(String data, boolean noKey) {
        this.data = data;
        this.noKey = noKey;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(data);
        buf.writeBoolean(noKey);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        data = buf.readUtf();
        noKey = buf.readBoolean();
    }

    @Override
    public void onMessage(Player player) {
        player.getPersistentData().putBoolean(data, noKey);
    }

    @Override
    public Class<KeysPressedPacket> getPacketClass() {
        return KeysPressedPacket.class;
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
