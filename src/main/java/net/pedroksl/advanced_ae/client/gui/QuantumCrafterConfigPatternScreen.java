package net.pedroksl.advanced_ae.client.gui;

import java.util.ArrayList;
import java.util.HashMap;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.client.gui.widgets.NumberTextField;
import net.pedroksl.advanced_ae.gui.QuantumCrafterConfigPatternMenu;
import net.pedroksl.advanced_ae.network.packet.SetStockAmountPacket;

import appeng.api.stacks.AEKey;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.AESubScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.Scrollbar;
import appeng.client.guidebook.document.LytRect;
import appeng.client.guidebook.render.SimpleRenderContext;
import appeng.core.AppEng;
import appeng.menu.slot.FakeSlot;

public class QuantumCrafterConfigPatternScreen extends AEBaseScreen<QuantumCrafterConfigPatternMenu> {

    private static final int ROW_HEIGHT = 18;
    private static final int SLOT_SIZE = ROW_HEIGHT;
    private static final int ROW_SPACING = 2;
    private static final int VISIBLE_ROWS = 4;

    private static final int LIST_ANCHOR_X = 18;
    private static final int LIST_ANCHOR_Y = 25;
    private static final int OUTPUT_X = 18;
    private static final int OUTPUT_Y = 110;

    private static final Rect2i SLOT_BBOX = new Rect2i(176, 0, SLOT_SIZE, SLOT_SIZE);
    private final ResourceLocation DEFAULT_TEXTURE = AppEng.makeId("textures/guis/pattern_config.png");

    private final Scrollbar scrollbar;
    private final ArrayList<InputRow> rows = new ArrayList<>();
    private InputRow outputRow;

    public QuantumCrafterConfigPatternScreen(
            QuantumCrafterConfigPatternMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        AESubScreen.addBackButton(menu, "back", widgets);

        this.scrollbar = widgets.addScrollBar("scrollbar", Scrollbar.SMALL);
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        this.menu.slots.removeIf(slot -> slot instanceof FakeSlot);
        this.rows.forEach(row -> row.textField.setVisible(false));

        final int scrollLevel = scrollbar.getCurrentScroll();
        int visibleRows = Math.min(VISIBLE_ROWS, this.rows.size());
        int i = 0;
        for (; i < visibleRows; ++i) {
            int currentRow = scrollLevel + i;
            if (currentRow >= this.rows.size()) {
                break;
            }

            var x = LIST_ANCHOR_X + 1;
            var y = LIST_ANCHOR_Y + 1 + i * (ROW_HEIGHT + ROW_SPACING);

            InputRow row = this.rows.get(currentRow);
            var renderContext = new SimpleRenderContext(LytRect.empty(), guiGraphics);
            renderContext.renderItem(row.key().wrapForDisplayOrFilter(), x, y, 16, 16);

            row.textField.setRectangle(80, 16, this.leftPos + x + 24, this.topPos + y + 4);
            row.textField.setVisible(true);
        }

        if (outputRow != null) {
            var x = OUTPUT_X + 1;
            var y = OUTPUT_Y + 1;

            var renderContext = new SimpleRenderContext(LytRect.empty(), guiGraphics);
            renderContext.renderItem(outputRow.key().wrapForDisplayOrFilter(), x, y, 16, 16);

            outputRow.textField.setRectangle(80, 16, offsetX + x + 24, offsetY + y + 4);
            outputRow.textField.setVisible(true);
        }
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);

        int currentX = offsetX + LIST_ANCHOR_X;
        int currentY = offsetY + LIST_ANCHOR_Y;

        int visibleRows = Math.min(VISIBLE_ROWS, this.rows.size());
        for (int i = 0; i < visibleRows; ++i) {
            guiGraphics.blit(
                    DEFAULT_TEXTURE,
                    currentX,
                    currentY,
                    SLOT_BBOX.getX(),
                    SLOT_BBOX.getY(),
                    SLOT_BBOX.getWidth(),
                    SLOT_BBOX.getHeight());
            currentY += ROW_HEIGHT + ROW_SPACING;
        }

        guiGraphics.blit(
                DEFAULT_TEXTURE,
                offsetX + OUTPUT_X,
                offsetY + OUTPUT_Y,
                SLOT_BBOX.getX(),
                SLOT_BBOX.getY(),
                SLOT_BBOX.getWidth(),
                SLOT_BBOX.getHeight());
    }

    public void update(HashMap<AEKey, Long> inputs, Pair<AEKey, Long> output) {
        if (inputs.size() != this.rows.size()) {
            this.rows.forEach(row -> this.removeWidget(row.textField));
            this.rows.clear();

            for (var entry : inputs.entrySet()) {
                var textField = addNewTextField(entry.getValue(), this.rows.size());
                this.rows.add(new InputRow(
                        entry.getKey(), textField, Component.empty().append("Keep:")));
            }
        } else {
            for (var row : this.rows) {
                row.textField.setLongValue(inputs.get(row.key));
                row.textField.setFocused(false);
            }
        }

        if (this.outputRow == null) {
            var textField = addNewTextField(output.getSecond(), -1);
            this.outputRow =
                    new InputRow(output.getFirst(), textField, Component.empty().append("Keep:"));
        } else {
            this.outputRow.textField.setLongValue(output.getSecond());
            this.outputRow.textField.setFocused(false);
        }

        resetScrollbar();
    }

    private void resetScrollbar() {
        scrollbar.setHeight(VISIBLE_ROWS * ROW_HEIGHT + (VISIBLE_ROWS - 1) * ROW_SPACING - 2);
        scrollbar.setRange(0, this.rows.size() - VISIBLE_ROWS, 2);
    }

    public record InputRow(AEKey key, NumberTextField textField, Component label) {}

    private NumberTextField addNewTextField(long value, int index) {
        NumberTextField searchField = new NumberTextField(this.style, 0, 0, 0, 0, amount -> {
            if (index >= 0) {
                PacketDistributor.sendToServer(new SetStockAmountPacket(index, amount));
            } else {
                menu.setMaxCrafted(amount);
            }
        });
        searchField.setLongValue(value);
        this.addRenderableWidget(searchField);
        return searchField;
    }
}
