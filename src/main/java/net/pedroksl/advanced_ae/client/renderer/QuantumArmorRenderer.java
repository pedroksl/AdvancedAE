package net.pedroksl.advanced_ae.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;

import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class QuantumArmorRenderer extends GeoArmorRenderer<QuantumArmorBase> {

    public static final String HUD_BONE = "hud";
    public static final String LEFT_BLADE_BONE = "blade_left";
    public static final String RIGHT_BLADE_BONE = "blade_right";
    public static final String FACE_SHIELD_BONE = "face_shield";
    public static final String RIGHT_ARM = "armorRightArm";
    public static final String LEFT_ARM = "armorLeftArm";

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

    public void renderChildBones(
            String boneName,
            PoseStack poseStack,
            QuantumArmorBase animatable,
            RenderType renderType,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            boolean isReRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            int colour) {
        var bone = this.getGeoModel().getBone(boneName);
        bone.ifPresent(geoBone -> {
            geoBone.setHidden(false);
            renderChildBones(
                    poseStack,
                    animatable,
                    geoBone,
                    renderType,
                    bufferSource,
                    buffer,
                    isReRender,
                    partialTick,
                    packedLight,
                    packedOverlay,
                    colour);
        });

        //        if (bone.isPresent()) {
        //            var childBones = bone.get().getChildBones();
        //            childBones.stream().filter(bone -> bone.getName().contains("_translucent"))
        //        }

        /*
        val layer = RenderLayer.getEntityTranslucentCull(layerTexture)
        val hidden = bakedModel.bones.filter { "_translucent" !in it.name }.onEach { it.isHidden = true }

        renderer.reRender(bakedModel, poseStack, bufferSource, animatable, layer, buffer, partialTick, packedLight, packedOverlay, 1f, 1f, 1f, 1f)

        hidden.forEach {
        				it.isHidden = false
        				it.setChildrenHidden(false)
        }
        	*/
    }
}
