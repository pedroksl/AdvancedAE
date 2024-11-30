package net.pedroksl.advanced_ae.xmod.mekansim;

import mekanism.common.capabilities.radiation.item.RadiationShieldingHandler;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;

public class MekanismPlugin {

    public static void initCap() {
        try {
            var cap = mekanism.common.capabilities.Capabilities.RADIATION_SHIELDING;
            RadiationShieldingHandler.create(is -> is.getItem() instanceof QuantumArmorBase ? 0.25 : 0);
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
