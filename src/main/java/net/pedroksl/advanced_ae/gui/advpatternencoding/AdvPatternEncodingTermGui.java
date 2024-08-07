package net.pedroksl.advanced_ae.gui.advpatternencoding;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AdvPatternEncodingTermGui<C extends AdvPatternEncodingTermContainer> extends AEBaseScreen<C> {

	public AdvPatternEncodingTermGui(C menu, Inventory playerInventory, Component title,
	                                 ScreenStyle style) {
		super(menu, playerInventory, title, style);
	}
}
