package net.pedroksl.advanced_ae.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;

public final class AAEBlockEntityRenderHelper {

    private AAEBlockEntityRenderHelper() {}

    public static void renderString(PoseStack poseStack, MultiBufferSource buffers, Component text, int textColor) {
        String renderedText = text.getString();
        Font fr = Minecraft.getInstance().font;
        int width = fr.width(renderedText);
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.02F);
        poseStack.scale(0.016129032F, -0.016129032F, 0.016129032F);
        poseStack.scale(0.5F, 0.5F, 0.0F);
        poseStack.translate(-0.5F * (float) width, 0.0F, 0.5F);
        fr.drawInBatch(
                renderedText,
                0.0F,
                0.0F,
                textColor,
                false,
                poseStack.last().pose(),
                buffers,
                Font.DisplayMode.NORMAL,
                0,
                15728880);
        poseStack.popPose();
    }
}
