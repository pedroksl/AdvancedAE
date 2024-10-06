package net.pedroksl.advanced_ae.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.inventory.QuantumArmorMenuHost;

import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;

public class QuantumArmorConfigMenu extends AEBaseMenu implements ISubMenuHost {
    public QuantumArmorConfigMenu(int id, Inventory playerInventory, QuantumArmorMenuHost host) {
        super(AAEMenus.QUANTUM_ARMOR_CONFIG, id, playerInventory, host);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu iSubMenu) {}

    @Override
    public ItemStack getMainMenuIcon() {
        return null;
    }
}
