package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEServerSettingToggleButton;
import net.pedroksl.advanced_ae.gui.AdvancedIOBusMenu;

import appeng.api.config.YesNo;
import appeng.client.gui.style.ScreenStyle;

public class AdvancedIOBusScreen extends StockExportBusScreen<AdvancedIOBusMenu> {

    private final AAEServerSettingToggleButton<YesNo> regulateButton;

    public AdvancedIOBusScreen(AdvancedIOBusMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.regulateButton = new AAEServerSettingToggleButton<>(AAESettings.REGULATE_STOCK, YesNo.YES);
        this.addToLeftToolbar(this.regulateButton);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.regulateButton.set(getMenu().getRegulate());
    }
}
