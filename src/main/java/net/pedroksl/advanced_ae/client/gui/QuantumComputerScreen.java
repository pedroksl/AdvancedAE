package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.gui.quantumcomputer.AdvCpuSelectionList;
import net.pedroksl.advanced_ae.gui.quantumcomputer.QuantumComputerMenu;

import appeng.client.gui.me.crafting.CraftingCPUScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.Scrollbar;

public class QuantumComputerScreen extends CraftingCPUScreen<QuantumComputerMenu> {
    public QuantumComputerScreen(
            QuantumComputerMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        var scrollbar = widgets.addScrollBar("selectCpuScrollbar", Scrollbar.BIG);
        widgets.add("selectCpuList", new AdvCpuSelectionList(menu, scrollbar, style));
    }

    @Override
    protected Component getGuiDisplayName(Component in) {
        return in; // the cpu name is on the button
    }
}
