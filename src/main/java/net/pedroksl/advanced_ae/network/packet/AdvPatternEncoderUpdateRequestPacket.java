package net.pedroksl.advanced_ae.network.packet;

import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.gui.patternencoder.AdvPatternEncoderContainer;

public class AdvPatternEncoderUpdateRequestPacket implements IMessage {

	public AdvPatternEncoderUpdateRequestPacket() {
	}

	@Override
	public void toBytes(RegistryFriendlyByteBuf buf) {
	}

	@Override
	public void fromBytes(RegistryFriendlyByteBuf buf) {
	}

	@Override
	public void onMessage(Player player) {
		if (player.containerMenu instanceof AdvPatternEncoderContainer encoderContainer) {
			encoderContainer.onUpdateRequested();
		}
	}

	@Override
	public ResourceLocation id() {
		return AdvancedAE.id("pattern_encoder_update_request");
	}

	@Override
	public boolean isClient() {
		return false;
	}
}
