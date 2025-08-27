package net.pedroksl.advanced_ae.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;

public class QuantumArmorTintMenu extends AEBaseMenu implements ISubMenu {
    public QuantumArmorTintMenu(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Override
    public ISubMenuHost getHost() {
        return null;
    }
}
