package net.pedroksl.advanced_ae.network;

import com.glodblock.github.glodium.network.NetworkHandler;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.network.packet.*;
import net.pedroksl.advanced_ae.network.packet.quantumarmor.*;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.ServerboundPacket;

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

    @Override
    public void onRegister(RegisterPayloadHandlersEvent event) {
        super.onRegister(event);

        serverbound(this.registrar, AAEConfigButtonPacket.TYPE, AAEConfigButtonPacket.STREAM_CODEC);
        serverbound(this.registrar, ServerActionPacket.TYPE, ServerActionPacket.STREAM_CODEC);
        serverbound(this.registrar, SetStockAmountPacket.TYPE, SetStockAmountPacket.STREAM_CODEC);
        serverbound(this.registrar, AAEHotkeyPacket.TYPE, AAEHotkeyPacket.STREAM_CODEC);
        serverbound(this.registrar, QuantumArmorUpgradeTogglePacket.TYPE, QuantumArmorUpgradeTogglePacket.STREAM_CODEC);
        serverbound(this.registrar, QuantumArmorUpgradeValuePacket.TYPE, QuantumArmorUpgradeValuePacket.STREAM_CODEC);
        serverbound(this.registrar, QuantumArmorUpgradeFilterPacket.TYPE, QuantumArmorUpgradeFilterPacket.STREAM_CODEC);
        serverbound(this.registrar, QuantumArmorMagnetPacket.TYPE, QuantumArmorMagnetPacket.STREAM_CODEC);

        clientbound(this.registrar, PatternConfigServerUpdatePacket.TYPE, PatternConfigServerUpdatePacket.STREAM_CODEC);
        clientbound(this.registrar, EnabledPatternsUpdatePacket.TYPE, EnabledPatternsUpdatePacket.STREAM_CODEC);
        clientbound(this.registrar, QuantumArmorUpgradeStatePacket.TYPE, QuantumArmorUpgradeStatePacket.STREAM_CODEC);
        clientbound(this.registrar, FluidTankStackUpdatePacket.TYPE, FluidTankStackUpdatePacket.STREAM_CODEC);
    }

    private static <T extends ClientboundPacket> void clientbound(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playToClient(type, codec, ClientboundPacket::handleOnClient);
    }

    private static <T extends ServerboundPacket> void serverbound(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playToServer(type, codec, ServerboundPacket::handleOnServer);
    }
}
