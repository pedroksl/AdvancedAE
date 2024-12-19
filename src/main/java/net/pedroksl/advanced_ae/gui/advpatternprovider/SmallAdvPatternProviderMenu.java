package net.pedroksl.advanced_ae.gui.advpatternprovider;

import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;

public class SmallAdvPatternProviderMenu extends AdvPatternProviderMenu {

    public SmallAdvPatternProviderMenu(int id, Inventory playerInventory, AdvPatternProviderLogicHost host) {
        super(AAEMenus.SMALL_ADV_PATTERN_PROVIDER.get(), id, playerInventory, host);
    }
}
