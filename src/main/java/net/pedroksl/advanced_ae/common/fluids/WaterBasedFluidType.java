package net.pedroksl.advanced_ae.common.fluids;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.pedroksl.advanced_ae.AdvancedAE;

public class WaterBasedFluidType extends FluidType {

    private final ResourceLocation UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png");
    private final ResourceLocation WATER_STILL = AdvancedAE.makeId("block/water_still");
    private final ResourceLocation WATER_FLOW = AdvancedAE.makeId("block/water_flowing");
    private final ResourceLocation WATER_OVERLAY = AdvancedAE.makeId("block/water_overlay");

    protected int tintColor = -1;

    public WaterBasedFluidType(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return WATER_STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return WATER_FLOW;
            }

            @Override
            public ResourceLocation getOverlayTexture() {
                return WATER_OVERLAY;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                return UNDERWATER_LOCATION;
            }

            @Override
            public int getTintColor() {
                return tintColor;
            }

            @Override
            public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                return tintColor;
            }
        });
    }
}
