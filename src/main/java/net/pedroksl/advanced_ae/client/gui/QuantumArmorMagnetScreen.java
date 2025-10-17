package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEIcon;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.gui.QuantumArmorMagnetMenu;
import net.pedroksl.ae2addonlib.client.widgets.AddonSlider;
import net.pedroksl.ae2addonlib.client.widgets.AddonToggleButton;

import appeng.client.gui.style.ScreenStyle;

public class QuantumArmorMagnetScreen extends QuantumArmorFilterScreen<QuantumArmorMagnetMenu> {

    private final AddonToggleButton toggleButton;
    private final AddonSlider slider;
    private boolean sliderInitialized = false;

    public QuantumArmorMagnetScreen(
            QuantumArmorMagnetMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        var settings = UpgradeType.MAGNET.getSettings();
        this.slider = new AddonSlider(
                settings.minValue,
                settings.maxValue,
                0,
                settings.multiplier,
                value -> this.menu.setCurrentValue((int) Math.round(value / settings.multiplier)));
        this.widgets.add("slider", this.slider);

        this.toggleButton = new AddonToggleButton(AAEIcon.BLACKLIST, AAEIcon.WHITELIST, this.menu::setBlacklist);
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
