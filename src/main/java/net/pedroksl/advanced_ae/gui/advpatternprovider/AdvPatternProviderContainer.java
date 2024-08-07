package net.pedroksl.advanced_ae.gui.advpatternprovider;

import appeng.menu.implementations.MenuTypeBuilder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;

public class AdvPatternProviderContainer extends AdvPatternProviderMenu {

	public static final MenuType<AdvPatternProviderContainer> TYPE = MenuTypeBuilder
			.create(AdvPatternProviderContainer::new, AdvPatternProviderLogicHost.class)
			.build("adv_pattern_provider");

	public AdvPatternProviderContainer(int id, Inventory playerInventory, AdvPatternProviderLogicHost host) {
		super(TYPE, id, playerInventory, host);
	}
}
