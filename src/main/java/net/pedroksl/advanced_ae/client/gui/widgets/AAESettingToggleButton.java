package net.pedroksl.advanced_ae.client.gui.widgets;

import java.util.*;
import java.util.function.Predicate;

import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.api.ShowQuantumCrafters;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.ae2addonlib.client.widgets.AddonSettingToggleButton;

import appeng.api.config.*;

public class AAESettingToggleButton<T extends Enum<T>> extends AddonSettingToggleButton<T> {

    public AAESettingToggleButton(Setting<T> setting, T val) {
        super(setting, val);
    }

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
        registerApp(
                AAEIcon.CRAFTER_TERMINAL_VISIBLE,
                AAESettings.TERMINAL_SHOW_QUANTUM_CRAFTERS,
                ShowQuantumCrafters.VISIBLE,
                AAEText.ShowCraftersCategory,
                AAEText.ShowVisibleCrafters);
        registerApp(
                AAEIcon.CRAFTER_TERMINAL_ALL,
                AAESettings.TERMINAL_SHOW_QUANTUM_CRAFTERS,
                ShowQuantumCrafters.ALL,
                AAEText.ShowCraftersCategory,
                AAEText.ShowAllCrafters);
        registerApp(
                AAEIcon.CRAFTER_TERMINAL_NOT_FULL,
                AAESettings.TERMINAL_SHOW_QUANTUM_CRAFTERS,
                ShowQuantumCrafters.NOT_FULL,
                AAEText.ShowCraftersCategory,
                AAEText.ShowNonFullCrafters);
        registerApp(
                AAEIcon.SHOW_ON_CRAFTER_TERMINAL,
                AAESettings.QUANTUM_CRAFTER_TERMINAL,
                YesNo.YES,
                AAEText.CrafterTerminalSetting,
                AAEText.ShowOnCrafterTerminal);
        registerApp(
                AAEIcon.HIDE_ON_CRAFTER_TERMINAL,
                AAESettings.QUANTUM_CRAFTER_TERMINAL,
                YesNo.NO,
                AAEText.CrafterTerminalSetting,
                AAEText.HideOnCrafterTerminal);
        registerApp(
                AAEIcon.REGULATE_ON,
                AAESettings.REGULATE_STOCK,
                YesNo.YES,
                AAEText.CrafterTerminalSetting,
                AAEText.HideOnCrafterTerminal);
        registerApp(
                AAEIcon.REGULATE_OFF,
                AAESettings.REGULATE_STOCK,
                YesNo.NO,
                AAEText.CrafterTerminalSetting,
                AAEText.ShowOnCrafterTerminal);
    }
}
