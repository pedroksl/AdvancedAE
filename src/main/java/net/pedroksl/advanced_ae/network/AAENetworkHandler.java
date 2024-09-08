package net.pedroksl.advanced_ae.network;

import com.glodblock.github.glodium.network.NetworkHandler;

import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.network.packet.*;

public class AAENetworkHandler extends NetworkHandler {

    public static final AAENetworkHandler INSTANCE = new AAENetworkHandler();

    public AAENetworkHandler() {
        super(AdvancedAE.MOD_ID);
        registerPacket(AdvPatternEncoderUpdateRequestPacket::new);
        registerPacket(AdvPatternEncoderPacket::new);
        registerPacket(AdvPatternEncoderChangeDirectionPacket::new);
        registerPacket(UpdateSideStatusPacket::new);
        registerPacket(OutputDirectionClientUpdatePacket::new);
    }
}
