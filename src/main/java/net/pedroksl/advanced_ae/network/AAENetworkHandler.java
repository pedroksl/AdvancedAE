package net.pedroksl.advanced_ae.network;

import com.glodblock.github.glodium.network.NetworkHandler;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import com.glodblock.github.glodium.network.packet.SGenericPacket;

import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.network.packet.*;

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
        registerPacket(PatternConfigServerUpdatePacket.class, PatternConfigServerUpdatePacket::new);
        registerPacket(EnabledPatternsUpdatePacket.class, EnabledPatternsUpdatePacket::new);
        registerPacket(SetStockAmountPacket.class, SetStockAmountPacket::new);
        registerPacket(FluidTankClientAudioPacket.class, FluidTankClientAudioPacket::new);
        registerPacket(FluidTankStackUpdatePacket.class, FluidTankStackUpdatePacket::new);
        registerPacket(OutputDirectionClientUpdatePacket.class, OutputDirectionClientUpdatePacket::new);
        registerPacket(UpdateSideStatusPacket.class, UpdateSideStatusPacket::new);
        registerPacket(FluidTankItemUsePacket.class, FluidTankItemUsePacket::new);
        registerPacket(AAEHotkeyPacket.class, AAEHotkeyPacket::new);
        registerPacket(AAEConfigButtonPacket.class, AAEConfigButtonPacket::new);
    }
}
