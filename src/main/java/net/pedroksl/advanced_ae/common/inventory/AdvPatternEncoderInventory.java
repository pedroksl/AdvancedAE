package net.pedroksl.advanced_ae.common.inventory;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.inventories.InternalInventory;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.gui.patternencoder.AdvPatternEncoderContainer;
import org.jetbrains.annotations.Nullable;

public class AdvPatternEncoderInventory extends ItemMenuHost implements InternalInventoryHost {

	private final AppEngInternalInventory inOutInventory = new AppEngInternalInventory(this, 2);
	private AdvPatternEncoderContainer.inventoryChangedHandler invChangeHandler;

	public AdvPatternEncoderInventory(Player player, @Nullable Integer slot, ItemStack itemStack) {
		super(player, slot, itemStack);
	}

	@Override
	public void saveChanges() {
	}

	@Override
	public void onChangeInventory(InternalInventory inv, int slot) {
		invChangeHandler.handleChange(inv, slot);
	}

	public AppEngInternalInventory getInventory() {
		return this.inOutInventory;
	}

	public void setInventoryChangedHandler(AdvPatternEncoderContainer.inventoryChangedHandler handler) {
		invChangeHandler = handler;
	}
}
