package net.pedroksl.advanced_ae.api;

import net.neoforged.neoforge.fluids.FluidStack;

public interface IFluidTankScreen {

    void updateFluidTankContents(int index, FluidStack stack);

    void playSoundFeedback(boolean isInsert);
}
