package net.pedroksl.advanced_ae.gui.advpatternencoding;

import appeng.helpers.IPatternTerminalMenuHost;
import appeng.menu.implementations.MenuTypeBuilder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.pedroksl.advanced_ae.common.parts.AdvPatternEncodingTermPart;

public class AdvPatternEncodingTermContainer extends AdvPatternEncodingTermMenu {

	public static final MenuType<AdvPatternEncodingTermContainer> TYPE = MenuTypeBuilder
			.create(AdvPatternEncodingTermContainer::new, AdvPatternEncodingTermPart.class)
			.build("adv_pattern_encoding_terminal");

	public AdvPatternEncodingTermContainer(int id, Inventory ip, IPatternTerminalMenuHost host) {
		super(TYPE, id, ip, host, true);
	}
}
