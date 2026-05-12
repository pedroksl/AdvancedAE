package net.pedroksl.advanced_ae.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
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
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.menu.upgradeType == UpgradeType.AUTO_STOCK) {
            if (Minecraft.getInstance().options.keyPickItem.matchesMouse(event)) {
                Slot slot = getSlotUnderMouse();
                if (this.isValidSlot(slot)) {
                    this.menu.openAmountMenu(slot.index);
                    return true;
                }
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

    private boolean isValidSlot(Slot slot) {
        return slot != null && slot.isActive() && slot.hasItem() && this.menu.isConfigSlot(slot);
    }
}
