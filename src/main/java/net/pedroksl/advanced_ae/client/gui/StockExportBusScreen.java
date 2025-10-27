package net.pedroksl.advanced_ae.client.gui;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.GuiGraphics;
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
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        assert this.minecraft != null;

        if (this.minecraft.options.keyPickItem.matchesMouse(btn)) {
            Slot slot = this.findSlot(xCoord, yCoord);
            if (this.isValidSlot(slot)) {
                this.menu.openAmountMenu(slot.index);
            }
        }

        return super.mouseClicked(xCoord, yCoord, btn);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int x, int y) {
        if (this.menu.getCarried().isEmpty() && this.isValidSlot(this.hoveredSlot)) {
            ArrayList<Component> itemTooltip =
                    new ArrayList<>(this.getTooltipFromContainerItem(this.hoveredSlot.getItem()));
            GenericStack unwrapped = GenericStack.fromItemStack(this.hoveredSlot.getItem());
            if (unwrapped != null) {
                itemTooltip.add(Tooltips.getAmountTooltip(ButtonToolTips.Amount, unwrapped));
            }

            itemTooltip.add(Tooltips.getSetAmountTooltip());
            this.drawTooltip(guiGraphics, x, y, itemTooltip);
        } else {
            super.renderTooltip(guiGraphics, x, y);
        }
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(10.0F, 17.0F, 0.0F);
        poseStack.scale(0.7F, 0.7F, 1.0F);
        Color color = this.style.getColor(PaletteColor.DEFAULT_TEXT_COLOR);
        guiGraphics.drawString(this.font, AAEText.SetAmountButtonHint.text(), 0, 0, color.toARGB(), false);
        poseStack.popPose();
    }

    private boolean isValidSlot(Slot slot) {
        return slot != null && slot.isActive() && slot.hasItem() && this.menu.isConfigSlot(slot);
    }
}
