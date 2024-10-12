package net.pedroksl.advanced_ae.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.locator.MenuHostLocator;
import appeng.menu.slot.FakeSlot;
import appeng.util.ConfigInventory;

public class QuantumArmorFilterConfigMenu extends AEBaseMenu implements ISubMenu {

    @GuiSync(7)
    public UpgradeType type;

    public int slotIndex;
    private final ISubMenuHost host;

    private final ConfigInventory inv = ConfigInventory.configStacks(9)
            .changeListener(this::onSlotChanged)
            .allowOverstacking(true)
            .build();

    private final FakeSlot[] slots = new FakeSlot[9];

    private static final String SET_AMOUNT = "set_amount";

    public QuantumArmorFilterConfigMenu(int id, Inventory playerInventory, ISubMenuHost host) {
        super(AAEMenus.QUANTUM_ARMOR_FILTER_CONFIG, id, playerInventory, host);
        this.host = host;

        var wrappedInv = inv.createMenuWrapper();

        for (var x = 0; x < inv.size(); x++) {
            slots[x] = new FakeSlot(wrappedInv, x);
            this.addSlot(slots[x], SlotSemantics.MACHINE_INPUT);
        }

        registerClientAction(SET_AMOUNT, Integer.class, this::setAmount);
    }

    @Override
    public ISubMenuHost getHost() {
        return this.host;
    }

    public static void open(
            ServerPlayer player,
            MenuHostLocator locator,
            int slotIndex,
            List<GenericStack> filterList,
            UpgradeType type) {
        MenuOpener.open(AAEMenus.QUANTUM_ARMOR_FILTER_CONFIG, player, locator);

        if (player.containerMenu instanceof QuantumArmorFilterConfigMenu cca) {
            cca.setSlotIndex(slotIndex);
            cca.setUpgradeType(type);
            cca.setFilterList(filterList);
            cca.broadcastChanges();
        }
    }

    public void setSlotIndex(int index) {
        this.slotIndex = index;
    }

    public void setUpgradeType(UpgradeType type) {
        this.type = type;
    }

    public void setFilterList(List<GenericStack> filterList) {
        for (var x = 0; x < inv.size(); x++) {
            if (x < filterList.size()) {
                var filter = filterList.get(x);
                if (filter != null) {
                    if (filter.what() instanceof AEItemKey key) {
                        var stack = key.toStack();
                        stack.setCount((int) filter.amount());
                        this.slots[x].setFilterTo(stack);
                        continue;
                    }
                }
            }
            this.slots[x].setFilterTo(ItemStack.EMPTY);
        }
    }

    public void onSlotChanged() {
        if (isClientSide()) {
            return;
        }

        List<GenericStack> filterList = new ArrayList<>();
        for (var x = 0; x < inv.size(); x++) {
            var stack = this.slots[x].getDisplayStack();
            if (!stack.isEmpty()) {
                filterList.add(GenericStack.fromItemStack(stack));
            }
        }

        var stack = getPlayer().getInventory().getItem(this.slotIndex);
        if (stack.getItem() instanceof QuantumArmorBase item) {
            if (item.getPossibleUpgrades().contains(this.type)) {
                if (item.hasUpgrade(stack, this.type)) {
                    stack.set(AAEComponents.UPGRADE_FILTER.get(this.type), filterList);
                }
            }
        }
    }

    public void setAmount(int amount) {
        if (isClientSide()) {
            sendClientAction(SET_AMOUNT, amount);
            return;
        }

        // Do something
    }
}
