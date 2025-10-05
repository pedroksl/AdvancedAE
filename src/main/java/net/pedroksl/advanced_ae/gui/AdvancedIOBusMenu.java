package net.pedroksl.advanced_ae.gui;

import appeng.api.config.YesNo;
import appeng.menu.guisync.GuiSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.parts.StockExportBusPart;

import appeng.menu.MenuOpener;

public class AdvancedIOBusMenu extends StockExportBusMenu {

    @GuiSync(2)
    private YesNo regulate;

    public AdvancedIOBusMenu(int id, Inventory ip, StockExportBusPart host) {
        super(AAEMenus.ADVANCED_IO_BUS.get(), id, ip, host);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        this.regulate = getHost().getConfigManager().getSetting(AAESettings.REGULATE_STOCK);
    }

    public YesNo getRegulate() {
        return this.regulate;
    }

    @Override
    public void returnFromSetAmountMenu() {
        Player player = getPlayerInventory().player;
        if (player instanceof ServerPlayer serverPlayer) {
            MenuOpener.open(AAEMenus.ADVANCED_IO_BUS.get(), serverPlayer, getLocator(), true);
        }
    }
}
