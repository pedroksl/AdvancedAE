package net.pedroksl.advanced_ae.client.renderer;

import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.items.armors.QuantumChestplate;

import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class QuantumArmorRenderer extends GeoArmorRenderer<QuantumChestplate> {
    public QuantumArmorRenderer() {
        super(new DefaultedItemGeoModel<>(AdvancedAE.makeId("quantum_armor")));
    }
}
