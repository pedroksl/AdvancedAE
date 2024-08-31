package net.pedroksl.advanced_ae.mixins.cpu;

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
import appeng.me.service.CraftingService;
import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.CompoundTag;
import net.pedroksl.advanced_ae.common.cluster.AdvCraftingCPU;
import net.pedroksl.advanced_ae.common.cluster.AdvCraftingCPUCluster;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Mixin(value = CraftingService.class, remap = false)
public class MixinCraftingService {

    @Unique
    private static final Comparator<AdvCraftingCPUCluster> FAST_FIRST_COMPARATOR = Comparator.comparingInt(
                    AdvCraftingCPUCluster::getCoProcessors)
            .reversed()
            .thenComparingLong(AdvCraftingCPUCluster::getAvailableStorage);

    @Unique
    private final Set<AdvCraftingCPUCluster> advancedAE$advCraftingCPUClusters = new HashSet<>();

    @Unique
    private long advancedAE$latestChange;

    @Shadow
    private long lastProcessedCraftingLogicChangeTick;

    @Final
    @Shadow
    private Set<CraftingCPUCluster> craftingCPUClusters;

    @Final
    @Shadow
    private IGrid grid;

    @Final
    @Shadow
    private IEnergyService energyGrid;

    @Shadow
    private boolean updateList;

    @Shadow
    public void addLink(CraftingLink link) {}

    @Shadow
    private void updateCPUClusters() {}

    @ModifyConstant(
            method = "onServerEndTick",
            constant = @Constant(longValue = 0, ordinal = 0)
    )
    private long injectLatestChanged(long value) {
        return advancedAE$latestChange;
    }

    @Inject(method = "onServerEndTick", at = @At("HEAD"))
    private void tickAdvClusters(CallbackInfo ci) {
        if (this.updateList) {
            this.updateList = false;
            this.updateCPUClusters();
            lastProcessedCraftingLogicChangeTick = -1; // Ensure caches below are also updated
        }

        advancedAE$latestChange = 0;
        for (var cluster : this.advancedAE$advCraftingCPUClusters) {
            for (var cpu : cluster.getActiveCPUs()) {
                cpu.craftingLogic.tickCraftingLogic(energyGrid, (CraftingService) (Object) this);
                advancedAE$latestChange = Math.max(
                        advancedAE$latestChange,
                        cpu.craftingLogic.getLastModifiedOnTick());
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

    /**
     * @author Pedroksl
     * @reason Add Advanced CPU Clusters to this method
     */
    @Overwrite
    public long insertIntoCpus(AEKey what, long amount, Actionable type) {
        long inserted = 0;
        for (var cpu : this.craftingCPUClusters) {
            inserted += cpu.craftingLogic.insert(what, amount - inserted, type);
        }

        for (var cluster : this.advancedAE$advCraftingCPUClusters) {
            for (var cpu : cluster.getActiveCPUs()) {
                inserted += cpu.craftingLogic.insert(what, amount - inserted, type);
            }
        }
        return inserted;
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
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void onSubmitJob(
            ICraftingPlan job,
            ICraftingRequester requestingMachine,
            ICraftingCPU target,
            boolean prioritizePower,
            IActionSource src,
            CallbackInfoReturnable<ICraftingSubmitResult> cir,
            CraftingCPUCluster cpuCluster,
            MutableObject<UnsuitableCpus> unsuitableCpusResult) {
        if (target instanceof AdvCraftingCPU advCpu) {
            cir.setReturnValue(advCpu.craftingLogic.trySubmitJob(this.grid, job, src, requestingMachine));
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

        return validCpusClusters.get(0);
    }

    @Inject(method = "getCpus", at = @At("RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void onGetCpus(
            CallbackInfoReturnable<ImmutableSet<ICraftingCPU>> cir, ImmutableSet.Builder<ICraftingCPU> cpus) {
        for (var cluster : this.advancedAE$advCraftingCPUClusters) {
            for (var cpu : cluster.getActiveCPUs()) {
                cpus.add(cpu);
            }
            cpus.add(cluster.getRemainingCapacityCPU());
        }
        cir.setReturnValue(cpus.build());
    }

    @Inject(method = "getRequestedAmount", at = @At("RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void onGetRequestedAmount(AEKey what, CallbackInfoReturnable<Long> cir, long requested) {
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
                }
            }
        }
    }
}
