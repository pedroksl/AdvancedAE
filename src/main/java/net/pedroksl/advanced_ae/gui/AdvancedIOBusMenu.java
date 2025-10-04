package net.pedroksl.advanced_ae.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.parts.StockExportBusPart;

import appeng.menu.MenuOpener;

public class AdvancedIOBusMenu extends StockExportBusMenu {
    public AdvancedIOBusMenu(int id, Inventory ip, StockExportBusPart host) {
        super(AAEMenus.ADVANCED_IO_BUS.get(), id, ip, host);
    }

    @Override
    public void returnFromSetAmountMenu() {
        Player player = getPlayerInventory().player;
        if (player instanceof ServerPlayer serverPlayer) {
            MenuOpener.open(AAEMenus.ADVANCED_IO_BUS.get(), serverPlayer, getLocator(), true);
        }
    }
}
