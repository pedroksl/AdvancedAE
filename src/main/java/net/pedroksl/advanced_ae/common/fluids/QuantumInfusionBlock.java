package net.pedroksl.advanced_ae.common.fluids;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.pedroksl.advanced_ae.common.definitions.AAEFluids;

public class QuantumInfusionBlock extends LiquidBlock {
    public QuantumInfusionBlock() {
        super(
                () -> (FlowingFluid) AAEFluids.QUANTUM_INFUSION.flowing(),
                Properties.of()
                        .liquid()
                        .strength(20.0F)
                        .noLootTable()
                        .replaceable()
                        .noCollission()
                        .pushReaction(PushReaction.DESTROY)
                        .mapColor(MapColor.COLOR_PURPLE));
    }
}
