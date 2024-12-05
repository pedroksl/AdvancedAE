package net.pedroksl.advanced_ae.mixins.cpu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.llamalad7.mixinextras.sugar.Local;

import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.CompoundTag;
import net.pedroksl.advanced_ae.common.cluster.AdvCraftingCPUCluster;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.*;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.crafting.CraftingLink;
import appeng.crafting.execution.CraftingSubmitResult;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.helpers.InterestManager;
import appeng.me.helpers.StackWatcher;
import appeng.me.service.CraftingService;

@Mixin(value = CraftingService.class, remap = false)
public class MixinCraftingService {

    @Unique
    private static final Comparator<AdvCraftingCPUCluster> FAST_FIRST_COMPARATOR = Comparator.comparingInt(
                    AdvCraftingCPUCluster::getCoProcessors)
            .reversed()
            .thenComparingLong(AdvCraftingCPUCluster::getAvailableStorage);

    @Unique
    private final Set<AdvCraftingCPUCluster> advancedAE$advCraftingCPUClusters = new HashSet<>();

    @Final
    @Shadow
    private IGrid grid;

    @Final
    @Shadow
    private InterestManager<StackWatcher<ICraftingWatcherNode>> interestManager;

    @Final
    @Shadow
    private IEnergyService energyGrid;

    @Final
    @Shadow
    private Set<AEKey> currentlyCrafting;

    @Shadow
    private boolean updateList;

    @Shadow
    public void addLink(CraftingLink link) {}

    @Inject(method = "onServerEndTick", at = @At("TAIL"))
    private void tickAdvClusters(CallbackInfo ci) {
        var previouslyCrafting = this.currentlyCrafting;
        for (var cluster : this.advancedAE$advCraftingCPUClusters) {
            if (cluster != null) {
                for (var cpu : cluster.getActiveCPUs()) {
                    cpu.craftingLogic.tickCraftingLogic(energyGrid, (CraftingService) (Object) this);
                    cpu.craftingLogic.getAllWaitingFor(this.currentlyCrafting);
                }
            }
        }

        // Notify watchers about items no longer being crafted
        var changed = new HashSet<AEKey>();
        changed.addAll(Sets.difference(previouslyCrafting, currentlyCrafting));
        changed.addAll(Sets.difference(currentlyCrafting, previouslyCrafting));
        for (var what : changed) {
            for (var watcher : interestManager.get(what)) {
                watcher.getHost().onRequestChange(what);
            }
            for (var watcher : interestManager.getAllStacksWatchers()) {
                watcher.getHost().onRequestChange(what);
            }
        }
    }

    @Inject(method = "removeNode", at = @At("TAIL"))
    private void onRemoveNode(IGridNode gridNode, CallbackInfo ci) {
        if (gridNode.getOwner() instanceof AdvCraftingBlockEntity) {
            this.updateList = true;
        }
    }

    @Inject(method = "addNode", at = @At("TAIL"))
    private void onAddNode(IGridNode gridNode, CompoundTag savedData, CallbackInfo ci) {
        if (gridNode.getOwner() instanceof AdvCraftingBlockEntity) {
            this.updateList = true;
        }
    }

    @Inject(method = "updateCPUClusters", at = @At("TAIL"))
    private void onUpdateCPUClusters(CallbackInfo ci) {
        this.advancedAE$advCraftingCPUClusters.clear();

        for (var blockEntity : this.grid.getMachines(AdvCraftingBlockEntity.class)) {
            final AdvCraftingCPUCluster cluster = blockEntity.getCluster();
            if (cluster != null) {
                this.advancedAE$advCraftingCPUClusters.add(cluster);

                for (var cpu : cluster.getActiveCPUs()) {
                    ICraftingLink maybeLink = cpu.craftingLogic.getLastLink();
                    if (maybeLink != null) {
                        this.addLink((CraftingLink) maybeLink);
                    }
                }
            }
        }
    }

    @Inject(method = "insertIntoCpus", at = @At("RETURN"), cancellable = true)
    private void onInsertIntoCpus(
            AEKey what,
            long amount,
            Actionable type,
            CallbackInfoReturnable<Long> cir,
            @Local(ordinal = 1) long inserted) {
        for (var cluster : this.advancedAE$advCraftingCPUClusters) {
            if (cluster != null) {
                for (var cpu : cluster.getActiveCPUs()) {
                    inserted += cpu.craftingLogic.insert(what, amount - inserted, type);
                }
            }
        }

        cir.setReturnValue(inserted);
    }

    @Inject(
            method = "submitJob",
            at =
                    @At(
                            value = "INVOKE_ASSIGN",
                            target = "appeng/me/service/CraftingService.findSuitableCraftingCPU "
                                    + "(Lappeng/api/networking/crafting/ICraftingPlan;ZLappeng/api/networking/security/IActionSource;"
                                    + "Lorg/apache/commons/lang3/mutable/MutableObject;)"
                                    + "Lappeng/me/cluster/implementations/CraftingCPUCluster;"),
            cancellable = true)
    private void onSubmitJob(
            ICraftingPlan job,
            ICraftingRequester requestingMachine,
            ICraftingCPU target,
            boolean prioritizePower,
            IActionSource src,
            CallbackInfoReturnable<ICraftingSubmitResult> cir,
            @Local CraftingCPUCluster cpuCluster,
            @Local MutableObject<UnsuitableCpus> unsuitableCpusResult) {
        if (target instanceof AdvCraftingCPUCluster advCpuCluster) {
            cir.setReturnValue(advCpuCluster.submitJob(this.grid, job, src, requestingMachine));
        } else {
            var advCluster = advancedAE$findSuitableAdvCraftingCPU(job, src, unsuitableCpusResult);
            if (advCluster != null) {
                updateList = true;
                cir.setReturnValue(advCluster.submitJob(this.grid, job, src, requestingMachine));
            } else if (cpuCluster == null) {
                var unsuitableCpus = unsuitableCpusResult.getValue();
                // If no CPUs were unsuitable, but we couldn't find one, that means there aren't any
                if (unsuitableCpus == null) {
                    cir.setReturnValue(CraftingSubmitResult.NO_CPU_FOUND);
                } else {
                    cir.setReturnValue(CraftingSubmitResult.noSuitableCpu(unsuitableCpus));
                }
            }
        }
    }

    @Unique
    private AdvCraftingCPUCluster advancedAE$findSuitableAdvCraftingCPU(
            ICraftingPlan job, IActionSource src, MutableObject<UnsuitableCpus> unsuitableCpusResult) {
        var validCpusClusters = new ArrayList<AdvCraftingCPUCluster>(this.advancedAE$advCraftingCPUClusters.size());
        int offline = 0;
        int tooSmall = 0;
        int excluded = 0;

        for (var cluster : this.advancedAE$advCraftingCPUClusters) {
            if (!cluster.isActive()) {
                offline++;
                continue;
            }
            if (cluster.getAvailableStorage() < job.bytes()) {
                tooSmall++;
                continue;
            }
            if (!cluster.canBeAutoSelectedFor(src)) {
                excluded++;
                continue;
            }
            validCpusClusters.add(cluster);
        }

        if (validCpusClusters.isEmpty()) {
            if (offline > 0 || tooSmall > 0 || excluded > 0) {
                unsuitableCpusResult.setValue(new UnsuitableCpus(offline, 0, tooSmall, excluded));
            }
            return null;
        }

        validCpusClusters.sort((a, b) -> {
            // Prioritize sorting by selected mode
            var firstPreferred = a.isPreferredFor(src);
            var secondPreferred = b.isPreferredFor(src);
            if (firstPreferred != secondPreferred) {
                // Sort such that preferred comes first, not preferred second
                return Boolean.compare(secondPreferred, firstPreferred);
            }

            return FAST_FIRST_COMPARATOR.compare(a, b);
        });

        return validCpusClusters.getFirst();
    }

    @Inject(method = "getCpus", at = @At("RETURN"), cancellable = true)
    private void onGetCpus(
            CallbackInfoReturnable<ImmutableSet<ICraftingCPU>> cir, @Local ImmutableSet.Builder<ICraftingCPU> cpus) {
        for (var cluster : this.advancedAE$advCraftingCPUClusters) {
            for (var cpu : cluster.getActiveCPUs()) {
                cpus.add(cpu);
            }
            cpus.add(cluster.getRemainingCapacityCPU());
        }
        cir.setReturnValue(cpus.build());
    }

    @Inject(method = "getRequestedAmount", at = @At("RETURN"), cancellable = true)
    private void onGetRequestedAmount(AEKey what, CallbackInfoReturnable<Long> cir, @Local long requested) {
        for (var cluster : this.advancedAE$advCraftingCPUClusters) {
            for (var cpu : cluster.getActiveCPUs()) {
                requested += cpu.craftingLogic.getWaitingFor(what);
            }
        }

        cir.setReturnValue(requested);
    }

    @Inject(method = "hasCpu", at = @At("HEAD"), cancellable = true)
    private void onHasCpu(ICraftingCPU cpu, CallbackInfoReturnable<Boolean> cir) {
        for (var cluster : this.advancedAE$advCraftingCPUClusters) {
            for (var activeCpu : cluster.getActiveCPUs()) {
                if (activeCpu == cpu) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }
}
