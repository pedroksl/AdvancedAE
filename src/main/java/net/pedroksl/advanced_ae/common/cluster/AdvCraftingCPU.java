package net.pedroksl.advanced_ae.common.cluster;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.logic.AdvCraftingCPULogic;

import appeng.api.config.CpuSelectionMode;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.CraftingJobStatus;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.GenericStack;
import appeng.crafting.inv.ListCraftingInventory;

public class AdvCraftingCPU implements ICraftingCPU {

    final ICraftingPlan plan;
    private long fakeStorage = 0;
    private final AdvCraftingCPUCluster cluster;
    public final AdvCraftingCPULogic craftingLogic = new AdvCraftingCPULogic(this);
    public GenericStack finalOutput;

    public AdvCraftingCPU(AdvCraftingCPUCluster cluster, ICraftingPlan plan) {
        this.cluster = cluster;
        this.plan = plan;
    }

    protected AdvCraftingCPU(AdvCraftingCPUCluster cluster, long fakeStorage) {
        this.cluster = cluster;
        this.plan = null;
        this.fakeStorage = fakeStorage;
    }

    @Override
    public boolean isBusy() {
        return craftingLogic.hasJob();
    }

    @Override
    public @Nullable CraftingJobStatus getJobStatus() {
        var finalOutput = craftingLogic.getFinalJobOutput();
        if (finalOutput != null) {
            var elapsedTimeTracker = craftingLogic.getElapsedTimeTracker();
            var progress =
                    Math.max(0, elapsedTimeTracker.getStartItemCount() - elapsedTimeTracker.getRemainingItemCount());
            return new CraftingJobStatus(
                    finalOutput, elapsedTimeTracker.getStartItemCount(), progress, elapsedTimeTracker.getElapsedTime());
        } else {
            return null;
        }
    }

    @Override
    public void cancelJob() {
        if (this.plan == null) {
            return;
        }

        craftingLogic.cancel();
        this.cluster.cancelJob(plan);
    }

    @Override
    public long getAvailableStorage() {
        return this.plan != null ? this.plan.bytes() : fakeStorage;
    }

    @Override
    public int getCoProcessors() {
        return cluster.getCoProcessors();
    }

    @Override
    public @Nullable Component getName() {
        return cluster.getName();
    }

    @Override
    public CpuSelectionMode getSelectionMode() {
        return cluster.getSelectionMode();
    }

    public boolean isInventoryEmpty() {
        return this.getInventory().list.isEmpty();
    }

    public void markDirty() {
        cluster.markDirty();
    }

    public boolean isActive() {
        return cluster.isActive();
    }

    public Level getLevel() {
        return cluster.getLevel();
    }

    public IGrid getGrid() {
        return cluster.getGrid();
    }

    public void updateOutput(GenericStack stack) {
        finalOutput = stack;
    }

    public ListCraftingInventory getInventory() {
        return craftingLogic.getInventory();
    }

    public void deactivate() {
        cluster.deactivate(plan);
    }

    public IActionSource getSrc() {
        return cluster.getSrc();
    }

    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        craftingLogic.writeToNBT(data, registries);
    }

    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        craftingLogic.readFromNBT(data, registries);
    }
}
