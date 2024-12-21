package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEIcon;
import net.pedroksl.advanced_ae.client.gui.widgets.AAESlider;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEToggleButton;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.gui.QuantumArmorMagnetMenu;

import appeng.client.gui.style.ScreenStyle;

public class QuantumArmorMagnetScreen extends QuantumArmorFilterScreen<QuantumArmorMagnetMenu> {

    private final AAEToggleButton toggleButton;
    private final AAESlider slider;
    private boolean sliderInitialized = false;

    public QuantumArmorMagnetScreen(
            QuantumArmorMagnetMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        var settings = UpgradeType.MAGNET.getSettings();
        this.slider = new AAESlider(
                settings.minValue,
                settings.maxValue,
                0,
                settings.multiplier,
                value -> this.menu.setCurrentValue((int) Math.round(value / settings.multiplier)));
        this.widgets.add("slider", this.slider);

        this.toggleButton = new AAEToggleButton(AAEIcon.BLACKLIST, AAEIcon.WHITELIST, this.menu::setBlacklist);
        this.widgets.add("blacklist", this.toggleButton);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.toggleButton.setState(this.menu.blacklist);

        if (!sliderInitialized && this.menu.currentValue != -1) {
            this.slider.setValue(this.menu.currentValue);
            sliderInitialized = true;
        }
    }
}
