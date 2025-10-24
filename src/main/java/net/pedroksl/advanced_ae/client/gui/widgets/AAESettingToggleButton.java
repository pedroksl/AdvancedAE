package net.pedroksl.advanced_ae.client.gui.widgets;

import java.util.*;
import java.util.function.Predicate;

import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.ae2addonlib.client.widgets.AddonSettingToggleButton;

import appeng.api.config.Setting;
import appeng.api.config.YesNo;

public class AAESettingToggleButton<T extends Enum<T>> extends AddonSettingToggleButton<T> {
    public AAESettingToggleButton(
            Setting<T> setting, T val, AddonSettingToggleButton.IHandler<AddonSettingToggleButton<T>> onPress) {
        super(setting, val, t -> true, onPress);
    }

    public AAESettingToggleButton(
            Setting<T> setting,
            T val,
            Predicate<T> isValidValue,
            AddonSettingToggleButton.IHandler<AddonSettingToggleButton<T>> onPress) {
        super(setting, val, isValidValue, onPress);
    }

    public static <T extends Enum<T>> AAESettingToggleButton<T> serverButton(Setting<T> setting, T val) {
        return AddonSettingToggleButton.serverButton(setting, val, AdvancedAE.MOD_ID, AAESettingToggleButton::new);
    }

    @Override
    protected void registerAppearances() {
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
