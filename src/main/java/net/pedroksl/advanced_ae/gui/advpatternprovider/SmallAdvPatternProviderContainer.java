package net.pedroksl.advanced_ae.gui.advpatternprovider;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;

import appeng.menu.implementations.MenuTypeBuilder;

public class SmallAdvPatternProviderContainer extends AdvPatternProviderMenu {

    public static final MenuType<SmallAdvPatternProviderContainer> TYPE = MenuTypeBuilder.create(
                    SmallAdvPatternProviderContainer::new, AdvPatternProviderLogicHost.class)
            .build("small_adv_pattern_provider");

    protected SmallAdvPatternProviderContainer(int id, Inventory playerInventory, AdvPatternProviderLogicHost host) {
        super(TYPE, id, playerInventory, host);
    }
}
