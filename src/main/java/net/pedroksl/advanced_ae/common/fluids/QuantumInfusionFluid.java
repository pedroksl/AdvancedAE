package net.pedroksl.advanced_ae.common.fluids;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.pedroksl.advanced_ae.common.definitions.AAEFluids;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class QuantumInfusionFluid extends BaseFlowingFluid {
    public static final BaseFlowingFluid.Properties PROPERTIES = new BaseFlowingFluid.Properties(
                    AAEFluids.QUANTUM_INFUSION.fluidTypeHolder()::value,
                    AAEFluids.QUANTUM_INFUSION.sourceHolder()::value,
                    AAEFluids.QUANTUM_INFUSION.flowingHolder()::value)
            .bucket(AAEFluids.QUANTUM_INFUSION::bucketItem)
            .block(AAEFluids.QUANTUM_INFUSION.blockHolder()::value);

    protected QuantumInfusionFluid(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean canConvertToSource(ServerLevel level) {
        return false;
    }

    public static class Flowing extends QuantumInfusionFluid {
        public Flowing() {
            super(PROPERTIES);
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> pBuilder) {
            super.createFluidStateDefinition(pBuilder);
            pBuilder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState pState) {
            return pState.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState pState) {
            return false;
        }
    }

    public static class Source extends QuantumInfusionFluid {
        public Source() {
            super(PROPERTIES);
        }

        @Override
        public int getAmount(FluidState pState) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState pState) {
            return true;
        }
    }
}
