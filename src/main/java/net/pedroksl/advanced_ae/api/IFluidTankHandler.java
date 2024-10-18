package net.pedroksl.advanced_ae.api;

import org.lwjgl.glfw.GLFW;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.pedroksl.advanced_ae.network.packet.FluidTankClientAudioPacket;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import appeng.helpers.externalstorage.GenericStackInv;

public interface IFluidTankHandler {

    ItemStack getCarriedItem();

    GenericStackInv getTank(int index);

    boolean canExtractFromTank(int index);

    boolean canInsertInto(int index);

    void playAudioCues(FluidTankClientAudioPacket p);

    default void onItemUse(int index, int button) {
        var stack = getCarriedItem();
        if (!stack.isEmpty()) {
            var cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (cap != null) {

                var tank = getTank(index);
                if (tank == null) return;

                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    if (!canExtractFromTank(index)) return;

                    var genStack = tank.getStack(0);
                    if (genStack != null && genStack.what() != null) {
                        var fluid = ((AEFluidKey) genStack.what()).toStack((int) genStack.amount());
                        if (cap.fill(
                                        new FluidStack(fluid.getFluid(), (int)
                                                tank.extract(0, genStack.what(), 1000, Actionable.SIMULATE)),
                                        IFluidHandler.FluidAction.SIMULATE)
                                > 0) {
                            tank.extract(
                                    0,
                                    genStack.what(),
                                    cap.fill(new FluidStack(fluid.getFluid(), 1000), IFluidHandler.FluidAction.EXECUTE),
                                    Actionable.MODULATE);

                            playAudioCues(new FluidTankClientAudioPacket(false));
                        }
                    }
                } else {
                    if (!canInsertInto(index) || cap.getFluidInTank(0) == FluidStack.EMPTY) return;

                    var fluid = cap.getFluidInTank(0);
                    var genStack = GenericStack.fromFluidStack(fluid);
                    if (genStack != null && genStack.what() != null) {
                        if (cap.drain(
                                                (int) tank.insert(0, genStack.what(), 1000, Actionable.SIMULATE),
                                                IFluidHandler.FluidAction.SIMULATE)
                                        .getAmount()
                                > 0) {
                            cap.fill(
                                    new FluidStack(fluid.getFluid(), (int) tank.insert(
                                            0,
                                            genStack.what(),
                                            cap.drain(1000, IFluidHandler.FluidAction.EXECUTE)
                                                    .getAmount(),
                                            Actionable.MODULATE)),
                                    IFluidHandler.FluidAction.EXECUTE);

                            playAudioCues(new FluidTankClientAudioPacket(true));
                        }
                    }
                }
            }
        }
    }
}
