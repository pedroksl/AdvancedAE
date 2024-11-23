package net.pedroksl.advanced_ae.client.widgets;

import appeng.client.gui.widgets.ITooltip;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;

public class AAEToggleButton extends AAEIconButton implements ITooltip {

    private final Listener listener;

    private final AAEIcon iconOn;
    private final AAEIcon iconOff;

    private List<Component> tooltipOn = Collections.emptyList();
    private List<Component> tooltipOff = Collections.emptyList();

    private boolean state;

    public AAEToggleButton(AAEIcon on, AAEIcon off, Component displayName, Component displayHint, Listener listener) {
        this(on, off, listener);
        setTooltipOn(List.of(displayName, displayHint));
        setTooltipOff(List.of(displayName, displayHint));
    }

    public AAEToggleButton(AAEIcon on, AAEIcon off, Listener listener) {
        super(null);
        this.iconOn = on;
        this.iconOff = off;
        this.listener = listener;
    }

    public void setTooltipOn(List<Component> lines) {
        this.tooltipOn = lines;
    }

    public void setTooltipOff(List<Component> lines) {
        this.tooltipOff = lines;
    }

    @Override
    public void onPress() {
        this.listener.onChange(!state);
    }

    public void setState(boolean isOn) {
        this.state = isOn;
    }

    protected AAEIcon getIcon() {
        return this.state ? this.iconOn : this.iconOff;
    }

    @Override
    public List<Component> getTooltipMessage() {
        return state ? tooltipOn : tooltipOff;
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return super.isTooltipAreaVisible() && !getTooltipMessage().isEmpty();
    }

    @FunctionalInterface
    public interface Listener {
        void onChange(boolean state);
    }
}
