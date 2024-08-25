package net.pedroksl.advanced_ae.common.cluster;

import java.util.*;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;

import appeng.api.config.CpuSelectionMode;
import appeng.api.config.Settings;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.*;
import appeng.api.networking.events.GridCraftingCpuChange;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.GenericStack;
import appeng.api.util.IConfigManager;
import appeng.blockentity.crafting.CraftingMonitorBlockEntity;
import appeng.crafting.execution.CraftingSubmitResult;
import appeng.crafting.inv.ListCraftingInventory;
import appeng.me.cluster.IAECluster;
import appeng.me.cluster.MBCalculator;
import appeng.me.helpers.MachineSource;

public class AdvCraftingCPUCluster implements IAECluster {

    private final BlockPos boundsMin;
    private final BlockPos boundsMax;

    private HashMap<ICraftingPlan, AdvCraftingCPU> activeCpus = new HashMap<>();
    private AdvCraftingCPU remainingStorageCpu;
    private final List<AdvCraftingBlockEntity> blockEntities = new ArrayList<>();
    private final List<CraftingMonitorBlockEntity> status = new ArrayList<>();
    private final IConfigManager configManager;
    private Component myName = null;
    private boolean isDestroyed = false;
    private long storage = 0;
    private long storageMultiplier = 0;
    private long remainingStorage = 0;
    private MachineSource machineSrc = null;
    private int accelerator = 0;
    private int acceleratorMultiplier = 0;

    public AdvCraftingCPUCluster(BlockPos boundsMin, BlockPos boundsMax) {
        this.boundsMin = boundsMin.immutable();
        this.boundsMax = boundsMax.immutable();

        this.configManager = IConfigManager.builder(this::markDirty)
                .registerSetting(Settings.CPU_SELECTION_MODE, CpuSelectionMode.ANY)
                .build();
    }

    @Override
    public boolean isDestroyed() {
        return this.isDestroyed;
    }

    @Override
    public BlockPos getBoundsMin() {
        return this.boundsMin;
    }

    @Override
    public BlockPos getBoundsMax() {
        return this.boundsMax;
    }

    @Override
    public void updateStatus(boolean b) {
        for (AdvCraftingBlockEntity r : this.blockEntities) {
            r.updateSubType(true);
        }
    }

    @Override
    public void destroy() {
        if (this.isDestroyed) {
            return;
        }
        this.isDestroyed = true;

        boolean ownsModification = !MBCalculator.isModificationInProgress();
        if (ownsModification) {
            MBCalculator.setModificationInProgress(this);
        }
        try {
            updateGridForChangedCpu(null);
        } finally {
            if (ownsModification) {
                MBCalculator.setModificationInProgress(null);
            }
        }
    }

    private void updateGridForChangedCpu(AdvCraftingCPUCluster cluster) {
        var posted = false;
        for (AdvCraftingBlockEntity r : this.blockEntities) {
            final IGridNode n = r.getActionableNode();
            if (n != null && !posted) {
                n.getGrid().postEvent(new GridCraftingCpuChange(n));
                posted = true;
            }

            r.updateStatus(cluster);
        }
    }

    @Override
    public Iterator<AdvCraftingBlockEntity> getBlockEntities() {
        return this.blockEntities.iterator();
    }

    public List<ListCraftingInventory> getInventories() {
        List<ListCraftingInventory> list = new ArrayList<>();
        for (var cpu : this.activeCpus.values()) {
            list.add(cpu.getInventory());
        }
        return list;
    }

    void addBlockEntity(AdvCraftingBlockEntity te) {
        if (this.machineSrc == null || te.isCoreBlock()) {
            this.machineSrc = new MachineSource(te);
        }

        te.setCoreBlock(false);
        te.saveChanges();
        this.blockEntities.addFirst(te);

        //		if (te instanceof CraftingMonitorBlockEntity) {
        //			this.status.add((CraftingMonitorBlockEntity) te);
        //		}
        if (te.getStorageBytes() > 0) {
            this.storage += te.getStorageBytes();
            recalculateRemainingStorage();
        }
        if (te.getStorageMultiplier() > 0) {
            this.storageMultiplier += te.getStorageMultiplier();
            recalculateRemainingStorage();
        }
        if (te.getAcceleratorThreads() > 0) {
            if (te.getAcceleratorThreads() <= 16) {
                this.accelerator += te.getAcceleratorThreads();
            } else {
                throw new IllegalArgumentException("Co-processor threads may not exceed 16 per single unit block.");
            }
        }
        if (te.getAccelerationMultiplier() > 0) {
            this.acceleratorMultiplier += te.getAccelerationMultiplier();
        }
    }

    public void recalculateRemainingStorage() {
        var totalStorage = this.storage;
        if (this.storageMultiplier > 0) totalStorage *= this.storageMultiplier;

        long usedStorage = 0;
        for (var plan : activeCpus.keySet()) {
            usedStorage += plan.bytes();
        }

        this.remainingStorage = totalStorage - usedStorage;
    }

    public void markDirty() {
        this.getCore().saveChanges();
    }

    public void updateOutput(GenericStack finalOutput) {
        var send = finalOutput;

        if (finalOutput != null && finalOutput.amount() <= 0) {
            send = null;
        }

        for (var t : this.status) {
            t.setJob(send);
        }
    }

    public IActionSource getSrc() {
        return Objects.requireNonNull(this.machineSrc);
    }

    private AdvCraftingBlockEntity getCore() {
        if (this.machineSrc == null) {
            return null;
        }
        return (AdvCraftingBlockEntity) this.machineSrc.machine().get();
    }

    @Nullable
    public IGrid getGrid() {
        IGridNode node = getNode();
        return node != null ? node.getGrid() : null;
    }

    public void cancelJobs() {
        for (var plan : activeCpus.keySet()) {
            killCpu(plan);
        }
    }

    public void cancelJob(ICraftingPlan plan) {
        var cpu = activeCpus.get(plan);
        if (cpu != null) {
            killCpu(plan);
        }
    }

    public ICraftingSubmitResult submitJob(
            IGrid grid, ICraftingPlan plan, IActionSource src, ICraftingRequester requestingMachine) {
        // Check that the node is active.
        if (!isActive()) return CraftingSubmitResult.CPU_OFFLINE;
        // Check bytes.
        if (getAvailableStorage() < plan.bytes()) return CraftingSubmitResult.CPU_TOO_SMALL;

        var newCpu = new AdvCraftingCPU(this, plan);

        var submitResult = newCpu.craftingLogic.trySubmitJob(grid, plan, src, requestingMachine);
        if (submitResult.successful()) {
            this.activeCpus.put(plan, newCpu);
            recalculateRemainingStorage();
            updateGridForChangedCpu(this);
        }
        return submitResult;
    }

    private void killCpu(ICraftingPlan plan) {
        var cpu = this.activeCpus.remove(plan);
        cpu.craftingLogic.cancel();
        recalculateRemainingStorage();
        updateGridForChangedCpu(this);
    }

    public List<AdvCraftingCPU> getActiveCPUs() {
        return activeCpus.values().stream().toList();
    }

    public AdvCraftingCPU getRemainingCapacityCPU() {
        if (this.remainingStorageCpu == null
                || this.remainingStorageCpu.getAvailableStorage() != this.remainingStorage) {
            this.remainingStorageCpu = new AdvCraftingCPU(this, this.remainingStorage);
        }
        return this.remainingStorageCpu;
    }

    @Nullable
    public CraftingJobStatus getJobStatus(ICraftingPlan plan) {
        var cpu = activeCpus.get(plan);
        if (cpu != null) {
            return cpu.getJobStatus();
        }
        return null;
    }

    public long getAvailableStorage() {
        return this.remainingStorage;
    }

    public int getCoProcessors() {
        var coprocessors = this.accelerator;
        if (this.acceleratorMultiplier > 0) coprocessors *= this.acceleratorMultiplier;
        return coprocessors;
    }

    public Component getName() {
        return this.myName;
    }

    @Nullable
    public IGridNode getNode() {
        AdvCraftingBlockEntity core = getCore();
        return core != null ? core.getActionableNode() : null;
    }

    public boolean isActive() {
        IGridNode node = getNode();
        return node != null && node.isActive();
    }

    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        for (var cpu : activeCpus.values()) {
            if (cpu != null) {
                cpu.writeToNBT(data, registries);
            }
        }
        this.configManager.writeToNBT(data, registries);
    }

    void done() {
        final AdvCraftingBlockEntity core = this.getCore();

        core.setCoreBlock(true);

        if (core.getPreviousState() != null) {
            this.readFromNBT(core.getPreviousState(), core.getLevel().registryAccess());
            core.setPreviousState(null);
        }

        this.updateName();
    }

    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        for (var cpu : activeCpus.values()) {
            if (cpu != null) {
                cpu.readFromNBT(data, registries);
            }
        }
        this.configManager.readFromNBT(data, registries);
    }

    public void updateName() {
        this.myName = null;
        for (AdvCraftingBlockEntity te : this.blockEntities) {

            if (te.hasCustomName()) {
                if (this.myName != null) {
                    this.myName.copy().append(" ").append(te.getCustomName());
                } else {
                    this.myName = te.getCustomName().copy();
                }
            }
        }
    }

    public Level getLevel() {
        return this.getCore().getLevel();
    }

    public void breakCluster() {
        final AdvCraftingBlockEntity t = this.getCore();

        if (t != null) {
            t.breakCluster();
        }
    }

    public CpuSelectionMode getSelectionMode() {
        return this.configManager.getSetting(Settings.CPU_SELECTION_MODE);
    }

    public IConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Checks if this CPU cluster can be automatically selected for a crafting request by the given action source.
     */
    public boolean canBeAutoSelectedFor(IActionSource source) {
        return switch (getSelectionMode()) {
            case ANY -> true;
            case PLAYER_ONLY -> source.player().isPresent();
            case MACHINE_ONLY -> source.player().isEmpty();
        };
    }

    /**
     * Checks if this CPU cluster is preferred for crafting requests by the given action source.
     */
    public boolean isPreferredFor(IActionSource source) {
        return switch (getSelectionMode()) {
            case ANY -> false;
            case PLAYER_ONLY -> source.player().isPresent();
            case MACHINE_ONLY -> source.player().isEmpty();
        };
    }
}