package net.pedroksl.advanced_ae.client.renderer;

import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.items.armors.QuantumChestplate;

import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class QuantumArmorRenderer extends GeoArmorRenderer<QuantumChestplate> {

    public static final String HUD_BONE = "hud";

    public QuantumArmorRenderer() {
        super(new DefaultedItemGeoModel<>(AdvancedAE.makeId("quantum_armor")));

        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    public void setBoneVisible(String boneName, boolean visible) {
        var bone = this.getGeoModel().getBone(boneName);
        if (bone != null && bone.isPresent()) {
            super.setBoneVisible(bone.get(), visible);
        }
    }
}
