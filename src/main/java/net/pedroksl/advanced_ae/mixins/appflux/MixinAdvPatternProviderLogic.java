package net.pedroksl.advanced_ae.mixins.appflux;

import java.util.List;

import com.glodblock.github.appflux.common.AFItemAndBlock;
import com.glodblock.github.appflux.common.me.service.IEnergyDistributor;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;

import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;

@Mixin(value = AdvPatternProviderLogic.class, remap = false)
public abstract class MixinAdvPatternProviderLogic implements IUpgradeableObject {

    @Final
    @Shadow
    private AdvPatternProviderLogicHost host;

    @Final
    @Shadow
    private IManagedGridNode mainNode;

    @Final
    @Shadow
    private IActionSource actionSource;

    @Unique
    private IUpgradeInventory af_upgrades = UpgradeInventories.empty();

    @Unique
    private EnergyTicker af_ticker;

    @Unique
    private void af_onUpgradesChanged() {
        this.host.saveChanges();
        this.host.getBlockEntity().invalidateCapabilities();
        this.af_ticker.updateSleep();
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.af_upgrades;
    }

    @Inject(
            method =
                    "<init>(Lappeng/api/networking/IManagedGridNode;Lnet/pedroksl/advanced_ae/common/logic/AdvPatternProviderLogicHost;I)V",
            at = @At("TAIL"))
    private void initUpgrade(
            IManagedGridNode mainNode, AdvPatternProviderLogicHost host, int patternInventorySize, CallbackInfo ci) {
        this.af_upgrades =
                UpgradeInventories.forMachine(host.getTerminalIcon().getItem(), 1, this::af_onUpgradesChanged);
        this.af_ticker = new EnergyTicker(
                this.host::getBlockEntity,
                this.host,
                () -> this.af_upgrades.isInstalled(AFItemAndBlock.INDUCTION_CARD),
                this.mainNode,
                this.actionSource);
        this.mainNode.addService(IEnergyDistributor.class, this.af_ticker);
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void saveUpgrade(CompoundTag tag, CallbackInfo ci) {
        this.af_upgrades.writeToNBT(tag, "upgrades");
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void loadUpgrade(CompoundTag tag, CallbackInfo ci) {
        this.af_upgrades.readFromNBT(tag, "upgrades");
    }

    @Inject(method = "addDrops", at = @At("TAIL"))
    private void dropUpgrade(List<ItemStack> drops, CallbackInfo ci) {
        for (var is : this.af_upgrades) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

    @Inject(method = "clearContent", at = @At("TAIL"))
    private void clearUpgrade(CallbackInfo ci) {
        this.af_upgrades.clear();
    }
}
