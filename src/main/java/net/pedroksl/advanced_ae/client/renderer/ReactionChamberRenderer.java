package net.pedroksl.advanced_ae.client.renderer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import org.joml.Quaternionf;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.pedroksl.advanced_ae.common.definitions.AAEConfig;
import net.pedroksl.advanced_ae.common.entities.ReactionChamberEntity;

import appeng.api.orientation.RelativeSide;

public class ReactionChamberRenderer implements BlockEntityRenderer<ReactionChamberEntity, ReactionChamberRenderState> {
    private final ItemModelResolver itemModelResolver;

    private static final float ITEM_RENDER_SCALE = 0.4f;

    public ReactionChamberRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemModelResolver = ctx.itemModelResolver();
    }

    @Override
    public ReactionChamberRenderState createRenderState() {
        return new ReactionChamberRenderState();
    }

    @Override
    public void extractRenderState(
            ReactionChamberEntity be,
            ReactionChamberRenderState state,
            float partialTicks,
            Vec3 cameraPos,
            @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTicks, cameraPos, crumblingOverlay);

        state.clearState();

        if (!AAEConfig.instance().getEnableEffects()) {
            return;
        }

        var fluidStack = be.getFluidStack();
        if (fluidStack == null || fluidStack.isEmpty()) {
            return;
        }

        Level level = be.getLevel();
        if (level == null) {
            return;
        }

        var fluidModel = Minecraft.getInstance()
                .getModelManager()
                .getFluidStateModelSet()
                .get(fluidStack.getFluid().defaultFluidState());

        state.fluidTexture = fluidModel.stillMaterial().sprite();
        var tintSource = fluidModel.fluidTintSource();
        state.fluidTint = tintSource != null ? tintSource.colorAsStack(fluidStack) : -1;
        state.orientation = be.getOrientation();

        var inv = be.getInput();
        for (int x = 0; x < inv.size(); x++) {
            var stack = inv.getStackInSlot(x);
            if (stack.isEmpty()) continue;

            this.itemModelResolver.updateForTopItem(
                    state.items.get(x),
                    stack,
                    ItemDisplayContext.GROUND,
                    be.getLevel(),
                    null,
                    // This is the random seed
                    (int) be.getBlockPos().asLong());
        }
    }

    @Override
    public void submit(
            ReactionChamberRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState cameraRenderState) {
        poseStack.pushPose();

        RenderType renderType = Sheets.translucentBlockSheet();

        for (int x = 0; x < QUADS.length; x += 6) {
            poseStack.pushPose();

            float x0 = QUADS[x];
            float y0 = QUADS[x + 1];
            float z0 = QUADS[x + 2];
            float x1 = QUADS[x + 3];
            float y1 = QUADS[x + 4];
            float z1 = QUADS[x + 5];
            Direction face = state.orientation.getSide(SIDES[x / 6]);

            submitNodeCollector.submitCustomGeometry(poseStack, renderType, ((pose, buffer) -> {
                drawQuad(
                        buffer,
                        pose,
                        x0,
                        y0,
                        z0,
                        x1,
                        y1,
                        z1,
                        state.fluidTexture.getU0(),
                        state.fluidTexture.getV0(),
                        state.fluidTexture.getU1(),
                        state.fluidTexture.getV1(),
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY,
                        state.fluidTint,
                        face);
            }));
            poseStack.popPose();
        }

        for (int x = 0; x < state.items.size(); x++) {
            var itemState = state.items.get(x);

            if (itemState.isEmpty()) {
                continue;
            }
            poseStack.pushPose();

            float duration = 10000;
            long t = System.currentTimeMillis() % (int) duration;
            float angle = t / (duration / 360f) + x * 120;
            poseStack.rotateAround(new Quaternionf().rotationY(Mth.DEG_TO_RAD * angle), 0.5f, 0f, 0.5f);
            var yOffset = itemYPosition(t, duration) / 12f;
            poseStack.translate(0.25f, T - 0.1 + yOffset, 0.5f);
            poseStack.scale(ITEM_RENDER_SCALE, ITEM_RENDER_SCALE, ITEM_RENDER_SCALE);

            itemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private static void drawQuad(
            VertexConsumer buffer,
            PoseStack.Pose poseStack,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            float u0,
            float v0,
            float u1,
            float v1,
            int light,
            int overlay,
            int color,
            Direction front) {

        if (front != Direction.UP && front != Direction.DOWN) {
            drawVertex(buffer, poseStack, x0, y0, z0, u0, v0, front, light, overlay, color);
            drawVertex(buffer, poseStack, x1, y0, z1, u0, v1, front, light, overlay, color);
            drawVertex(buffer, poseStack, x1, y1, z1, u1, v1, front, light, overlay, color);
            drawVertex(buffer, poseStack, x0, y1, z0, u1, v0, front, light, overlay, color);
        } else {
            drawVertex(buffer, poseStack, x0, y0, z0, u0, v0, front, light, overlay, color);
            drawVertex(buffer, poseStack, x1, y1, z0, u0, v1, front, light, overlay, color);
            drawVertex(buffer, poseStack, x1, y1, z1, u1, v1, front, light, overlay, color);
            drawVertex(buffer, poseStack, x0, y0, z1, u1, v0, front, light, overlay, color);
        }
    }

    private static void drawVertex(
            VertexConsumer buffer,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            float u,
            float v,
            Direction front,
            int light,
            int overlay,
            int color) {
        buffer.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light);
        setNormal(buffer, pose, front);
    }

    // Fluid Coordinates
    private static final float L = 2 / 16.f; // left (x-axis)
    private static final float R = 14 / 16.f; // right (x-axis)
    private static final float T = 11 / 16.f; // top (y-axis)
    private static final float B = 2 / 16.f; // bottom (y-axis)
    private static final float FR = 2 / 16.f; // front (z-axis)
    private static final float BA = 14 / 16.f; // back (z-axis)

    // Face limits for the fluid block (has no bottom)
    private static final float[] QUADS = {
        // Front Face
        L, T, FR, R, B, FR,
        // Left Face
        R, T, FR, R, B, BA,
        // Right Face
        L, T, BA, L, B, FR,
        // Bottom Face
        R, T, BA, L, B, BA,
        // Top Face
        L, T, BA, R, T, FR
    };
    public static RelativeSide[] SIDES = {
        RelativeSide.FRONT, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.BACK, RelativeSide.TOP
    };

    private static void setNormal(VertexConsumer buffer, PoseStack.Pose pose, Direction front) {
        buffer.setNormal(pose, front.getStepX(), front.getStepY(), front.getStepZ());
    }

    // Calculates a value between -1 and 1 for the y position of the rendered items
    private static float itemYPosition(long x, float max) {
        // 0 -> 0, max/2 -> 0, max -> 0
        // max/4 -> maxY, 3max/4 -> -maxY
        // max -> 2*PI
        float rad = x * 2 * Mth.PI / max;
        return Mth.cos(rad);
    }
}
