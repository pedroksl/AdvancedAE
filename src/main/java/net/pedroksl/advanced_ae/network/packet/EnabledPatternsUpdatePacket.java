package net.pedroksl.advanced_ae.network.packet;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.client.gui.QuantumCrafterScreen;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

public record EnabledPatternsUpdatePacket(List<Boolean> enabledPatterns) implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, EnabledPatternsUpdatePacket> STREAM_CODEC =
            StreamCodec.ofMember(EnabledPatternsUpdatePacket::write, EnabledPatternsUpdatePacket::decode);

    public static final Type<EnabledPatternsUpdatePacket> TYPE =
            CustomAppEngPayload.createType("aae_crafter_enabled_buttons");

    @Override
    public Type<EnabledPatternsUpdatePacket> type() {
        return TYPE;
    }

    public EnabledPatternsUpdatePacket(List<Boolean> enabledPatterns) {
        this.enabledPatterns = enabledPatterns;
    }

    public static EnabledPatternsUpdatePacket decode(RegistryFriendlyByteBuf stream) {
        List<Boolean> list = new ArrayList<>();

        var size = stream.readInt();
        for (var x = 0; x < size; x++) {
            list.add(stream.readBoolean());
        }

        return new EnabledPatternsUpdatePacket(list);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeInt(this.enabledPatterns.size());
        for (var entry : this.enabledPatterns) {
            data.writeBoolean(entry);
        }
    }

    @Override
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterScreen screen) {
            screen.updateEnabledButtons(this.enabledPatterns);
        }
    }
}
