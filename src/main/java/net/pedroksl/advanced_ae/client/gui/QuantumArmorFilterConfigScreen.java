package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.gui.QuantumArmorFilterConfigMenu;

import appeng.client.gui.style.ScreenStyle;

public class QuantumArmorFilterConfigScreen extends QuantumArmorFilterScreen<QuantumArmorFilterConfigMenu> {

    public QuantumArmorFilterConfigScreen(
            QuantumArmorFilterConfigMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
