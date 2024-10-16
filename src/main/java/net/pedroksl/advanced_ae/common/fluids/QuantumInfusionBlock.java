package net.pedroksl.advanced_ae.common.fluids;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.pedroksl.advanced_ae.common.definitions.AAEFluids;

public class QuantumInfusionBlock extends LiquidBlock {
    public QuantumInfusionBlock() {
        super(
                (FlowingFluid) AAEFluids.QUANTUM_INFUSION.source(),
                Properties.of()
                        .mapColor(MapColor.COLOR_GRAY)
                        .replaceable()
                        .noCollission()
                        .strength(100.0F)
                        .pushReaction(PushReaction.DESTROY)
                        .noLootTable()
                        .liquid()
                        .sound(SoundType.EMPTY));
    }
}
