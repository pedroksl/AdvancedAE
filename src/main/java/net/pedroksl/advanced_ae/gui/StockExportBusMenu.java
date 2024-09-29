package net.pedroksl.advanced_ae.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.parts.StockExportBusPart;

import appeng.api.stacks.GenericStack;
import appeng.core.definitions.AEItems;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.UpgradeableMenu;

public class StockExportBusMenu extends UpgradeableMenu<StockExportBusPart> {

    private static final String OPEN_AMOUNT_MENU = "open_amount_menu";

    public StockExportBusMenu(int id, Inventory ip, StockExportBusPart host) {
        this(AAEMenus.STOCK_EXPORT_BUS, id, ip, host);
    }

    public StockExportBusMenu(
            MenuType<? extends StockExportBusMenu> menuType, int id, Inventory ip, StockExportBusPart host) {
        super(menuType, id, ip, host);

        registerClientAction(OPEN_AMOUNT_MENU, Integer.class, this::openAmountMenu);
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

    public void openAmountMenu(int slotIndex) {
        if (isClientSide()) {
            sendClientAction(OPEN_AMOUNT_MENU, slotIndex);
            return;
        }
        var slot = this.getSlot(slotIndex);

        GenericStack currentStack = GenericStack.fromItemStack(slot.getItem());
        if (currentStack != null) {
            var locator = getLocator();
            if (locator != null && isServerSide()) {
                SetAmountMenu.open(
                        ((ServerPlayer) this.getPlayer()),
                        getLocator(),
                        currentStack,
                        (newStack) -> PacketDistributor.sendToServer(new InventoryActionPacket(
                                InventoryAction.SET_FILTER, slot.index, GenericStack.wrapInItemStack(newStack))));
            }
        }
    }
}
