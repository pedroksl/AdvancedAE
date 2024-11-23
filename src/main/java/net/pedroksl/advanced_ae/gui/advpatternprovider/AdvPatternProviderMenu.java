package net.pedroksl.advanced_ae.gui.advpatternprovider;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.menu.ToolboxMenu;
import com.glodblock.github.appflux.util.helpers.IUpgradableMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import appeng.api.config.LockCraftingMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.stacks.GenericStack;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.helpers.patternprovider.PatternProviderReturnInventory;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.RestrictedInputSlot;
import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;

/**
 * @see appeng.client.gui.implementations.PatternProviderScreen
 */
public class AdvPatternProviderMenu extends AEBaseMenu implements IUpgradableMenu {

	protected final AdvPatternProviderLogic logic;

	private final ToolboxMenu toolbox;

	@GuiSync(3)
	public YesNo blockingMode = YesNo.NO;
	@GuiSync(4)
	public YesNo showInAccessTerminal = YesNo.YES;
	@GuiSync(5)
	public LockCraftingMode lockCraftingMode = LockCraftingMode.NONE;
	@GuiSync(6)
	public LockCraftingMode craftingLockedReason = LockCraftingMode.NONE;
	@GuiSync(7)
	public GenericStack unlockStack = null;

	public AdvPatternProviderMenu(int id, Inventory playerInventory, AdvPatternProviderLogicHost host) {
		this(AAEMenus.ADV_PATTERN_PROVIDER, id, playerInventory, host);
	}

	public AdvPatternProviderMenu(MenuType<? extends AdvPatternProviderMenu> menuType,
	                                 int id, Inventory playerInventory,
	                                 AdvPatternProviderLogicHost host) {
		super(menuType, id, playerInventory, host);
		this.createPlayerInventorySlots(playerInventory);

		this.logic = host.getLogic();

		var patternInv = logic.getPatternInv();
		for (int x = 0; x < patternInv.size(); x++) {
			this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN,
							patternInv, x),
					SlotSemantics.ENCODED_PATTERN);
		}

		// Show first few entries of the return inv
		var returnInv = logic.getReturnInv().createMenuWrapper();
		for (int i = 0; i < PatternProviderReturnInventory.NUMBER_OF_SLOTS; i++) {
			if (i < returnInv.size()) {
				this.addSlot(new AppEngSlot(returnInv, i), SlotSemantics.STORAGE);
			}
		}

		this.toolbox = new ToolboxMenu(this);
		this.setupUpgrades(((IUpgradeableObject) host).getUpgrades());
	}

	@Override
	public void broadcastChanges() {
		if (isServerSide()) {
			blockingMode = logic.getConfigManager().getSetting(Settings.BLOCKING_MODE);
			showInAccessTerminal = logic.getConfigManager().getSetting(Settings.PATTERN_ACCESS_TERMINAL);
			lockCraftingMode = logic.getConfigManager().getSetting(Settings.LOCK_CRAFTING_MODE);
			craftingLockedReason = logic.getCraftingLockedReason();
			unlockStack = logic.getUnlockStack();
		}

		toolbox.tick();

		super.broadcastChanges();
	}

	public GenericStackInv getReturnInv() {
		return logic.getReturnInv();
	}

	public ToolboxMenu getToolbox() {
		return this.toolbox;
	}

	public IUpgradeInventory getUpgrades() {
		return ((IUpgradeableObject) this.logic).getUpgrades();
	}

	public boolean hasUpgrade(ItemLike upgradeCard) {
		return this.getUpgrades().isInstalled(upgradeCard);
	}

	public YesNo getBlockingMode() {
		return blockingMode;
	}

	public LockCraftingMode getLockCraftingMode() {
		return lockCraftingMode;
	}

	public LockCraftingMode getCraftingLockedReason() {
		return craftingLockedReason;
	}

	public GenericStack getUnlockStack() {
		return unlockStack;
	}

	public YesNo getShowInAccessTerminal() {
		return showInAccessTerminal;
	}
}
