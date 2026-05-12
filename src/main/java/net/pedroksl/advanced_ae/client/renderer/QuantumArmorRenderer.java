package net.pedroksl.advanced_ae.client.renderer;

import java.util.HashMap;
import java.util.Map;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.ae2addonlib.util.Colors;

public class QuantumArmorRenderer<R extends HumanoidRenderState & GeoRenderState>
        extends GeoArmorRenderer<QuantumArmorBase, R> {

    public static final String HUD_BONE = "hud";
    public static final String LEFT_BLADE_BONE = "blade_left";
    public static final String RIGHT_BLADE_BONE = "blade_right";
    public static final String FACE_SHIELD_BONE = "face_shield";

    private final Map<EquipmentSlot, Boolean> visibilityMap = new HashMap<>();

    private int tintColor = Colors.PURPLE.argb();

    public QuantumArmorRenderer() {
        super(new DefaultedItemGeoModel<>(AdvancedAE.makeId("quantum_armor")));

        visibilityMap.put(EquipmentSlot.HEAD, true);
        visibilityMap.put(EquipmentSlot.CHEST, true);
        visibilityMap.put(EquipmentSlot.LEGS, true);
        visibilityMap.put(EquipmentSlot.FEET, true);

        withRenderLayer(new QuantumArmorTintLayer<>(this));
        withRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    public void setBoneVisible(String boneName, boolean visible) {
        // TODO bone visibility
        //        this.getGeoModel().getBone(boneName).ifPresent(geoBone -> geoBone.setHidden(!visible));
    }

    public void setVisible(EquipmentSlot slot, boolean visible) {
        visibilityMap.put(slot, visible);
    }

    //    @Override
    //    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
    //        setVisibleBySlot(currentSlot);
    //
    //        super.applyBoneVisibilityBySlot(currentSlot);
    //    }

    private void setVisibleBySlot(EquipmentSlot slot) {
        //        HumanoidModel<?> model = this;
        //
        //        boolean visible = visibilityMap.get(slot);
        //        switch (slot) {
        //            case HEAD -> {
        //                model.head.visible = visible;
        //            }
        //            case CHEST -> {
        //                model.body.visible = visible;
        //                model.rightArm.visible = visible;
        //                model.leftArm.visible = visible;
        //            }
        //            case LEGS, FEET -> {
        //                model.rightLeg.visible = visible;
        //                model.leftLeg.visible = visible;
        //            }
        //        }
    }

    public void setTintColor(int color) {
        // this.tintLayer.tintColor = color;
    }

    public int getTintColor() {
        return this.tintColor;
    }

    private static class QuantumArmorTintLayer<T extends GeoAnimatable, O, R extends GeoRenderState>
            extends GeoRenderLayer<T, O, R> {

        private final Identifier TINT_TEXTURE = AdvancedAE.makeId("textures/item/quantum_armor_tint.png");

        public QuantumArmorTintLayer(GeoRenderer<T, O, R> renderer) {
            super(renderer);
        }

        @Override
        protected Identifier getTextureResource(R renderState) {
            return TINT_TEXTURE;
        }

        @Override
        public void submitRenderTask(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
            var color = renderPassInfo.renderColor();

            renderPassInfo
                    .renderState()
                    .addGeckolibData(
                            DataTickets.RENDER_COLOR, ((QuantumArmorRenderer<?>) getRenderer()).getTintColor());
            super.submitRenderTask(renderPassInfo, renderTasks);
            renderPassInfo.renderState().addGeckolibData(DataTickets.RENDER_COLOR, color);
        }
    }
}
