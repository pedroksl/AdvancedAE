package net.pedroksl.advanced_ae.gui.advpatternprovider;

import appeng.client.gui.style.ScreenStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AdvPatternProviderGui extends AdvPatternProviderScreen<AdvPatternProviderMenu> {

	public AdvPatternProviderGui(AdvPatternProviderMenu menu, Inventory playerInventory, Component title,
	                             ScreenStyle style) {
		super(menu, playerInventory, title, style);
	}

}
