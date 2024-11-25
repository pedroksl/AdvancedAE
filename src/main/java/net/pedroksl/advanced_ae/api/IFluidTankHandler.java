package net.pedroksl.advanced_ae.api;

import org.lwjgl.glfw.GLFW;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.pedroksl.advanced_ae.network.packet.FluidTankClientAudioPacket;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import appeng.helpers.externalstorage.GenericStackInv;

public interface IFluidTankHandler {

    ItemStack getCarriedItem();

    void setCarriedItem(ItemStack stack);

    GenericStackInv getTank();

    boolean canExtractFromTank(int index);

    boolean canInsertInto(int index);

    void playAudioCues(FluidTankClientAudioPacket p);

    default void onItemUse(int index, int button) {
        var stack = getCarriedItem();
        if (!stack.isEmpty()) {
            var forgeCap = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
            forgeCap.ifPresent((cap) -> {
                var tank = getTank();
                if (tank == null) return;

                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    if (!canExtractFromTank(index)) return;

                    var genStack = tank.getStack(index);
                    if (genStack != null && genStack.what() != null) {
                        var fluid = ((AEFluidKey) genStack.what()).toStack((int) genStack.amount());

                        var extracted = Math.min(genStack.amount(), 1000);
                        var inserted = cap.fill(
                                new FluidStack(fluid.getFluid(), (int) extracted), IFluidHandler.FluidAction.EXECUTE);
                        var endAmount = genStack.amount() - inserted;
                        if (endAmount > 0) {
                            tank.setStack(index, new GenericStack(genStack.what(), genStack.amount() - inserted));
                        } else {
                            tank.setStack(index, null);
                        }

                        setCarriedItem(stack);

                        if (inserted > 0) {
                            playAudioCues(new FluidTankClientAudioPacket(false));
                        }
                    }
                } else {
                    if (!canInsertInto(index) || cap.getFluidInTank(0).isEmpty()) return;

                    var fluid = cap.getFluidInTank(0);
                    var genStack = GenericStack.fromFluidStack(fluid);
                    if (genStack != null && genStack.what() != null) {

                        if (!cap.drain(
                                        (int) tank.insert(index, genStack.what(), 1000, Actionable.SIMULATE),
                                        IFluidHandler.FluidAction.SIMULATE)
                                .isEmpty()) {

                            var extracted = cap.drain(1000, IFluidHandler.FluidAction.EXECUTE)
                                    .getAmount();
                            var inserted = tank.insert(index, genStack.what(), extracted, Actionable.MODULATE);
                            if (extracted - inserted > 0) {
                                cap.fill(
                                        new FluidStack(fluid.getFluid(), (int) (extracted - inserted)),
                                        IFluidHandler.FluidAction.EXECUTE);
                            }

                            setCarriedItem(stack);

                            playAudioCues(new FluidTankClientAudioPacket(true));
                        }
                    }
                }
            });
        }
    }
}
