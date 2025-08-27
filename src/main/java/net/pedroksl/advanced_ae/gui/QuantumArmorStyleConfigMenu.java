package net.pedroksl.advanced_ae.gui;

import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.definitions.AAESlotSemantics;
import net.pedroksl.advanced_ae.common.inventory.QuantumArmorMenuHost;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;

import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.guisync.GuiSync;
import appeng.menu.locator.MenuLocator;
import appeng.menu.slot.DisabledSlot;

public class QuantumArmorStyleConfigMenu extends AEBaseMenu implements ISubMenu {

    private final QuantumArmorMenuHost host;

    @GuiSync(13)
    public int slotIndex;

    private static final String SELECT_SLOT = "select_slot";

    public static final String LAST_SLOT_INDEX = "aae$lastSlotIndex";

    public QuantumArmorStyleConfigMenu(int id, Inventory playerInventory, QuantumArmorMenuHost host) {
        super(AAEMenus.QUANTUM_ARMOR_STYLE_CONFIG.get(), id, playerInventory, host);
        this.host = host;

        int indexOfFirstQuantum = -1;
        for (int i = 3; i >= 0; i--) {
            var index = Inventory.INVENTORY_SIZE + i;
            var slot = new DisabledSlot(playerInventory, index);
            if (slot.getItem().getItem() instanceof QuantumArmorBase) {
                if (indexOfFirstQuantum == -1) {
                    indexOfFirstQuantum = index;
                }
            }
            this.addSlot(slot, AAESlotSemantics.ARMOR);
        }

        registerClientAction(SELECT_SLOT, Integer.class, this::setSelectedItemSlot);
        if (getPlayer().getPersistentData().contains(LAST_SLOT_INDEX)) {
            setSelectedItemSlot(getPlayer().getPersistentData().getInt(LAST_SLOT_INDEX));
        } else {
            setSelectedItemSlot(indexOfFirstQuantum);
        }
    }

    @Override
    public QuantumArmorMenuHost getHost() {
        return host;
    }

    public static void open(ServerPlayer player, MenuLocator locator, int slotIndex) {
        MenuOpener.open(AAEMenus.QUANTUM_ARMOR_STYLE_CONFIG.get(), player, locator);

        if (player.containerMenu instanceof QuantumArmorStyleConfigMenu cca) {
            cca.setSlotIndex(Math.abs(slotIndex - player.getInventory().getContainerSize() - 3));
            cca.broadcastChanges();
        }
    }

    public void setSlotIndex(int index) {
        this.slotIndex = index;
    }

    public void setSelectedItemSlot(int index) {
        if (isClientSide()) {
            sendClientAction(SELECT_SLOT, index);
            getPlayer().getPersistentData().putInt(LAST_SLOT_INDEX, index);
            return;
        }

        this.host.setSelectedItemSlot(index);
    }

    public void updateItemColors(List<Integer> slots, int color) {
        for (var slotIndex : slots) {
            var slot = getSlot(slotIndex);
            if (slot != null && slot.hasItem() && slot.getItem().getItem() instanceof QuantumArmorBase armor) {
                armor.setTintColor(this.getPlayerInventory().player, slot.getItem(), color);
            }
        }
    }
}
