package net.pedroksl.advanced_ae.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.parts.StockExportBusPart;

import appeng.core.definitions.AEItems;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.UpgradeableMenu;

public class StockExportBusMenu extends UpgradeableMenu<StockExportBusPart> {

    public StockExportBusMenu(int id, Inventory ip, StockExportBusPart host) {
        super(AAEMenus.STOCK_EXPORT_BUS, id, ip, host);
    }

    public StockExportBusMenu(
            MenuType<? extends StockExportBusMenu> menuType, int id, Inventory ip, StockExportBusPart host) {
        super(menuType, id, ip, host);
    }

    @Override
    protected void setupConfig() {
        this.addExpandableConfigSlots((this.getHost()).getConfig(), 2, 9, 5);
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        int upgrades = this.getUpgrades().getInstalledUpgrades(AEItems.CAPACITY_CARD);
        return upgrades > idx;
    }

    public boolean isConfigSlot(Slot slot) {
        return this.getSlots(SlotSemantics.CONFIG).contains(slot);
    }
}
