package net.pedroksl.advanced_ae.common.fluids;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.pedroksl.advanced_ae.common.definitions.AAEFluids;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class QuantumInfusionFluid extends BaseFlowingFluid {
    public static final BaseFlowingFluid.Properties PROPERTIES = new BaseFlowingFluid.Properties(
                    AAEFluids.QUANTUM_INFUSION.fluidTypeId(),
                    AAEFluids.QUANTUM_INFUSION.flowingId(),
                    AAEFluids.QUANTUM_INFUSION.sourceId())
            .bucket(AAEFluids.QUANTUM_INFUSION.bucketItemId())
            .block(AAEFluids.QUANTUM_INFUSION.blockId());

    protected QuantumInfusionFluid(Properties properties) {
        super(properties);
    }

    @Override
    public Fluid getFlowing() {
        return AAEFluids.QUANTUM_INFUSION.flowing();
    }

    @Override
    public Fluid getSource() {
        return AAEFluids.QUANTUM_INFUSION.source();
    }

    @Override
    public Item getBucket() {
        return AAEFluids.QUANTUM_INFUSION.bucketItem();
    }

    @Override
    protected boolean canConvertToSource(Level pLevel) {
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
