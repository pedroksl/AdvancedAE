package net.pedroksl.advanced_ae.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.client.widgets.AAEActionItems;
import net.pedroksl.advanced_ae.client.widgets.AAEServerSettingToggleButton;
import net.pedroksl.advanced_ae.client.widgets.AAEToolbarActionButton;
import net.pedroksl.advanced_ae.gui.QuantumCrafterMenu;

import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.Icon;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AECheckbox;
import appeng.client.gui.widgets.IconButton;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import appeng.menu.SlotSemantics;

public class QuantumCrafterScreen extends UpgradeableScreen<QuantumCrafterMenu> {

    private final SettingToggleButton<RedstoneMode> redstoneMode;
    private final AAEServerSettingToggleButton<YesNo> meExportBtn;
    private final AAEToolbarActionButton outputConfigure;

    private final List<Button> configButtons = new ArrayList<>();
    private final List<AECheckbox> enableButtons = new ArrayList<>();

    public QuantumCrafterScreen(
            QuantumCrafterMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.redstoneMode = new ServerSettingToggleButton<>(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        addToLeftToolbar(this.redstoneMode);

        this.meExportBtn = new AAEServerSettingToggleButton<>(AAESettings.ME_EXPORT, YesNo.NO);
        this.addToLeftToolbar(this.meExportBtn);

        this.outputConfigure =
                new AAEToolbarActionButton(AAEActionItems.DIRECTIONAL_OUTPUT, btn -> menu.configureOutput());
        this.outputConfigure.setVisibility(getMenu().getMeExport() == YesNo.NO);
        this.addToLeftToolbar(this.outputConfigure);

        var patternSlots = menu.getSlots(SlotSemantics.MACHINE_INPUT);
        for (int i = 0; i < patternSlots.size(); i++) {
            var cfgButton = new ConfigButton(btn -> {
                var idx = configButtons.indexOf(btn);
                menu.configPattern(idx);
            });
            cfgButton.setDisableBackground(true);
            cfgButton.setMessage(Component.translatable("gui.tooltips.advanced_ae.ConfigurePatternButton"));
            widgets.add("cfgButton" + (1 + i), cfgButton);
            configButtons.add(cfgButton);

            var enableButton = widgets.addCheckbox("enableButton" + (1 + i), Component.empty(), new onEnableToggle(i));
            enableButton.setRadio(true);
            enableButton.setTooltip(
                    Tooltip.create(Component.translatable("gui.tooltips.advanced_ae.EnablePatternButton")));
            enableButtons.add(enableButton);
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.redstoneMode.set(this.menu.getRedStoneMode());
        this.redstoneMode.setVisibility(menu.hasUpgrade(AEItems.REDSTONE_CARD));
        this.meExportBtn.set(getMenu().getMeExport());
        this.outputConfigure.setVisibility(getMenu().getMeExport() == YesNo.NO);
    }

    public void updateEnabledButtons(List<Boolean> enabledButtons) {
        for (var x = 0; x < this.enableButtons.size(); x++) {
            if (enabledButtons.size() > x) {
                this.enableButtons.get(x).setSelected(enabledButtons.get(x));
            }
        }
    }

    static class ConfigButton extends IconButton {
        public ConfigButton(OnPress onPress) {
            super(onPress);
        }

        @Override
        protected Icon getIcon() {
            return isHoveredOrFocused() ? Icon.WRENCH : Icon.WRENCH_DISABLED;
        }
    }

    private class onEnableToggle implements Runnable {
        private final int index;

        onEnableToggle(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            menu.toggleEnablePattern(this.index);
        }
    }
}
