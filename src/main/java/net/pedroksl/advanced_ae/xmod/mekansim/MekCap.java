package net.pedroksl.advanced_ae.xmod.mekansim;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;

import mekanism.common.capabilities.radiation.item.RadiationShieldingHandler;

public class MekCap {

    public static void initCap(RegisterCapabilitiesEvent event) {
        try {
            var cap = mekanism.common.capabilities.Capabilities.RADIATION_SHIELDING;
            for (var armor : AAEItems.getQuantumArmor()) {
                event.registerItem(
                        cap,
                        (stack, ctx) -> RadiationShieldingHandler.create(0.25),
                        armor.get().asItem());
            }
        } catch (Throwable ignored) {
            // NO-OP
        }
    }
}
