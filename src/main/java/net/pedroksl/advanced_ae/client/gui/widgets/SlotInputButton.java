package net.pedroksl.advanced_ae.client.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import appeng.api.stacks.AEKey;

/**
 * Small button showing the target slot configured for the FIRST occurrence of a pattern input — a quick default
 * for the common case where an item only appears once. For repeated ingredients that need to split across
 * multiple slots, use the dedicated "Slots" mode of the Advanced Pattern Encoder screen instead (toggle button
 * next to the title), which has room for proper per-occurrence controls.
 * <p>
 * Click increments the slot; Shift+click (or right click, where it actually reaches this widget instead of
 * being swallowed by the container screen's slot-click handling) decrements it, floored at -1 ("auto", no
 * specific slot).
 */
public class SlotInputButton extends Button {

    private AEKey key;
    private int slot = -1;

    public SlotInputButton(int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, labelFor(-1), onPress, DEFAULT_NARRATION);
    }

    private static Component labelFor(int slot) {
        return Component.literal(slot < 0 ? "-" : String.valueOf(slot));
    }

    public void setKey(AEKey key) {
        this.key = key;
    }

    public AEKey getKey() {
        return key;
    }

    public void setSlot(int slot) {
        this.slot = slot;
        this.setMessage(labelFor(slot));
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && (button == 0 || button == 1) && this.clicked(mouseX, mouseY)) {
            boolean decrement = button == 1 || Screen.hasShiftDown();
            int newSlot = decrement ? Math.max(-1, this.slot - 1) : this.slot + 1;
            this.setSlot(newSlot);
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onPress.onPress(this);
            return true;
        }
        return false;
    }
}
