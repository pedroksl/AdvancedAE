package net.pedroksl.advanced_ae.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.parts.ImportExportBusPart;

import appeng.core.definitions.AEItems;
import appeng.menu.implementations.UpgradeableMenu;

public class ImportExportBusMenu extends UpgradeableMenu<ImportExportBusPart> {

    public ImportExportBusMenu(int id, Inventory ip, ImportExportBusPart host) {
        this(AAEMenus.IMPORT_EXPORT_BUS.get(), id, ip, host);
    }

    public ImportExportBusMenu(
            MenuType<? extends ImportExportBusMenu> menuType, int id, Inventory ip, ImportExportBusPart host) {
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
}
