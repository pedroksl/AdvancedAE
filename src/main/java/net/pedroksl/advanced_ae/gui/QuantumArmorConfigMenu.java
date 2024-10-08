package net.pedroksl.advanced_ae.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.definitions.AAESlotSemantics;
import net.pedroksl.advanced_ae.common.inventory.QuantumArmorMenuHost;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.advanced_ae.common.items.upgrades.QuantumUpgradeBaseItem;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.DisabledSlot;

public class QuantumArmorConfigMenu extends AEBaseMenu implements ISubMenuHost, IProgressProvider {

    @GuiSync(2)
    public int maxProcessingTime;

    @GuiSync(3)
    public int processingTime = -1;

    private final QuantumArmorMenuHost<?> host;

    private final Slot inputSlot;

    private static final String SELECT_SLOT = "select_slot";

    public QuantumArmorConfigMenu(int id, Inventory playerInventory, QuantumArmorMenuHost<?> host) {
        super(AAEMenus.QUANTUM_ARMOR_CONFIG, id, playerInventory, host);
        this.host = host;
        this.createPlayerInventorySlots(playerInventory);

        this.inputSlot = this.addSlot(new UpgradeSlot(host.getInventory(), 0), SlotSemantics.MACHINE_INPUT);

        int indexOfFirstQuantum = -1;
        for (int i = 3; i >= 0; i--) {
            var index = Inventory.INVENTORY_SIZE + i;
            var slot = new DisabledSlot(playerInventory, index);
            if (indexOfFirstQuantum == -1 && slot.getItem().getItem() instanceof QuantumArmorBase) {
                indexOfFirstQuantum = index;
            }
            this.addSlot(slot, AAESlotSemantics.ARMOR);
        }

        this.host.setSelectedItemSlot(indexOfFirstQuantum);

        maxProcessingTime = this.host.getMaxProcessingTime();
        this.host.setProgressChangedHandler(this::progressChanged);
        this.host.setInventoryChangedHandler(this::onChangeInventory);

        registerClientAction(SELECT_SLOT, Integer.class, this::setSelectedItemSlot);
    }

    private void progressChanged(int progress) {
        this.processingTime = progress;
    }

    private void onChangeInventory(InternalInventory inv, int slot) {}

    @Override
    public void returnToMainMenu(Player player, ISubMenu iSubMenu) {
        this.host.returnToMainMenu(player, iSubMenu);
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return this.host.getItemStack();
    }

    @Override
    public int getCurrentProgress() {
        return this.processingTime;
    }

    @Override
    public int getMaxProgress() {
        return this.maxProcessingTime;
    }

    public boolean isArmorSlot(Slot slot) {
        return this.getSlots(AAESlotSemantics.ARMOR).contains(slot);
    }

    public void setSelectedItemSlot(int index) {
        if (isClientSide()) {
            sendClientAction(SELECT_SLOT, index);
            return;
        }

        this.host.setSelectedItemSlot(index);
    }

    public int getSelectedSlotIndex() {
        return this.host.getSelctedSlotIndex();
    }

    private static class UpgradeSlot extends AppEngSlot {
        public UpgradeSlot(InternalInventory inv, int invSlot) {
            super(inv, invSlot);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (stack.getItem() instanceof QuantumUpgradeBaseItem upgrade) {
                return upgrade.getType() != UpgradeType.EMPTY;
            }
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return true;
        }
    }
}
