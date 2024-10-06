package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.gui.QuantumArmorConfigMenu;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.Scrollbar;

public class QuantumArmorConfigScreen extends AEBaseScreen<QuantumArmorConfigMenu> {

    private final Scrollbar scrollbar;

    public QuantumArmorConfigScreen(
            QuantumArmorConfigMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.scrollbar = widgets.addScrollBar("scrollbar", Scrollbar.SMALL);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
    }
}
