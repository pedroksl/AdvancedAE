package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.gui.QuantumCrafterWirelessTermMenu;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.BackgroundPanel;
import appeng.client.gui.widgets.ToolboxPanel;
import appeng.client.gui.widgets.UpgradesPanel;
import appeng.menu.SlotSemantics;

import de.mari_023.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;

public class QuantumCrafterWirelessTermScreen extends QuantumCrafterTermScreen<QuantumCrafterWirelessTermMenu>
        implements IUniversalTerminalCapable {

    public QuantumCrafterWirelessTermScreen(
            QuantumCrafterWirelessTermMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        if (getMenu().isWUT()) {
            addToLeftToolbar(new CycleTerminalButton((btn) -> this.cycleTerminal()));
        }

        this.widgets.add(
                "upgrades",
                new UpgradesPanel(
                        this.getMenu().getSlots(SlotSemantics.UPGRADE),
                        this.getMenu().getHost()));
        if (this.getMenu().getToolbox().isPresent()) {
            this.widgets.add(
                    "toolbox",
                    new ToolboxPanel(style, this.getMenu().getToolbox().getName()));
        }

        this.widgets.add("singularityBackground", new BackgroundPanel(style.getImage("singularityBackground")));
    }

    @Override
    public void storeState() {}
}
