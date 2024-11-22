package net.pedroksl.advanced_ae.client;

import appeng.api.config.CpuSelectionMode;
import appeng.api.config.Settings;
import appeng.client.gui.me.crafting.CraftingCPUScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.Scrollbar;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.gui.quantumcomputer.AdvCpuSelectionList;
import net.pedroksl.advanced_ae.gui.quantumcomputer.QuantumComputerMenu;

public class QuantumComputerScreen extends CraftingCPUScreen<QuantumComputerMenu> {

    private final SettingToggleButton<CpuSelectionMode> selectionMode;

    public QuantumComputerScreen(
            QuantumComputerMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.selectionMode = new ServerSettingToggleButton<>(Settings.CPU_SELECTION_MODE, CpuSelectionMode.ANY);
        addToLeftToolbar(this.selectionMode);

        var scrollbar = widgets.addScrollBar("selectCpuScrollbar", Scrollbar.DEFAULT);
        widgets.add("selectCpuList", new AdvCpuSelectionList(menu, scrollbar, style));
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.selectionMode.set(this.menu.getSelectionMode());
    }

    @Override
    protected Component getGuiDisplayName(Component in) {
        return in; // the cpu name is on the button
    }
}
