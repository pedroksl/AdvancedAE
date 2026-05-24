package net.pedroksl.advanced_ae.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;

public final class AAEBlockEntityRenderHelper {

    private AAEBlockEntityRenderHelper() {}

    public static void renderString(
            PoseStack poseStack, FormattedCharSequence text, int textWidth, int textColor, SubmitNodeCollector nodes) {
        Font fr = Minecraft.getInstance().font;
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.02F);
        poseStack.scale(0.016129032F, -0.016129032F, 0.016129032F);
        poseStack.scale(0.5F, 0.5F, 0.0F);
        poseStack.translate(-0.5f * textWidth, -0.5f * fr.lineHeight, 0.5f);
        nodes.submitText(
                poseStack, 0, 0, text, false, Font.DisplayMode.NORMAL, LightCoordsUtil.FULL_BRIGHT, textColor, 0, 0);
        poseStack.popPose();
    }

    public static void renderTexture(
            PoseStack poseStack, Identifier texture, int textureSize, int textWidth, SubmitNodeCollector nodes) {
        Font fr = Minecraft.getInstance().font;
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.02F);
        poseStack.scale(0.016129032F, -0.016129032F, 0.016129032F);
        poseStack.scale(0.5F, 0.5F, 0.0F);
        poseStack.translate(0.5f * textWidth, -0.6f * fr.lineHeight, 0.5f);
        nodes.submitCustomGeometry(poseStack, RenderTypes.entityCutout(texture), (pose, consumer) -> {
            var color = -1;
            var s = 2 * textureSize;
            var light = LightCoordsUtil.FULL_BRIGHT;
            consumer.addVertex(pose, 0, s, 0)
                    .setColor(color)
                    .setUv(0, 1F)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal(0.0F, 1.0F, 0.0F);
            consumer.addVertex(pose, s, s, 0)
                    .setColor(color)
                    .setUv(1F, 1F)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal(0.0F, 1.0F, 0.0F);
            consumer.addVertex(pose, s, 0, 0)
                    .setColor(color)
                    .setUv(1F, 0)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal(0.0F, 1.0F, 0.0F);
            consumer.addVertex(pose, 0, 0, 0)
                    .setColor(color)
                    .setUv(0, 0)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal(0.0F, 1.0F, 0.0F);
        });
        poseStack.popPose();
    }
}
