package net.pedroksl.advanced_ae.mixins.appflux;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.menu.AEBaseMenu;
import com.glodblock.github.appflux.util.helpers.IUpgradableMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvPatternProviderMenu.class)
public abstract class MixinAdvPatternProviderMenu extends AEBaseMenu implements IUpgradableMenu {

	@Final
	@Shadow(remap = false)
	protected AdvPatternProviderLogic logic;

	@Inject(
			method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;" +
					"Lnet/pedroksl/advanced_ae/common/logic/AdvPatternProviderLogicHost;)V",
			at = @At(value = "INVOKE", target = "Lnet/pedroksl/advanced_ae/gui/advpatternprovider/AdvPatternProviderMenu;" +
					"createPlayerInventorySlots(Lnet/minecraft/world/entity/player/Inventory;)V"),
			remap = false
	)
	private void initToolbox(MenuType menuType, int id, Inventory playerInventory, AdvPatternProviderLogicHost host,
	                         CallbackInfo ci) {
		this.setupUpgrades(((IUpgradeableObject) host).getUpgrades());
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

	public MixinAdvPatternProviderMenu(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
		super(menuType, id, playerInventory, host);
	}

}