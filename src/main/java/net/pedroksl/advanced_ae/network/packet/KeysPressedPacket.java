package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class KeysPressedPacket extends AddonPacket {

    private final String data;
    private final boolean noKey;

    public KeysPressedPacket(FriendlyByteBuf stream) {
        data = stream.readUtf();
        noKey = stream.readBoolean();
    }

    public KeysPressedPacket(String data, boolean noKey) {
        this.data = data;
        this.noKey = noKey;
    }

    @Override
    public void write(FriendlyByteBuf stream) {
        stream.writeUtf(data);
        stream.writeBoolean(noKey);
    }

    @Override
    public void serverPacketData(ServerPlayer player) {
        player.getPersistentData().putBoolean(data, noKey);
    }
}
