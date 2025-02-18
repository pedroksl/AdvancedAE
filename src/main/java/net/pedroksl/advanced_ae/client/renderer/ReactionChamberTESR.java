package net.pedroksl.advanced_ae.client.renderer;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import org.joml.Quaternionf;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.pedroksl.advanced_ae.common.entities.ReactionChamberEntity;

import appeng.api.orientation.RelativeSide;

public class ReactionChamberTESR implements BlockEntityRenderer<ReactionChamberEntity> {
    private final BlockEntityRendererProvider.Context context;

    private static final float ITEM_RENDER_SCALE = 0.4f;

    public ReactionChamberTESR(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(
            ReactionChamberEntity be, float v, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        var fluidStack = be.getFluidStack();
        if (fluidStack == null || fluidStack.isEmpty()) {
            return;
        }

        Level level = be.getLevel();
        if (level == null) {
            return;
        }

        BlockPos pos = be.getBlockPos();

        var state = fluidStack.getFluid().defaultFluidState();

        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation texture = fluidTypeExtensions.getStillTexture(fluidStack);

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(texture);
        int tintColor = fluidTypeExtensions.getTintColor(state, level, pos);

        VertexConsumer buffer = buffers.getBuffer(Sheets.translucentCullBlockSheet());

        var orientation = be.getOrientation();

        // spotless:off
        for (int x = 0; x < QUADS.length; x += 6) {
            float x0 = QUADS[x];
            float y0 = QUADS[x + 1];
            float z0 = QUADS[x + 2];
            float x1 = QUADS[x + 3];
            float y1 = QUADS[x + 4];
            float z1 = QUADS[x + 5];
            Direction face = orientation.getSide(SIDES[x / 6]);

            poseStack.pushPose();
            drawQuad(buffer, poseStack, x0, y0, z0, x1, y1, z1, sprite.getU0(), sprite.getV0(), sprite.getU1(),
                    sprite.getV1(), light, overlay, tintColor, face);
            poseStack.popPose();
        }
		// spotless:on

        var inv = be.getInput();
        for (int x = 0; x < inv.size(); x++) {
            var stack = inv.getStackInSlot(x);
            if (stack.isEmpty()) continue;

            this.renderItem(poseStack, stack, x, buffers, light, overlay);
        }
    }

    private static void drawQuad(
            VertexConsumer buffer,
            PoseStack poseStack,
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
            PoseStack poseStack,
            float x,
            float y,
            float z,
            float u,
            float v,
            Direction front,
            int light,
            int overlay,
            int color) {
        buffer.vertex(poseStack.last().pose(), x, y, z);
        buffer.color(color);
        buffer.uv(u, v);
        buffer.overlayCoords(overlay);
        buffer.uv2(light);
        setNormal(buffer, poseStack, front);
        buffer.endVertex();
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

    private void renderItem(
            PoseStack ms,
            ItemStack stack,
            int index,
            MultiBufferSource buffers,
            int combinedLight,
            int combinedOverlay) {
        if (!stack.isEmpty()) {
            ms.pushPose();
            float duration = 10000;
            long t = System.currentTimeMillis() % (int) duration;
            float angle = t / (duration / 360f) + index * 120;
            ms.rotateAround(new Quaternionf().rotationY(Mth.DEG_TO_RAD * angle), 0.5f, 0f, 0.5f);
            var yOffset = itemYPosition(t, duration) / 12f;
            ms.translate(0.25f, T - 0.1 + yOffset, 0.5f);
            ms.scale(ITEM_RENDER_SCALE, ITEM_RENDER_SCALE, ITEM_RENDER_SCALE);

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            itemRenderer.renderStatic(
                    stack,
                    ItemDisplayContext.GROUND,
                    combinedLight,
                    combinedOverlay,
                    ms,
                    buffers,
                    Minecraft.getInstance().level,
                    0);
            ms.popPose();
        }
    }

    private static void setNormal(VertexConsumer buffer, PoseStack poseStack, Direction front) {
        buffer.normal(poseStack.last().normal(), front.getStepX(), front.getStepY(), front.getStepZ());
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
