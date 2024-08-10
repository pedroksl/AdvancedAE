package net.pedroksl.advanced_ae.gui.patternencoder;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.pedroksl.advanced_ae.common.inventory.AdvPatternEncoderInventory;

public class AdvPatternEncoderContainer extends AEBaseMenu {
	public static final MenuType<AdvPatternEncoderContainer> TYPE = MenuTypeBuilder
			.create(AdvPatternEncoderContainer::new, AdvPatternEncoderInventory.class)
			.build("adv_pattern_encoder");

	public AdvPatternEncoderContainer(int id, Inventory playerInventory, AdvPatternEncoderInventory host) {
		super(TYPE, id, playerInventory, host);
		this.createPlayerInventorySlots(playerInventory);

		var patternInv = host.getInventoryByName("patternInv");
		this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN, patternInv, 0),
				SlotSemantics.ENCODED_PATTERN);
	}
}
