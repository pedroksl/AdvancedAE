package net.pedroksl.advanced_ae.client.gui;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.gui.QuantumArmorFilterConfigMenu;

import appeng.api.stacks.GenericStack;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.AESubScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.localization.ButtonToolTips;
import appeng.core.localization.Tooltips;

public class QuantumArmorFilterScreen<M extends QuantumArmorFilterConfigMenu> extends AEBaseScreen<M> {

    public QuantumArmorFilterScreen(M menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        AESubScreen.addBackButton(menu, "back", widgets);
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        if (this.menu.type == UpgradeType.AUTO_STOCK || this.menu.type == UpgradeType.MAGNET) {
            assert this.minecraft != null;

            if (this.minecraft.options.keyPickItem.matchesMouse(btn)) {
                Slot slot = this.findSlot(xCoord, yCoord);
                if (this.isValidSlot(slot)) {
                    this.menu.openAmountMenu(slot.index);
                }
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

    private boolean isValidSlot(Slot slot) {
        return slot != null && slot.isActive() && slot.hasItem() && this.menu.isConfigSlot(slot);
    }
}
