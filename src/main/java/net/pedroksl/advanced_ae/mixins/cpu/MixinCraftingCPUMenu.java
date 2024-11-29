package net.pedroksl.advanced_ae.mixins.cpu;

import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.pedroksl.advanced_ae.common.cluster.AdvCraftingCPU;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;
import net.pedroksl.advanced_ae.common.logic.AdvCraftingCPULogic;

import appeng.api.config.CpuSelectionMode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.core.sync.packets.CraftingStatusPacket;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.menu.AEBaseMenu;
import appeng.menu.me.common.IncrementalUpdateHelper;
import appeng.menu.me.crafting.CraftingCPUMenu;
import appeng.menu.me.crafting.CraftingStatus;
import appeng.menu.me.crafting.CraftingStatusEntry;

@Mixin(value = CraftingCPUMenu.class, remap = false)
public class MixinCraftingCPUMenu extends AEBaseMenu {

    @Final
    @Shadow
    private IncrementalUpdateHelper incrementalUpdateHelper;

    @Shadow
    private CraftingCPUCluster cpu;

    @Unique
    private AdvCraftingCPU advancedAE$advCpu = null;

    @Final
    @Shadow
    private Consumer<AEKey> cpuChangeListener;

    @Shadow
    public CpuSelectionMode schedulingMode;

    @Shadow
    public boolean cantStoreItems;

    @Shadow
    protected void setCPU(ICraftingCPU c) {}

    @Inject(
            method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;"
                    + "Ljava/lang/Object;)V",
            at = @At("TAIL"))
    private void onInit(MenuType<?> menuType, int id, Inventory ip, Object te, CallbackInfo ci) {
        if (te instanceof AdvCraftingBlockEntity advEntity) {
            var cluster = advEntity.getCluster();
            if (cluster == null) return;

            var active = cluster.getActiveCPUs();
            if (!active.isEmpty()) {
                this.setCPU(active.get(0));
            } else {
                this.setCPU(cluster.getRemainingCapacityCPU());
            }
        }
    }

    @Inject(method = "setCPU(Lappeng/api/networking/crafting/ICraftingCPU;)V", at = @At("HEAD"), cancellable = true)
    private void onSetCPU(ICraftingCPU c, CallbackInfo ci) {
        if (this.advancedAE$advCpu != null) {
            this.advancedAE$advCpu.craftingLogic.removeListener(cpuChangeListener);
        }

        if (c instanceof AdvCraftingCPU advCPU) {
            // Clear old cpu listener if it's still valid
            if (this.cpu != null) {
                this.cpu.craftingLogic.removeListener(cpuChangeListener);
            }

            // Clear helper inside the if statement because it will be cleared normally otherwise
            incrementalUpdateHelper.reset();

            this.advancedAE$advCpu = advCPU;

            // Initially send all items as a full-update to the client when the CPU changes
            var allItems = new KeyCounter();
            this.advancedAE$advCpu.craftingLogic.getAllItems(allItems);
            for (var entry : allItems) {
                incrementalUpdateHelper.addChange(entry.getKey());
            }

            this.advancedAE$advCpu.craftingLogic.addListener(cpuChangeListener);

            ci.cancel();
        } else {
            this.advancedAE$advCpu = null;
        }
    }

    @Inject(method = "cancelCrafting", at = @At("TAIL"))
    public void cancelCrafting(CallbackInfo ci) {
        if (!isClientSide()) {
            if (this.advancedAE$advCpu != null) {
                this.advancedAE$advCpu.cancelJob();
            }
        }
    }

    @Inject(method = "removed", at = @At("TAIL"))
    public void removed(Player player, CallbackInfo ci) {
        if (this.advancedAE$advCpu != null) {
            this.advancedAE$advCpu.craftingLogic.removeListener(cpuChangeListener);
        }
    }

    @Inject(method = "broadcastChanges", at = @At(value = "HEAD"))
    public void broadcastChanges(CallbackInfo ci) {
        if (isServerSide() && this.advancedAE$advCpu != null) {
            this.schedulingMode = this.advancedAE$advCpu.getSelectionMode();
            this.cantStoreItems = this.advancedAE$advCpu.craftingLogic.isCantStoreItems();

            if (this.incrementalUpdateHelper.hasChanges()) {
                CraftingStatus status =
                        advancedAE$create(this.incrementalUpdateHelper, this.advancedAE$advCpu.craftingLogic);
                this.incrementalUpdateHelper.commitChanges();

                sendPacketToClient(new CraftingStatusPacket(status));
            }
        }
    }

    @Unique
    private static CraftingStatus advancedAE$create(IncrementalUpdateHelper changes, AdvCraftingCPULogic logic) {
        boolean full = changes.isFullUpdate();

        ImmutableList.Builder<CraftingStatusEntry> newEntries = ImmutableList.builder();
        for (var what : changes) {
            long storedCount = logic.getStored(what);
            long activeCount = logic.getWaitingFor(what);
            long pendingCount = logic.getPendingOutputs(what);

            var sentStack = what;
            if (!full && changes.getSerial(what) != null) {
                // The item was already sent to the client, so we can skip the item stack
                sentStack = null;
            }

            var entry = new CraftingStatusEntry(
                    changes.getOrAssignSerial(what), sentStack, storedCount, activeCount, pendingCount);
            newEntries.add(entry);

            if (entry.isDeleted()) {
                changes.removeSerial(what);
            }
        }

        long elapsedTime = logic.getElapsedTimeTracker().getElapsedTime();
        long remainingItems = logic.getElapsedTimeTracker().getRemainingItemCount();
        long startItems = logic.getElapsedTimeTracker().getStartItemCount();

        return new CraftingStatus(full, elapsedTime, remainingItems, startItems, newEntries.build());
    }

    public MixinCraftingCPUMenu(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }
}
