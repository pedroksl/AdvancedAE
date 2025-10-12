package net.pedroksl.advanced_ae.common.definitions;

import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.fluids.QuantumInfusionBlock;
import net.pedroksl.advanced_ae.common.fluids.QuantumInfusionFluid;
import net.pedroksl.advanced_ae.common.fluids.QuantumInfusionFluidType;
import net.pedroksl.ae2addonlib.registry.AddonFluids;
import net.pedroksl.ae2addonlib.registry.helpers.FluidDefinition;

public class AAEFluids extends AddonFluids {

    public static final AAEFluids INSTANCE = new AAEFluids();

    AAEFluids() {
        super(AdvancedAE.MOD_ID);
    }

    public static final FluidDefinition<?, ?> QUANTUM_INFUSION = fluid(
            "Quantum Infusion",
            "quantum_infusion",
            QuantumInfusionFluidType::new,
            QuantumInfusionFluid.Flowing::new,
            QuantumInfusionFluid.Source::new,
            QuantumInfusionBlock::new);
}
