package net.pedroksl.advanced_ae.network;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.network.packet.*;
import net.pedroksl.advanced_ae.network.packet.quantumarmor.*;
import net.pedroksl.ae2addonlib.network.AddonNetworkHandler;

public class AAENetworkHandler extends AddonNetworkHandler {

    public static final AAENetworkHandler INSTANCE = new AAENetworkHandler();

    public AAENetworkHandler() {
        super(AdvancedAE.MOD_ID);
    }

    @Override
    public void onRegister(PayloadRegistrar registrar) {
        // spotless:off
        serverbound(registrar, AdvPatternEncoderChangeDirectionPacket.TYPE, AdvPatternEncoderChangeDirectionPacket.STREAM_CODEC);
        serverbound(registrar, AAEConfigButtonPacket.TYPE, AAEConfigButtonPacket.STREAM_CODEC);
        serverbound(registrar, SetStockAmountPacket.TYPE, SetStockAmountPacket.STREAM_CODEC);
        serverbound(registrar, QuantumArmorUpgradeTogglePacket.TYPE, QuantumArmorUpgradeTogglePacket.STREAM_CODEC);
        serverbound(registrar, QuantumArmorUpgradeValuePacket.TYPE, QuantumArmorUpgradeValuePacket.STREAM_CODEC);
        serverbound(registrar, QuantumArmorUpgradeFilterPacket.TYPE, QuantumArmorUpgradeFilterPacket.STREAM_CODEC);
        serverbound(registrar, QuantumArmorStylePacket.TYPE, QuantumArmorStylePacket.STREAM_CODEC);
        serverbound(registrar, QuantumArmorMagnetPacket.TYPE, QuantumArmorMagnetPacket.STREAM_CODEC);
        serverbound(registrar, KeysPressedPacket.TYPE, KeysPressedPacket.STREAM_CODEC);
        serverbound(registrar, QuantumCrafterTerminalClientAction.TYPE, QuantumCrafterTerminalClientAction.STREAM_CODEC);

        clientbound(registrar, AdvPatternEncoderPacket.TYPE, AdvPatternEncoderPacket.STREAM_CODEC);
        clientbound(registrar, PatternConfigServerUpdatePacket.TYPE, PatternConfigServerUpdatePacket.STREAM_CODEC);
        clientbound(registrar, PatternsUpdatePacket.TYPE, PatternsUpdatePacket.STREAM_CODEC);
        clientbound(registrar, QuantumArmorUpgradeStatePacket.TYPE, QuantumArmorUpgradeStatePacket.STREAM_CODEC);
        clientbound(registrar, MenuSelectionPacket.TYPE, MenuSelectionPacket.STREAM_CODEC);
        clientbound(registrar, ItemTrackingPacket.TYPE, ItemTrackingPacket.STREAM_CODEC);
        clientbound(registrar, QuantumCrafterTerminalPacket.TYPE, QuantumCrafterTerminalPacket.STREAM_CODEC);
        clientbound(registrar, ClearQuantumCrafterTerminalPacket.TYPE, ClearQuantumCrafterTerminalPacket.STREAM_CODEC);
        // spotless:on
    }
}
