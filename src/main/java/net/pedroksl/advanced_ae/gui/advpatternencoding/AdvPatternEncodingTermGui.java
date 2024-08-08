package net.pedroksl.advanced_ae.gui.advpatternencoding;

import appeng.client.gui.style.ScreenStyle;
import appeng.menu.me.items.PatternEncodingTermMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AdvPatternEncodingTermGui extends AdvPatternEncodingTermScreen {

	public AdvPatternEncodingTermGui(PatternEncodingTermMenu menu, Inventory playerInventory,
	                                 Component title, ScreenStyle style) {
		super(menu, playerInventory, title, style);
	}
}
