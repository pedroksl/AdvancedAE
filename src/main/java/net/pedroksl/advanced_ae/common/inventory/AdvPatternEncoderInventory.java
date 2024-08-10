package net.pedroksl.advanced_ae.common.inventory;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.inventories.InternalInventory;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AdvPatternEncoderInventory extends ItemMenuHost implements InternalInventoryHost {

	private final AppEngInternalInventory patternInv = new AppEngInternalInventory(this, 1);
	private final AppEngInternalInventory targetInv = new AppEngInternalInventory(this, 1);

	public AdvPatternEncoderInventory(Player player, @Nullable Integer slot, ItemStack itemStack) {
		super(player, slot, itemStack);
		var itemTag = this.getItemStack().getTag();
		if (itemTag != null) {
			this.patternInv.readFromNBT(itemTag, "patternInv");
			this.targetInv.readFromNBT(itemTag, "targetInv");
		}
	}

	@Override
	public void saveChanges() {
		var itemTag = this.getItemStack().getOrCreateTag();
		this.patternInv.writeToNBT(itemTag, "patternInv");
		this.targetInv.writeToNBT(itemTag, "targetInv");
	}

	@Override
	public void onChangeInventory(InternalInventory inv, int slot) {
		var itemTag = this.getItemStack().getOrCreateTag();
		if (this.patternInv == inv) {
			this.patternInv.writeToNBT(itemTag, "patternInv");
		}
		if (this.targetInv == inv) {
			this.targetInv.writeToNBT(itemTag, "targetInv");
		}
	}

	public AppEngInternalInventory getInventoryByName(String name) {
		return switch (name) {
			case "patternInv" -> this.patternInv;
			case "targetInv" -> this.targetInv;
			default -> null;
		};
	}
}