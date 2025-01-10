package net.pedroksl.advanced_ae.client.gui.widgets;

import java.util.*;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.common.definitions.AAEText;

import appeng.api.config.*;
import appeng.client.gui.AEBaseScreen;
import appeng.core.localization.ButtonToolTips;
import appeng.core.localization.LocalizationEnum;
import appeng.util.EnumCycler;

public class AAESettingToggleButton<T extends Enum<T>> extends AAEIconButton {
    private static Map<AAESettingToggleButton.EnumPair<?>, AAESettingToggleButton.ButtonAppearance> appearances;
    private final Setting<T> buttonSetting;
    private final AAESettingToggleButton.IHandler<AAESettingToggleButton<T>> onPress;
    private final EnumSet<T> validValues;
    private T currentValue;

    @FunctionalInterface
    public interface IHandler<T extends AAESettingToggleButton<?>> {
        void handle(T button, boolean backwards);
    }

    public AAESettingToggleButton(
            Setting<T> setting, T val, AAESettingToggleButton.IHandler<AAESettingToggleButton<T>> onPress) {
        this(setting, val, t -> true, onPress);
    }

    public AAESettingToggleButton(
            Setting<T> setting,
            T val,
            Predicate<T> isValidValue,
            AAESettingToggleButton.IHandler<AAESettingToggleButton<T>> onPress) {
        super(AAESettingToggleButton::onPress);
        this.onPress = onPress;

        // Build a list of values (in order) that are valid w.r.t. the given predicate
        EnumSet<T> validValues = EnumSet.allOf(val.getDeclaringClass());
        validValues.removeIf(isValidValue.negate());
        validValues.removeIf(s -> !setting.getValues().contains(s));
        this.validValues = validValues;

        this.buttonSetting = setting;
        this.currentValue = val;

        if (appearances == null) {

            appearances = new HashMap<>();
            registerApp(AAEIcon.ME_EXPORT_ON, AAESettings.ME_EXPORT, YesNo.YES, AAEText.MeExport, AAEText.MeExportOn);
            registerApp(AAEIcon.ME_EXPORT_OFF, AAESettings.ME_EXPORT, YesNo.NO, AAEText.MeExport, AAEText.MeExportOff);
            registerApp(
                    AAEIcon.FILTERED_IMPORT_ON,
                    AAESettings.FILTERED_IMPORT,
                    YesNo.YES,
                    AAEText.FilteredImport,
                    AAEText.FilteredImportOn);
            registerApp(
                    AAEIcon.FILTERED_IMPORT_OFF,
                    AAESettings.FILTERED_IMPORT,
                    YesNo.NO,
                    AAEText.FilteredImport,
                    AAEText.FilteredImportOff);
        }
    }

    private static void onPress(Button btn) {
        if (btn instanceof AAESettingToggleButton) {
            ((AAESettingToggleButton<?>) btn).triggerPress();
        }
    }

    private void triggerPress() {
        boolean backwards = false;
        // This isn't great, but we don't get any information about right-clicks
        // otherwise
        Screen currentScreen = Minecraft.getInstance().screen;
        if (currentScreen instanceof AEBaseScreen) {
            backwards = ((AEBaseScreen<?>) currentScreen).isHandlingRightClick();
        }
        onPress.handle(this, backwards);
    }

    private static <T extends Enum<T>> void registerApp(
            AAEIcon icon, Setting<T> setting, T val, LocalizationEnum title, Component... tooltipLines) {
        var lines = new ArrayList<Component>();
        lines.add(title.text());
        Collections.addAll(lines, tooltipLines);

        appearances.put(
                new AAESettingToggleButton.EnumPair<>(setting, val),
                new AAESettingToggleButton.ButtonAppearance(icon, null, lines));
    }

    private static <T extends Enum<T>> void registerApp(
            ItemLike item, Setting<T> setting, T val, LocalizationEnum title, Component... tooltipLines) {
        var lines = new ArrayList<Component>();
        lines.add(title.text());
        Collections.addAll(lines, tooltipLines);

        appearances.put(
                new AAESettingToggleButton.EnumPair<>(setting, val),
                new AAESettingToggleButton.ButtonAppearance(null, item.asItem(), lines));
    }

    private static <T extends Enum<T>> void registerApp(
            AAEIcon icon, Setting<T> setting, T val, LocalizationEnum title, LocalizationEnum hint) {
        registerApp(icon, setting, val, title, hint.text());
    }

    @Nullable
    private AAESettingToggleButton.ButtonAppearance getAppearance() {
        if (this.buttonSetting != null && this.currentValue != null) {
            return appearances.get(new AAESettingToggleButton.EnumPair<>(this.buttonSetting, this.currentValue));
        }
        return null;
    }

    @Override
    protected AAEIcon getIcon() {
        var app = getAppearance();
        if (app != null && app.icon != null) {
            return app.icon;
        }
        return AAEIcon.TOOLBAR_BUTTON_BACKGROUND;
    }

    @Override
    protected Item getItemOverlay() {
        var app = getAppearance();
        if (app != null && app.item != null) {
            return app.item;
        }
        return null;
    }

    public Setting<T> getSetting() {
        return this.buttonSetting;
    }

    public T getCurrentValue() {
        return this.currentValue;
    }

    public void set(T e) {
        if (this.currentValue != e) {
            this.currentValue = e;
        }
    }

    public T getNextValue(boolean backwards) {
        return EnumCycler.rotateEnum(currentValue, backwards, validValues);
    }

    @Override
    public List<Component> getTooltipMessage() {

        if (this.buttonSetting == null || this.currentValue == null) {
            return Collections.emptyList();
        }

        var buttonAppearance =
                appearances.get(new AAESettingToggleButton.EnumPair<>(this.buttonSetting, this.currentValue));
        if (buttonAppearance == null) {
            return Collections.singletonList(ButtonToolTips.NoSuchMessage.text());
        }

        return buttonAppearance.tooltipLines;
    }

    private static final class EnumPair<T extends Enum<T>> {

        final Setting<T> setting;
        final T value;

        public EnumPair(Setting<T> setting, T value) {
            this.setting = setting;
            this.value = value;
        }

        @Override
        public int hashCode() {
            return this.setting.hashCode() ^ this.value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final AAESettingToggleButton.EnumPair<?> other = (AAESettingToggleButton.EnumPair<?>) obj;
            return other.setting == this.setting && other.value == this.value;
        }
    }

    private record ButtonAppearance(@Nullable AAEIcon icon, @Nullable Item item, List<Component> tooltipLines) {}
}
