package net.pedroksl.advanced_ae.mixins.appflux;

import com.glodblock.github.appflux.util.helpers.IUpgradableMenu;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderMenu;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.menu.AEBaseMenu;
import appeng.menu.ToolboxMenu;

@Mixin(AdvPatternProviderMenu.class)
public abstract class MixinAdvPatternProviderMenu extends AEBaseMenu implements IUpgradableMenu {

    @Final
    @Shadow(remap = false)
    protected AdvPatternProviderLogic logic;

    @Unique
    private ToolboxMenu af_$toolbox;

    @Inject(
            method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;"
                    + "Lnet/pedroksl/advanced_ae/common/logic/AdvPatternProviderLogicHost;)V",
            at = @At("TAIL"),
            remap = false)
    private void initToolbox(
            MenuType menuType, int id, Inventory playerInventory, AdvPatternProviderLogicHost host, CallbackInfo ci) {
        this.af_$toolbox = new ToolboxMenu(this);
        this.setupUpgrades(((IUpgradeableObject) host).getUpgrades());
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public ToolboxMenu getToolbox() {
        return this.af_$toolbox;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public IUpgradeInventory getUpgrades() {
        return ((IUpgradeableObject) this.logic).getUpgrades();
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public boolean hasUpgrade(ItemLike upgradeCard) {
        return getUpgrades().isInstalled(upgradeCard);
    }

    @Inject(method = "broadcastChanges", at = @At("TAIL"))
    public void tickToolbox(CallbackInfo ci) {
        this.af_$toolbox.tick();
    }

    public MixinAdvPatternProviderMenu(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }
}
