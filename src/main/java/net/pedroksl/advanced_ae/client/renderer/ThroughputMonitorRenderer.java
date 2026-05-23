package net.pedroksl.advanced_ae.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.parts.ThroughputMonitorPart;

import appeng.api.orientation.BlockOrientation;
import appeng.api.stacks.AmountFormat;
import appeng.api.util.AEColor;
import appeng.client.api.renderer.parts.PartRenderer;
import appeng.client.render.BlockEntityRenderHelper;

public class ThroughputMonitorRenderer implements PartRenderer<ThroughputMonitorPart, ThroughputMonitorRenderState> {
    private final Font font;

    public static final int positiveColor = AEColor.GREEN.mediumVariant;
    public static final int negativeColor = AEColor.RED.mediumVariant;

    private static final int TREND_TEXTURE_SIZE = 5;
    private static final Identifier POSITIVE_TEXTURE = AdvancedAE.makeId("textures/part/throughput_monitor_up.png");
    private static final Identifier NEGATIVE_TEXTURE = AdvancedAE.makeId("textures/part/throughput_monitor_down.png");

    public ThroughputMonitorRenderer() {
        this.font = Minecraft.getInstance().font;
    }

    @Override
    public ThroughputMonitorRenderState createState() {
        return new ThroughputMonitorRenderState();
    }

    @Override
    public Class<ThroughputMonitorRenderState> stateClass() {
        return ThroughputMonitorRenderState.class;
    }

    @Override
    public void extract(ThroughputMonitorPart part, ThroughputMonitorRenderState state, float partialTicks) {
        if (part.isActive()) {
            state.orientation = BlockOrientation.get(part.getSide(), part.getSpin());
            state.textColor = part.getColor().contrastTextColor;
            state.textColor |= 0xFF000000; // ensure full visibility
            var displayed = part.getDisplayed();
            if (displayed != null) {
                int seed = (int) part.getHost().getBlockEntity().getBlockPos().asLong();
                state.what.extract(displayed, part.getLevel(), seed);

                var amount = part.getAmount();
                Component displayText = Component.literal(displayed.formatAmount(amount, AmountFormat.SLOT));
                state.text = displayText.getVisualOrderText();
                state.textWidth = font.width(state.text);
                state.throughput = part.getThroughputText().getVisualOrderText();

                var throughput = part.getThroughput();
                if (throughput > 0) {
                    state.subColor = positiveColor | 0xFF000000;
                    state.trendTexture = POSITIVE_TEXTURE;
                } else if (throughput < 0) {
                    state.subColor = negativeColor | 0xFF000000;
                    state.trendTexture = NEGATIVE_TEXTURE;
                } else {
                    state.subColor = state.textColor;
                    state.trendTexture = null;
                }
                state.throughputWidth = font.width(state.throughput) + (state.trendTexture != null ? 5 : 0);
            } else {
                state.what.clear();
                state.text = null;
                state.textWidth = 0;
            }
        }
    }

    @Override
    public void submit(
            ThroughputMonitorRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector nodes,
            CameraRenderState cameraRenderState) {
        if (state.what.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5); // Move to the center of the block
        BlockEntityRenderHelper.rotateToFace(poseStack, state.orientation);
        // Move to the "front" of the face.
        poseStack.translate(0, 0.05, 0.5);

        float itemScale = 6 / 16f;

        BlockEntityRenderHelper.submitItem2dWithAmount(
                poseStack, state.what, state.text, state.textColor, state.textWidth, nodes, itemScale);

        poseStack.translate(0, -0.3, 0);
        AAEBlockEntityRenderHelper.renderString(
                poseStack, state.throughput, state.throughputWidth, state.subColor, nodes);

        if (state.trendTexture != null) {
            AAEBlockEntityRenderHelper.renderTexture(
                    poseStack, state.trendTexture, TREND_TEXTURE_SIZE, state.throughputWidth, nodes);
        }

        poseStack.popPose();
    }
}
