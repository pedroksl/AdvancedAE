package net.pedroksl.advanced_ae.network;

import com.glodblock.github.glodium.network.NetworkHandler;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import com.glodblock.github.glodium.network.packet.SGenericPacket;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.network.packet.AdvPatternEncoderChangeDirectionPacket;
import net.pedroksl.advanced_ae.network.packet.AdvPatternEncoderPacket;

public class AAENetworkHandler extends NetworkHandler {

	public static final AAENetworkHandler INSTANCE = new AAENetworkHandler();

	public AAENetworkHandler() {
		super(AdvancedAE.MOD_ID);
	}

	public void init() {
		registerPacket(SGenericPacket.class, SGenericPacket::new);
		registerPacket(CGenericPacket.class, CGenericPacket::new);
		registerPacket(AdvPatternEncoderPacket.class, AdvPatternEncoderPacket::new);
		registerPacket(AdvPatternEncoderChangeDirectionPacket.class, AdvPatternEncoderChangeDirectionPacket::new);
	}

}
