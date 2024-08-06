package net.pedroksl.advanced_ae.gui.advpatternprovider;

import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;

public class AdvPatternProviderContainer extends AdvPatternProviderMenu {
	public AdvPatternProviderContainer(int id, Inventory playerInventory, AdvPatternProviderLogicHost host) {
		super(id, playerInventory, host);
	}
}
