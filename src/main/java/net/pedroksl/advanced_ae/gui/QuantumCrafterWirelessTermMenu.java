package net.pedroksl.advanced_ae.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.pedroksl.advanced_ae.common.helpers.QuantumCrafterWirelessTermMenuHost;

import appeng.api.storage.ITerminalHost;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.SlotSemantics;
import appeng.menu.ToolboxMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;

import de.mari_023.ae2wtlib.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wut.ItemWUT;

public class QuantumCrafterWirelessTermMenu extends QuantumCrafterTermMenu {

    public static final MenuType<QuantumCrafterWirelessTermMenu> TYPE = MenuTypeBuilder.create(
                    QuantumCrafterWirelessTermMenu::new, QuantumCrafterWirelessTermMenuHost.class)
            .build("aae_wireless_quantum_crafter_terminal");

    private final QuantumCrafterWirelessTermMenuHost host;
    private final ToolboxMenu toolboxMenu;

    public QuantumCrafterWirelessTermMenu(int id, Inventory playerInventory, QuantumCrafterWirelessTermMenuHost host) {
        super(QuantumCrafterWirelessTermMenu.TYPE, id, playerInventory, host, true);
        this.host = host;
        this.toolboxMenu = new ToolboxMenu(this);
        IUpgradeInventory upgrades = this.host.getUpgrades();

        for (int i = 0; i < upgrades.size(); i++) {
            var slot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.UPGRADES, upgrades, i);
            slot.setNotDraggable();
            addSlot(slot, SlotSemantics.UPGRADE);
        }

        addSlot(
                new RestrictedInputSlot(
                        RestrictedInputSlot.PlacableItemType.QE_SINGULARITY,
                        this.host.getSubInventory(WTMenuHost.INV_SINGULARITY),
                        0),
                AE2wtlibSlotSemantics.SINGULARITY);
    }

    @Override
    public void broadcastChanges() {
        toolboxMenu.tick();
        super.broadcastChanges();
    }

    public boolean isWUT() {
        return this.host.getItemStack().getItem() instanceof ItemWUT;
    }

    public ITerminalHost getHost() {
        return this.host;
    }

    public ToolboxMenu getToolbox() {
        return toolboxMenu;
    }
}
