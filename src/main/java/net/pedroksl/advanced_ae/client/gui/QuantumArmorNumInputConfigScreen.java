package net.pedroksl.advanced_ae.client.gui;

import java.awt.*;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.client.gui.widgets.AAESlider;
import net.pedroksl.advanced_ae.gui.QuantumArmorNumInputConfigMenu;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.AESubScreen;
import appeng.client.gui.style.ScreenStyle;

public class QuantumArmorNumInputConfigScreen extends AEBaseScreen<QuantumArmorNumInputConfigMenu> {

    private final AAESlider slider;

    private float multiplier = 1;
    private boolean sliderInitialized = false;

    public QuantumArmorNumInputConfigScreen(
            QuantumArmorNumInputConfigMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        AESubScreen.addBackButton(menu, "back", widgets);

        this.slider = new AAESlider(value -> this.menu.setCurrentValue((int) Math.round(value / this.multiplier)));
        this.widgets.add("slider", this.slider);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        if (!this.sliderInitialized && menu.type != null) {
            var settings = menu.type.getSettings();

            this.multiplier = settings.multiplier;
            var screenMin = settings.minValue * this.multiplier;
            var screenMax = settings.maxValue * this.multiplier;
            var currentValue = this.menu.currentValue * this.multiplier;
            this.slider.setValues(screenMin, screenMax, currentValue, this.multiplier);
            this.sliderInitialized = true;
        }
    }
}
