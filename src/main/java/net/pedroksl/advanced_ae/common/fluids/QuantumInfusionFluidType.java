package net.pedroksl.advanced_ae.common.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.SoundActions;
import net.pedroksl.ae2addonlib.util.WaterBasedFluidType;

public class QuantumInfusionFluidType extends WaterBasedFluidType {
    public QuantumInfusionFluidType() {
        super(Properties.create()
                .density(300)
                .viscosity(1000)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY));

        tintColor = 0xFF7362D3;
    }

    @Override
    public boolean canConvertToSource(FluidState state, LevelReader reader, BlockPos pos) {
        return false;
    }
}
