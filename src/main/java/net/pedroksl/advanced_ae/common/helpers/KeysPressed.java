package net.pedroksl.advanced_ae.common.helpers;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class KeysPressed {

    public static final StreamCodec<RegistryFriendlyByteBuf, KeysPressed> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.BYTE, KeysPressed::toByte, KeysPressed::new);

    public boolean noKey;
    public boolean upKey;
    public boolean downKey;

    public static String KEYS_PRESSED = "aae$keys_pressed";

    public KeysPressed(boolean noKey, boolean upKey, boolean downKey) {
        this.noKey = noKey;
        this.upKey = upKey;
        this.downKey = downKey;
    }

    public KeysPressed(byte keyPresses) {
        this.noKey = (keyPresses & (1 << 2)) != 0;
        this.upKey = (keyPresses & (1 << 1)) != 0;
        this.downKey = (keyPresses & 1) != 0;
    }

    public byte toByte() {
        return (byte) ((noKey ? 1 << 2 : 0) | (upKey ? 1 << 1 : 0) | (downKey ? 1 : 0));
    }
}
