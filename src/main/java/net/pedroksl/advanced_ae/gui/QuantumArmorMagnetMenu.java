package net.pedroksl.advanced_ae.gui;

import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import appeng.api.stacks.GenericStack;
import appeng.api.storage.ISubMenuHost;
import appeng.menu.MenuOpener;
import appeng.menu.guisync.GuiSync;
import appeng.menu.locator.MenuLocator;

public class QuantumArmorMagnetMenu extends QuantumArmorFilterConfigMenu {

    @GuiSync(8)
    public boolean blacklist;

    @GuiSync(9)
    public int currentValue;

    private static final String SET_BLACKLIST = "set_blacklist";
    private static final String SET_CURRENT_VALUE = "set_current_value";

    public QuantumArmorMagnetMenu(int id, Inventory playerInventory, ISubMenuHost host) {
        super(AAEMenus.QUANTUM_ARMOR_MAGNET.get(), id, playerInventory, host);

        registerClientAction(SET_BLACKLIST, Boolean.class, this::setBlacklist);
        registerClientAction(SET_CURRENT_VALUE, Integer.class, this::setCurrentValue);
    }

    public static void open(
            ServerPlayer player,
            MenuLocator locator,
            int slotIndex,
            List<GenericStack> filterList,
            int currentValue,
            boolean blacklist) {
        MenuOpener.open(AAEMenus.QUANTUM_ARMOR_MAGNET.get(), player, locator);

        if (player.containerMenu instanceof QuantumArmorMagnetMenu cca) {
            cca.setUpgradeType(UpgradeType.MAGNET);
            cca.setSlotIndex(slotIndex);
            cca.setFilterList(filterList);
            cca.setBlacklist(blacklist);
            cca.setCurrentValue(currentValue);
            cca.broadcastChanges();
        }
    }

    public void setBlacklist(boolean blacklist) {
        if (isClientSide()) {
            sendClientAction(SET_BLACKLIST, blacklist);
            return;
        }

        this.blacklist = blacklist;

        var stack = getPlayer().getInventory().getItem(this.slotIndex);
        if (stack.getItem() instanceof QuantumArmorBase item) {
            if (item.getPossibleUpgrades().contains(this.upgradeType)) {
                if (item.hasUpgrade(stack, this.upgradeType)) {
                    item.setUpgradeExtra(stack, this.upgradeType, blacklist);
                }
            }
        }
    }

    public void setCurrentValue(int value) {
        if (isClientSide()) {
            sendClientAction(SET_CURRENT_VALUE, value);
            return;
        }

        this.currentValue = value;

        var stack = getPlayer().getInventory().getItem(this.slotIndex);
        if (stack.getItem() instanceof QuantumArmorBase item) {
            if (item.getPossibleUpgrades().contains(this.upgradeType)) {
                if (item.hasUpgrade(stack, this.upgradeType)) {
                    var currentValue = item.getUpgradeValue(stack, this.upgradeType, -1);
                    if (currentValue == -1 || currentValue != value) {
                        item.setUpgradeValue(stack, this.upgradeType, value);
                    }
                }
            }
        }
    }

    @Override
    public void returnFromSetAmountMenu() {
        List<GenericStack> filterList = makeFilterList();

        Player player = getPlayerInventory().player;
        if (player instanceof ServerPlayer serverPlayer) {
            QuantumArmorMagnetMenu.open(
                    serverPlayer, getLocator(), this.slotIndex, filterList, this.currentValue, this.blacklist);
        }
    }
}
