package net.pedroksl.advanced_ae.network;

import com.glodblock.github.glodium.network.NetworkHandler;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.network.packet.AdvPatternEncoderChangeDirectionPacket;
import net.pedroksl.advanced_ae.network.packet.AdvPatternEncoderPacket;

public class AAENetworkHandler extends NetworkHandler {

	public static final AAENetworkHandler INSTANCE = new AAENetworkHandler();

	public AAENetworkHandler() {
		super(AdvancedAE.MOD_ID);
		registerPacket(AdvPatternEncoderPacket::new);
		registerPacket(AdvPatternEncoderChangeDirectionPacket::new);
	}
}
