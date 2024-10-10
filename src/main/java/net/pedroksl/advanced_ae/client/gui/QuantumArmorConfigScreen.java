package net.pedroksl.advanced_ae.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.pedroksl.advanced_ae.client.widgets.QuantumUpgradeWidget;
import net.pedroksl.advanced_ae.client.widgets.UpgradeState;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.gui.QuantumArmorConfigMenu;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.Color;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ProgressBar;
import appeng.client.gui.widgets.Scrollbar;
import appeng.core.AppEng;

public class QuantumArmorConfigScreen extends AEBaseScreen<QuantumArmorConfigMenu> {

    private static final int LIST_ANCHOR_X = 30;
    private static final int LIST_ANCHOR_Y = 31;
    private static final int LIST_LINE_HEIGHT = 16;
    private static final int VISIBLE_ROWS = 4;

    private static final Rect2i LIST_BACK_BBOX = new Rect2i(0, 195, 128, 16);
    private static final ResourceLocation DEFAULT_TEXTURE = AppEng.makeId("textures/guis/quantum_armor_config.png");

    private final Scrollbar scrollbar;
    private final ProgressBar pb;

    private int selectedIndex = -1;

    private final List<QuantumUpgradeWidget> upgradeList = new ArrayList<>();

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
                refreshList();
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

        Color color = this.style.getColor(PaletteColor.DEFAULT_TEXT_COLOR);

        final int scrollLevel = scrollbar.getCurrentScroll();
        int visibleRows = Math.min(VISIBLE_ROWS, this.upgradeList.size());
        for (var i = 0; i < visibleRows; ++i) {
            int currentRow = scrollLevel + i;
            if (currentRow >= this.upgradeList.size()) {
                break;
            }
            var upgrade = this.upgradeList.get(currentRow);
            guiGraphics.drawString(
                    this.font, upgrade.getName(), upgrade.getX() + 2, upgrade.getY() + 3, color.toARGB(), false);
        }
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);

        int currentX = offsetX + LIST_ANCHOR_X;
        int currentY = offsetY + LIST_ANCHOR_Y;

        int visibleRows = Math.min(VISIBLE_ROWS, this.upgradeList.size());
        for (int i = 0; i < visibleRows; ++i) {
            guiGraphics.blit(
                    DEFAULT_TEXTURE,
                    currentX,
                    currentY,
                    LIST_BACK_BBOX.getX(),
                    LIST_BACK_BBOX.getY(),
                    LIST_BACK_BBOX.getWidth(),
                    LIST_BACK_BBOX.getHeight());
            currentY += LIST_LINE_HEIGHT;
        }
    }

    public void refreshList() {
        this.upgradeList.forEach(w -> w.children().forEach(this::removeWidget));
        this.upgradeList.clear();
        this.resetScrollbar();

        if (this.selectedIndex == -1) return;

        int index = 0;
        var stack = this.menu.getSlot(selectedIndex).getItem();
        if (stack.getItem() instanceof QuantumArmorBase item) {
            for (var upgrade : item.getPossibleUpgrades()) {
                if (item.hasUpgrade(stack, upgrade)) {
                    var components = stack.getComponents();

                    UpgradeState state;
                    if (upgrade.getSettingType() == UpgradeType.SettingType.NUM_INPUT) {
                        if (!components.has(AAEComponents.UPGRADE_VALUE.get(upgrade))) continue;

                        state = new UpgradeState(
                                upgrade,
                                upgrade.getSettings(),
                                Boolean.TRUE.equals(components.get(AAEComponents.UPGRADE_TOGGLE.get(upgrade))),
                                components.get(AAEComponents.UPGRADE_VALUE.get(upgrade)));

                    } else if (upgrade.getSettingType() == UpgradeType.SettingType.FILTER) {
                        if (!components.has(AAEComponents.UPGRADE_FILTER.get(upgrade))) continue;

                        state = new UpgradeState(
                                upgrade,
                                upgrade.getSettings(),
                                Boolean.TRUE.equals(components.get(AAEComponents.UPGRADE_TOGGLE.get(upgrade))),
                                0,
                                components.get(AAEComponents.UPGRADE_FILTER.get(upgrade)));
                    } else continue;

                    var widget = new QuantumUpgradeWidget(
                            this, index, LIST_ANCHOR_X, LIST_ANCHOR_Y + index * LIST_LINE_HEIGHT, style, state);
                    widget.add();
                    this.upgradeList.add(widget);
                    index++;
                }
            }
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        int progress = this.menu.getCurrentProgress() * 100 / this.menu.getMaxProgress();
        if (progress >= 0) this.pb.setFullMsg(Component.literal(progress + "%"));
        else this.pb.setFullMsg(Component.literal(""));
    }

    public void addChildWidget(String id, AbstractWidget widget, Map<String, AbstractWidget> child) {
        if (widget.isFocused()) widget.setFocused(false);
        widget.setX(widget.getX() + leftPos);
        widget.setY(widget.getY() + topPos);
        child.put(id, widget);
        addRenderableWidget(widget);
    }

    public void requestUninstall(UpgradeType upgradeType) {
        this.menu.requestUninstall(upgradeType);
    }

    @Override
    protected void init() {
        super.init();

        var index = this.menu.getSelectedSlotIndex();
        this.selectedIndex = 4 - (index - Inventory.INVENTORY_SIZE) + Inventory.INVENTORY_SIZE;
        refreshList();
    }

    private void resetScrollbar() {
        scrollbar.setHeight(VISIBLE_ROWS * LIST_LINE_HEIGHT + (VISIBLE_ROWS - 1) * 2 - 2);
        scrollbar.setRange(0, this.upgradeList.size() - VISIBLE_ROWS, 2);
    }
}
