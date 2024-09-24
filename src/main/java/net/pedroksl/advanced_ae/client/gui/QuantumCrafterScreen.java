package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEActionItems;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEServerSettingToggleButton;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEToolbarActionButton;
import net.pedroksl.advanced_ae.gui.QuantumCrafterMenu;

import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;

public class QuantumCrafterScreen extends UpgradeableScreen<QuantumCrafterMenu> {

    private final SettingToggleButton<RedstoneMode> redstoneMode;
    private final AAEServerSettingToggleButton<YesNo> meExportBtn;
    private final AAEToolbarActionButton outputConfigure;

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
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.redstoneMode.set(this.menu.getRedStoneMode());
        this.redstoneMode.setVisibility(menu.hasUpgrade(AEItems.REDSTONE_CARD));
        this.meExportBtn.set(getMenu().getMeExport());
        this.outputConfigure.setVisibility(getMenu().getMeExport() == YesNo.NO);
    }
}
