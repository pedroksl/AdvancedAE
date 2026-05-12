package net.pedroksl.advanced_ae.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.gui.StockExportBusMenu;

import appeng.api.config.RedstoneMode;
import appeng.api.config.SchedulingMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.Color;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ButtonToolTips;
import appeng.core.localization.Tooltips;

public class StockExportBusScreen<M extends StockExportBusMenu> extends UpgradeableScreen<M> {

    private final SettingToggleButton<RedstoneMode> redstoneMode;
    private final SettingToggleButton<YesNo> craftMode;
    private final SettingToggleButton<SchedulingMode> schedulingMode;

    public StockExportBusScreen(M menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.redstoneMode = new ServerSettingToggleButton<>(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.addToLeftToolbar(this.redstoneMode);

        if ((menu.getHost()).getConfigManager().hasSetting(Settings.CRAFT_ONLY)) {
            this.craftMode = new ServerSettingToggleButton<>(Settings.CRAFT_ONLY, YesNo.NO);
            this.addToLeftToolbar(this.craftMode);
        } else {
            this.craftMode = null;
        }

        if ((menu.getHost()).getConfigManager().hasSetting(Settings.SCHEDULING_MODE)) {
            this.schedulingMode = new ServerSettingToggleButton<>(Settings.SCHEDULING_MODE, SchedulingMode.DEFAULT);
            this.addToLeftToolbar(this.schedulingMode);
        } else {
            this.schedulingMode = null;
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.redstoneMode.set((this.menu).getRedStoneMode());
        this.redstoneMode.setVisibility(this.menu.hasUpgrade(AEItems.REDSTONE_CARD));

        if (this.craftMode != null) {
            this.craftMode.set(this.menu.getCraftingMode());
            this.craftMode.setVisibility(this.menu.hasUpgrade(AEItems.CRAFTING_CARD));
        }

        if (this.schedulingMode != null) {
            this.schedulingMode.set(this.menu.getSchedulingMode());
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (Minecraft.getInstance().options.keyPickItem.matchesMouse(event)) {
            Slot slot = getSlotUnderMouse();
            if (this.isValidSlot(slot)) {
                this.menu.openAmountMenu(slot.index);
                return true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void drawTooltip(GuiGraphicsExtractor guiGraphics, int x, int y, List<Component> lines) {
        if (this.menu.getCarried().isEmpty() && this.isValidSlot(this.hoveredSlot)) {
            lines.addAll(this.getTooltipFromContainerItem(this.hoveredSlot.getItem()));

            GenericStack unwrapped = GenericStack.fromItemStack(this.hoveredSlot.getItem());
            if (unwrapped != null) {
                lines.add(Tooltips.getAmountTooltip(ButtonToolTips.Amount, unwrapped));
            }

            lines.add(Tooltips.getSetAmountTooltip());
        } else {
            super.drawTooltip(guiGraphics, x, y, lines);
        }
    }

    @Override
    public void drawFG(GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
        var poseStack = guiGraphics.pose();
        poseStack.pushMatrix();
        poseStack.translate(10.0F, 17.0F);
        poseStack.scale(0.6F);
        Color color = this.style.getColor(PaletteColor.DEFAULT_TEXT_COLOR);
        guiGraphics.text(this.font, AAEText.SetAmountButtonHint.text(), 0, 0, color.toARGB(), false);
        poseStack.popMatrix();
    }

    private boolean isValidSlot(Slot slot) {
        return slot != null && slot.isActive() && slot.hasItem() && this.menu.isConfigSlot(slot);
    }
}
