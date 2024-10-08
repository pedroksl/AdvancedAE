package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.advanced_ae.gui.QuantumArmorConfigMenu;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ProgressBar;
import appeng.client.gui.widgets.Scrollbar;

public class QuantumArmorConfigScreen extends AEBaseScreen<QuantumArmorConfigMenu> {

    private final Scrollbar scrollbar;
    private final ProgressBar pb;

    private int selectedIndex = -1;

    public QuantumArmorConfigScreen(
            QuantumArmorConfigMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.scrollbar = widgets.addScrollBar("scrollbar", Scrollbar.SMALL);

        this.pb = new ProgressBar(menu, style.getImage("progressBar"), ProgressBar.Direction.VERTICAL);
        this.pb.active = false;
        widgets.add("progressBar", this.pb);
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        assert this.minecraft != null;

        // Handle item selection
        if (this.minecraft.options.keyAttack.matchesMouse(btn)) {
            Slot slot = this.findSlot(xCoord, yCoord);
            if (this.isArmorSlot(slot)) {
                this.selectedIndex = slot.index;
                this.menu.setSelectedItemSlot(slot.getSlotIndex());
            }
        }

        return super.mouseClicked(xCoord, yCoord, btn);
    }

    private boolean isArmorSlot(Slot slot) {
        return slot != null
                && slot.isActive()
                && slot.hasItem()
                && this.menu.isArmorSlot(slot)
                && slot.getItem().getItem() instanceof QuantumArmorBase;
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);

        if (selectedIndex != -1) {
            var slot = this.menu.getSlot(selectedIndex);
            AEBaseScreen.renderSlotHighlight(guiGraphics, slot.x, slot.y, 0, 0x787d53c1);
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        int progress = this.menu.getCurrentProgress() * 100 / this.menu.getMaxProgress();
        if (progress >= 0) this.pb.setFullMsg(Component.literal(progress + "%"));
        else this.pb.setFullMsg(Component.literal(""));
    }

    @Override
    protected void init() {
        super.init();

        var index = this.menu.getSelectedSlotIndex();
        this.selectedIndex = 4 - (index - Inventory.INVENTORY_SIZE) + Inventory.INVENTORY_SIZE;
    }
}
