package net.pedroksl.advanced_ae.network;

import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.network.packet.*;
import net.pedroksl.advanced_ae.network.packet.quantumarmor.*;
import net.pedroksl.ae2addonlib.network.NetworkHandler;

public class AAENetworkHandler extends NetworkHandler {

    public static final AAENetworkHandler INSTANCE = new AAENetworkHandler();

    public AAENetworkHandler() {
        super(AdvancedAE.MOD_ID);
    }

    public void init() {
        registerPacket(AdvPatternEncoderPacket.class, AdvPatternEncoderPacket::new);
        registerPacket(AdvPatternEncoderChangeDirectionPacket.class, AdvPatternEncoderChangeDirectionPacket::new);
        registerPacket(PatternConfigServerUpdatePacket.class, PatternConfigServerUpdatePacket::new);
        registerPacket(PatternsUpdatePacket.class, PatternsUpdatePacket::new);
        registerPacket(SetStockAmountPacket.class, SetStockAmountPacket::new);
        registerPacket(MenuSelectionPacket.class, MenuSelectionPacket::new);
        registerPacket(KeysPressedPacket.class, KeysPressedPacket::new);
        registerPacket(QuantumArmorMagnetPacket.class, QuantumArmorMagnetPacket::new);
        registerPacket(QuantumArmorStylePacket.class, QuantumArmorStylePacket::new);
        registerPacket(QuantumArmorUpgradeFilterPacket.class, QuantumArmorUpgradeFilterPacket::new);
        registerPacket(QuantumArmorUpgradeStatePacket.class, QuantumArmorUpgradeStatePacket::new);
        registerPacket(QuantumArmorUpgradeValuePacket.class, QuantumArmorUpgradeValuePacket::new);
        registerPacket(QuantumArmorUpgradeTogglePacket.class, QuantumArmorUpgradeTogglePacket::new);
        registerPacket(ItemTrackingPacket.class, ItemTrackingPacket::new);
        registerPacket(ClearQuantumCrafterTerminalPacket.class, ClearQuantumCrafterTerminalPacket::new);
        registerPacket(QuantumCrafterTerminalPacket.class, QuantumCrafterTerminalPacket::new);
        registerPacket(QuantumCrafterTerminalClientAction.class, QuantumCrafterTerminalClientAction::new);
    }
}
