package net.pedroksl.advanced_ae.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEActionItems;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEServerSettingToggleButton;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEToolbarActionButton;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.gui.QuantumCrafterMenu;

import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AECheckbox;
import appeng.client.gui.widgets.IconButton;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import appeng.core.localization.Tooltips;
import appeng.menu.SlotSemantics;

public class QuantumCrafterScreen extends UpgradeableScreen<QuantumCrafterMenu> {

    private final SettingToggleButton<RedstoneMode> redstoneMode;
    private final AAEServerSettingToggleButton<YesNo> showOnTerminal;
    private final AAEServerSettingToggleButton<YesNo> meExportBtn;
    private final AAEToolbarActionButton outputConfigure;

    private final List<Button> configButtons = new ArrayList<>();
    private final List<AECheckbox> enableButtons = new ArrayList<>();
    private final InvalidPatternAlert invalidPatternAlert;

    private final List<Boolean> invalidPatternSlots;

    public QuantumCrafterScreen(
            QuantumCrafterMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.redstoneMode = new ServerSettingToggleButton<>(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        addToLeftToolbar(this.redstoneMode);

        this.showOnTerminal = new AAEServerSettingToggleButton<>(AAESettings.QUANTUM_CRAFTER_TERMINAL, YesNo.YES);
        this.addToLeftToolbar(this.showOnTerminal);

        this.meExportBtn = new AAEServerSettingToggleButton<>(AAESettings.ME_EXPORT, YesNo.NO);
        this.addToLeftToolbar(this.meExportBtn);

        this.outputConfigure =
                new AAEToolbarActionButton(AAEActionItems.DIRECTIONAL_OUTPUT, btn -> menu.configureOutput());
        this.outputConfigure.setVisibility(getMenu().getMeExport() == YesNo.NO);
        this.addToLeftToolbar(this.outputConfigure);

        var patternSlots = menu.getSlots(SlotSemantics.MACHINE_INPUT);
        invalidPatternSlots = new ArrayList<>(Collections.nCopies(patternSlots.size(), Boolean.FALSE));
        for (int i = 0; i < patternSlots.size(); i++) {
            var index = i;
            var cfgButton = new QuantumCrafterScreen.ConfigButton(b -> menu.configPattern(index));
            widgets.add("cfgButton" + (1 + i), cfgButton);
            configButtons.add(cfgButton);

            var enableButton = widgets.addCheckbox(
                    "enableButton" + (1 + i), Component.empty(), () -> menu.toggleEnablePattern(index));
            enableButton.setRadio(true);
            enableButton.setTooltip(Tooltip.create(AAEText.EnablePatternButton.text()));
            enableButtons.add(enableButton);
        }

        this.invalidPatternAlert = new InvalidPatternAlert(style.getImage("invalidPatternAlert"));
        this.invalidPatternAlert.setTooltip(Tooltip.create(Tooltips.of(
                AAEText.InvalidPattern.text().withStyle(Tooltips.RED),
                Component.literal("\n")
                        .append(AAEText.InvalidPatternDetails.text())
                        .withStyle(Tooltips.NORMAL_TOOLTIP_TEXT))));
        this.widgets.add("invalidPatternAlert", this.invalidPatternAlert);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.redstoneMode.set(this.menu.getRedStoneMode());
        this.redstoneMode.setVisibility(menu.hasUpgrade(AEItems.REDSTONE_CARD));
        this.showOnTerminal.set(getMenu().getShowOnTerminal());
        this.meExportBtn.set(getMenu().getMeExport());
        this.outputConfigure.setVisibility(getMenu().getMeExport() == YesNo.NO);
        this.invalidPatternAlert.visible = this.invalidPatternSlots.contains(true);
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);

        for (var x = 0; x < this.invalidPatternSlots.size(); x++) {
            if (this.invalidPatternSlots.get(x)) {
                Slot slot = menu.getSlots(SlotSemantics.MACHINE_INPUT).get(x);
                AEBaseScreen.renderSlotHighlight(guiGraphics, slot.x + offsetX, slot.y + offsetY, 0, 0x7fff0000);
            }
        }
    }

    public void updateInvalidButtons(List<Boolean> invalidPatterns) {
        for (var x = 0; x < this.invalidPatternSlots.size(); x++) {
            if (invalidPatterns.size() > x) {
                this.invalidPatternSlots.set(x, invalidPatterns.get(x));
            }
        }
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

            setDisableBackground(true);
            setMessage(AAEText.ConfigurePatternButton.text());
        }

        @Override
        protected Icon getIcon() {
            return isHoveredOrFocused() ? Icon.COG : Icon.COG_DISABLED;
        }
    }

    private static class InvalidPatternAlert extends AbstractWidget {

        private final Blitter invalidPatternAlert;

        public InvalidPatternAlert(Blitter alert) {
            super(0, 0, 18, 18, Component.empty());
            this.invalidPatternAlert = alert;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
            this.invalidPatternAlert.dest(this.getX(), this.getY()).blit(guiGraphics);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    }
}
