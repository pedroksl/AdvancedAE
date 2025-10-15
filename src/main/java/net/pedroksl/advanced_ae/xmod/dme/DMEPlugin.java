package net.pedroksl.advanced_ae.xmod.dme;

import java.util.List;

import net.neoforged.fml.InterModComms;
import net.pedroksl.advanced_ae.xmod.Addons;

public class DMEPlugin {

    private static List<String> BLACKLIST = List.of("net.pedroksl.advanced_ae.client.gui.QuantumCrafterTermScreen");

    public static void sendBlacklistIMC() {
        for (var gui : BLACKLIST) {
            InterModComms.sendTo(Addons.DARKMODEEVERYWHERE.getModId(), "dme-shaderblacklist", () -> gui);
        }
    }
}
