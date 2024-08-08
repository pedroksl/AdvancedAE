package net.pedroksl.advanced_ae.gui.advpatternencoding;

import appeng.helpers.IPatternTerminalMenuHost;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.items.PatternEncodingTermMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class AdvPatternEncodingTermContainer extends PatternEncodingTermMenu {

	public static final MenuType<PatternEncodingTermMenu> TYPE = MenuTypeBuilder
			.create(PatternEncodingTermMenu::new, IPatternTerminalMenuHost.class)
			.build("adv_pattern_encoding_terminal");

	public AdvPatternEncodingTermContainer(int id, Inventory ip, IPatternTerminalMenuHost host) {
		super(TYPE, id, ip, host, true);
	}

	public AdvPatternEncodingTermContainer(MenuType<?> type, int id, Inventory ip, IPatternTerminalMenuHost host, boolean bindInventory) {
		super(type, id, ip, host, bindInventory);
	}
}
