package net.pedroksl.advanced_ae.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.helpers.AAEColor;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;

import software.bernie.geckolib.cache.object.BakedGeoModel;
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

    private final QuantumArmorTintLayer tintLayer;

    public QuantumArmorRenderer() {
        super(new DefaultedItemGeoModel<>(AdvancedAE.makeId("quantum_armor")));

        this.tintLayer = new QuantumArmorTintLayer(this);
        addRenderLayer(this.tintLayer);
    }

    public void setBoneVisible(String boneName, boolean visible) {
        this.getGeoModel().getBone(boneName).ifPresent(geoBone -> geoBone.setHidden(!visible));
    }

    public void setTintColor(int color) {
        this.tintLayer.tintColor = color;
    }

    private static class QuantumArmorTintLayer extends AutoGlowingGeoLayer<QuantumArmorBase> {

        private final ResourceLocation TINT_TEXTURE = AdvancedAE.makeId("textures/item/quantum_armor_tint.png");
        private int tintColor = AAEColor.PURPLE.argb();

        public QuantumArmorTintLayer(GeoArmorRenderer<QuantumArmorBase> armorRenderer) {
            super(armorRenderer);
        }

        @Override
        public void render(
                PoseStack poseStack,
                QuantumArmorBase animatable,
                BakedGeoModel bakedModel,
                @Nullable RenderType renderType,
                MultiBufferSource bufferSource,
                @Nullable VertexConsumer buffer,
                float partialTick,
                int packedLight,
                int packedOverlay) {
            // Render Tint parts
            RenderType render = RenderType.entityCutout(TINT_TEXTURE);
            this.getRenderer()
                    .reRender(
                            bakedModel,
                            poseStack,
                            bufferSource,
                            animatable,
                            render,
                            bufferSource.getBuffer(render),
                            partialTick,
                            packedLight,
                            packedOverlay,
                            this.tintColor);

            // Render emissive parts
            renderType = getRenderType(animatable, bufferSource);
            if (renderType != null) {
                getRenderer()
                        .reRender(
                                bakedModel,
                                poseStack,
                                bufferSource,
                                animatable,
                                renderType,
                                bufferSource.getBuffer(renderType),
                                partialTick,
                                LightTexture.FULL_SKY,
                                packedOverlay,
                                this.tintColor);
            }

            super.render(
                    poseStack,
                    animatable,
                    bakedModel,
                    renderType,
                    bufferSource,
                    buffer,
                    partialTick,
                    packedLight,
                    packedOverlay);
        }

        @Override
        protected ResourceLocation getTextureResource(QuantumArmorBase animatable) {
            return TINT_TEXTURE;
        }
    }
}
