package net.pedroksl.advanced_ae.client.gui;

import appeng.api.config.ActionItems;
import appeng.api.config.CopyMode;
import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.Icon;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ActionButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.client.gui.widgets.ToggleButton;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.gui.PortableWorkbenchMenu;

import java.util.ArrayList;
import java.util.List;

public class PortableWorkbenchScreen extends UpgradeableScreen<PortableWorkbenchMenu> {

    private final ToggleButton copyMode;

    private final SettingToggleButton<FuzzyMode> fuzzyMode;

    public PortableWorkbenchScreen(
            PortableWorkbenchMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.fuzzyMode = addToLeftToolbar(
                new SettingToggleButton<>(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL, this::toggleFuzzyMode));
        this.addToLeftToolbar(new ActionButton(ActionItems.WRENCH, act -> menu.partition()));
        this.addToLeftToolbar(new ActionButton(ActionItems.CLOSE, act -> menu.clear()));
        this.copyMode = this.addToLeftToolbar(new ToggleButton(
                Icon.COPY_MODE_ON,
                Icon.COPY_MODE_OFF,
                GuiText.CopyMode.text(),
                GuiText.CopyModeDesc.text(),
                act -> menu.nextWorkBenchCopyMode()));
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.copyMode.setState(this.menu.getCopyMode() == CopyMode.CLEAR_ON_REMOVE);

        boolean hasFuzzy = menu.getCachedUpgrades().isInstalled(AEItems.FUZZY_CARD);
        this.fuzzyMode.set(menu.getFuzzyMode());
        this.fuzzyMode.setVisibility(hasFuzzy);
    }

    private void toggleFuzzyMode(SettingToggleButton<FuzzyMode> button, boolean backwards) {
        var fz = button.getNextValue(backwards);
        menu.setCellFuzzyMode(fz);
    }
}
