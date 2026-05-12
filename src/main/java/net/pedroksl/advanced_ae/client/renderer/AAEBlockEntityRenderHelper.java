package net.pedroksl.advanced_ae.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;

public final class AAEBlockEntityRenderHelper {

    private AAEBlockEntityRenderHelper() {}

    public static void renderString(
            PoseStack poseStack,
            FormattedCharSequence text,
            int textWidth,
            int textColor,
            SubmitNodeCollector nodes,
            float itemScale) {
        Font fr = Minecraft.getInstance().font;

        double spacing = -(itemScale / 2) - 0.25 / 16f;

        poseStack.pushPose();
        poseStack.translate(0.0f, spacing, 0.02f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.scale(1.0f / 62.0f, -1.0f / 62.0f, 1.0f / 62.0f);
        poseStack.translate(-0.5f * textWidth, -0.5f * Minecraft.getInstance().font.lineHeight, 0.5f);
        nodes.submitText(
                poseStack, 0, 0, text, false, Font.DisplayMode.NORMAL, LightCoordsUtil.FULL_BRIGHT, textColor, 0, 0);
        poseStack.popPose();
    }
}
