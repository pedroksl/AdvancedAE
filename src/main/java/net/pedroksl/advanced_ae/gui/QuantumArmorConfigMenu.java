package net.pedroksl.advanced_ae.gui;

import appeng.api.inventories.InternalInventory;
import appeng.client.gui.me.common.ClientReadOnlySlot;
import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.DisabledSlot;
import mekanism.common.inventory.container.slot.ArmorSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.definitions.AAESlotSemantics;
import net.pedroksl.advanced_ae.common.inventory.QuantumArmorMenuHost;

import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import net.pedroksl.advanced_ae.common.items.upgrades.QuantumUpgradeBaseItem;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

public class QuantumArmorConfigMenu extends AEBaseMenu implements ISubMenuHost {

    private final QuantumArmorMenuHost<?> host;

    private final Slot inputSlot;

    public QuantumArmorConfigMenu(int id, Inventory playerInventory, QuantumArmorMenuHost<?> host) {
        super(AAEMenus.QUANTUM_ARMOR_CONFIG, id, playerInventory, host);
        this.createPlayerInventorySlots(playerInventory);
        this.host = host;

        this.inputSlot = this.addSlot(new UpgradeSlot(host.getInventory(), 0), SlotSemantics.MACHINE_INPUT);

        for (int i = 3; i >= 0; i--) {
            this.addSlot(new DisabledSlot(playerInventory, Inventory.INVENTORY_SIZE + i), AAESlotSemantics.ARMOR);
        }

        this.host.setInventoryChangedHandler(this::onChangeInventory);
    }

    private void onChangeInventory(InternalInventory inv, int slot) {

    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu iSubMenu) {
        this.host.returnToMainMenu(player, iSubMenu);
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return this.host.getItemStack();
    }

    public interface InventoryChangedHandler {
        void handleChange(InternalInventory inv, int slot);
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
    }
}
