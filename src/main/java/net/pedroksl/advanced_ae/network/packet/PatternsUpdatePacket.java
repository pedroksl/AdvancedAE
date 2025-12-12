package net.pedroksl.advanced_ae.network.packet;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.pedroksl.advanced_ae.client.gui.QuantumCrafterScreen;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

public record PatternsUpdatePacket(List<Boolean> invalidPatterns, List<Boolean> enabledPatterns)
        implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, PatternsUpdatePacket> STREAM_CODEC =
            StreamCodec.ofMember(PatternsUpdatePacket::write, PatternsUpdatePacket::decode);

    public static final Type<PatternsUpdatePacket> TYPE = CustomAppEngPayload.createType("aae_crafter_enabled_buttons");

    @Override
    public Type<PatternsUpdatePacket> type() {
        return TYPE;
    }

    public PatternsUpdatePacket(List<Boolean> invalidPatterns, List<Boolean> enabledPatterns) {
        this.invalidPatterns = invalidPatterns;
        this.enabledPatterns = enabledPatterns;
    }

    public static PatternsUpdatePacket decode(RegistryFriendlyByteBuf stream) {
        List<Boolean> invalidList = new ArrayList<>();
        List<Boolean> enabledList = new ArrayList<>();

        var size = stream.readInt();
        for (var x = 0; x < size; x++) {
            invalidList.add(stream.readBoolean());
        }

        size = stream.readInt();
        for (var x = 0; x < size; x++) {
            enabledList.add(stream.readBoolean());
        }

        return new PatternsUpdatePacket(invalidList, enabledList);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeInt(this.invalidPatterns.size());
        for (var entry : this.invalidPatterns) {
            data.writeBoolean(entry);
        }

        data.writeInt(this.enabledPatterns.size());
        for (var entry : this.enabledPatterns) {
            data.writeBoolean(entry);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterScreen screen) {
            screen.updateInvalidButtons(this.invalidPatterns);
            screen.updateEnabledButtons(this.enabledPatterns);
        }
    }
}
