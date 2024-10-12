package net.pedroksl.advanced_ae.client.gui;

import java.util.function.Consumer;

import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import net.pedroksl.advanced_ae.client.gui.widgets.AAESlider;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.AESubScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.widgets.TabButton;
import appeng.menu.AEBaseMenu;

public class QuantumArmorNumInputConfigScreen<C extends AEBaseMenu, P extends AEBaseScreen<C>>
        extends AESubScreen<C, P> {

    private final ExtendedSlider slider;
    private final Consumer<Integer> setter;
    private final float multiplier;

    public QuantumArmorNumInputConfigScreen(
            P screen, int min, int max, int current, float multiplier, Consumer<Integer> setter) {
        super(screen, "/screens/quantum_armor_num_input_config.json");
        this.setter = setter;
        this.multiplier = multiplier;

        var button = new TabButton(Icon.BACK, screen.getTitle(), btn -> onClose());
        this.widgets.add("back", button);

        var screenMin = min * multiplier;
        var screenMax = max * multiplier;
        var screenCurrent = current * multiplier;
        var precision = (int) Math.log10(1 / multiplier);
        this.slider = new AAESlider(screenMin, screenMax, screenCurrent, multiplier, precision);

        this.widgets.add("slider", this.slider);
    }

    @Override
    public void onClose() {
        var value = this.slider.getValue();
        this.setter.accept((int) Math.round(value / this.multiplier));

        returnToParent();
        super.onClose();
    }
}
