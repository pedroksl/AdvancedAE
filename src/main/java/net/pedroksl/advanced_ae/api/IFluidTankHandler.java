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

    GenericStackInv getTank();

    boolean canExtractFromTank(int index);

    boolean canInsertInto(int index);

    void playAudioCues(FluidTankClientAudioPacket p);

    default void onItemUse(int index, int button) {
        var stack = getCarriedItem();
        if (!stack.isEmpty()) {
            var cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (cap != null) {

                var tank = getTank();
                if (tank == null) return;

                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    if (!canExtractFromTank(index)) return;

                    var genStack = tank.getStack(index);
                    if (genStack != null && genStack.what() != null) {
                        var fluid = ((AEFluidKey) genStack.what()).toStack((int) genStack.amount());

                        var extracted = tank.extract(index, genStack.what(), 1000, Actionable.MODULATE);
                        var inserted = cap.fill(
                                new FluidStack(fluid.getFluid(), (int) extracted), IFluidHandler.FluidAction.EXECUTE);
                        tank.insert(index, genStack.what(), extracted - inserted, Actionable.MODULATE);

                        playAudioCues(new FluidTankClientAudioPacket(false));
                    }
                } else {
                    if (!canInsertInto(index) || cap.getFluidInTank(0) == FluidStack.EMPTY) return;

                    var fluid = cap.getFluidInTank(0);
                    var genStack = GenericStack.fromFluidStack(fluid);
                    if (genStack != null && genStack.what() != null) {

                        var extracted = cap.drain(1000, IFluidHandler.FluidAction.EXECUTE)
                                .getAmount();
                        var inserted = tank.insert(index, genStack.what(), extracted, Actionable.MODULATE);
                        cap.fill(
                                new FluidStack(fluid.getFluid(), (int) (extracted - inserted)),
                                IFluidHandler.FluidAction.EXECUTE);

                        playAudioCues(new FluidTankClientAudioPacket(true));
                    }
                }
            }
        }
    }
}
