package net.pedroksl.advanced_ae.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEIcon;
import net.pedroksl.advanced_ae.common.definitions.AAESlotSemantics;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.helpers.AAEColor;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.advanced_ae.gui.QuantumArmorStyleConfigMenu;
import net.pedroksl.advanced_ae.network.packet.quantumarmor.QuantumArmorStylePacket;
import net.pedroksl.ae2addonlib.client.widgets.AddonIconButton;
import net.pedroksl.ae2addonlib.client.widgets.ColorPicker;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.AESubScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AECheckbox;

public class QuantumArmorStyleConfigScreen extends AEBaseScreen<QuantumArmorStyleConfigMenu> {

    private int selectedIndex = -1;
    private boolean applyToAll = true;

    private final ColorPicker colorPicker;
    private final AECheckbox checkBox;
    private final ConfirmButton confirmButton;

    public QuantumArmorStyleConfigScreen(
            QuantumArmorStyleConfigMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        AESubScreen.addBackButton(menu, "back", widgets);

        this.colorPicker = new ColorPicker(this.widgets::add, 0, style, "colorPicker");
        this.checkBox = this.widgets.addCheckbox("checkBox", AAEText.ApplyToAll.text(), this::toggleApplyToAll);
        this.confirmButton = new ConfirmButton(value -> this.confirm());
        this.widgets.add("confirmButton", this.confirmButton);
    }

    @Override
    protected void init() {
        super.init();
        this.selectedIndex = getMenu().slotIndex;

        int lastColor = -1;
        for (var slot : this.menu.getSlots(AAESlotSemantics.ARMOR)) {
            if (slot.hasItem() && slot.getItem().getItem() instanceof QuantumArmorBase armor) {
                int color = armor.getTintColor(slot.getItem());
                if (lastColor != -1 && lastColor != color) {
                    this.applyToAll = false;
                    break;
                }
                lastColor = color;
            }
        }
        this.checkBox.setSelected(this.applyToAll);

        updateColorPicker();
    }

    public void toggleApplyToAll() {
        this.applyToAll = !this.applyToAll;
    }

    public void updateColorPicker() {
        if (selectedIndex != -1 && selectedIndex < this.menu.slots.size()) {
            var slot = this.menu.getSlot(selectedIndex);
            if (slot.hasItem() && slot.getItem().getItem() instanceof QuantumArmorBase armor) {
                this.colorPicker.setColorAndUpdate(armor.getTintColor(slot.getItem()));
            }
        }
    }

    public void confirm() {
        List<Integer> updateList = new ArrayList<>();

        if (this.applyToAll) {
            for (var slot : this.menu.getSlots(AAESlotSemantics.ARMOR)) {
                if (slot.hasItem() && slot.getItem().getItem() instanceof QuantumArmorBase armor) {
                    updateList.add(slot.index);
                }
            }
        } else {
            var slot = this.menu.getSlot(selectedIndex);
            if (slot.hasItem() && slot.getItem().getItem() instanceof QuantumArmorBase armor) {
                updateList.add(slot.index);
            }
        }

        PacketDistributor.sendToServer(
                new QuantumArmorStylePacket(updateList, this.colorPicker.color().argb()));
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        assert this.minecraft != null;

        // Handle item selection
        if (btn == InputConstants.MOUSE_BUTTON_LEFT) {
            Slot slot = this.findSlot(xCoord, yCoord);
            if (isValidSlot(slot)) {
                if (this.applyToAll) return true;
                this.selectedIndex = slot.index;
                this.menu.setSelectedItemSlot(slot.getSlotIndex());
                updateColorPicker();
            }
        }

        return super.mouseClicked(xCoord, yCoord, btn);
    }

    private boolean isValidSlot(Slot slot) {
        return slot != null
                && slot.isActive()
                && slot.hasItem()
                && slot.getItem().getItem() instanceof QuantumArmorBase;
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);

        if (this.applyToAll) {
            for (var slot : this.menu.getSlots(AAESlotSemantics.ARMOR)) {
                AEBaseScreen.renderSlotHighlight(
                        guiGraphics, slot.x + offsetX, slot.y + offsetY, 0, AAEColor.LIGHT_PURPLE.argb());
            }
        } else if (selectedIndex != -1 && selectedIndex < this.menu.slots.size()) {
            var slot = this.menu.getSlot(selectedIndex);
            AEBaseScreen.renderSlotHighlight(
                    guiGraphics, slot.x + offsetX, slot.y + offsetY, 0, AAEColor.LIGHT_PURPLE.argb());
        }
    }

    static class ConfirmButton extends AddonIconButton {
        public ConfirmButton(OnPress onPress) {
            super(onPress);
        }

        @Override
        protected AAEIcon getIcon() {
            return AAEIcon.CONFIRM;
        }
    }
}
