package net.pedroksl.advanced_ae.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.pedroksl.advanced_ae.common.parts.ThroughputMonitorPart;

import appeng.api.orientation.BlockOrientation;
import appeng.api.stacks.AmountFormat;
import appeng.client.api.renderer.parts.PartRenderer;
import appeng.client.render.BlockEntityRenderHelper;

public class ThroughputMonitorRenderer implements PartRenderer<ThroughputMonitorPart, ThroughputMonitorRenderState> {
    private final Font font;

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
                state.subColor = part.getThroughputColor();
                state.throughputWidth = font.width(state.throughput);
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
        poseStack.translate(0, 0, 0.5);

        float itemScale = 6 / 16f;

        BlockEntityRenderHelper.submitItem2dWithAmount(
                poseStack, state.what, state.text, state.textColor, state.textWidth, nodes, itemScale);

        poseStack.translate(0, -0.23F, 0);
        AAEBlockEntityRenderHelper.renderString(
                poseStack, state.throughput, state.throughputWidth, state.subColor, nodes, itemScale);

        poseStack.popPose();
    }
}
