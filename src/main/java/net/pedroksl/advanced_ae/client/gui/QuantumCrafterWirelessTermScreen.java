package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.gui.QuantumCrafterWirelessTermMenu;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ToolboxPanel;

import de.mari_023.ae2wtlib.api.gui.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.api.terminal.IUniversalTerminalCapable;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;

public class QuantumCrafterWirelessTermScreen extends QuantumCrafterTermScreen<QuantumCrafterWirelessTermMenu>
        implements IUniversalTerminalCapable {

    private final ScrollingUpgradesPanel upgradesPanel;

    public QuantumCrafterWirelessTermScreen(
            QuantumCrafterWirelessTermMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        if (getMenu().isWUT()) {
            addToLeftToolbar(cycleTerminalButton());
        }

        upgradesPanel = addUpgradePanel(widgets, getMenu());
        if (getMenu().getToolbox().isPresent()) {
            widgets.add(
                    "toolbox", new ToolboxPanel(style, getMenu().getToolbox().getName()));
        }
    }

    @Override
    public void init() {
        super.init();
        upgradesPanel.setMaxRows(Math.max(2, getVisibleRows()));
    }

    @Override
    public WTMenuHost getHost() {
        return (WTMenuHost) getMenu().getHost();
    }

    @Override
    public void storeState() {}
}
