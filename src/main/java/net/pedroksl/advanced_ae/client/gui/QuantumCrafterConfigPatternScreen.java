package net.pedroksl.advanced_ae.client.gui;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEIcon;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.gui.QuantumCrafterConfigPatternMenu;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.network.packet.SetStockAmountPacket;
import net.pedroksl.ae2addonlib.client.widgets.AddonIconButton;
import net.pedroksl.ae2addonlib.client.widgets.NumberTextField;

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

    private static final int TEXTFIELD_WIDTH = 60;
    private static final int TEXTFIELD_HEIGHT = 16;

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
        this.rows.forEach(row -> {
            if (row != null) {
                if (!this.renderables.contains(row.textField)) {
                    this.addRenderableWidget(row.textField);
                }
                if (!this.renderables.contains(row.button)) {
                    this.addRenderableOnly(row.button);
                }
                row.textField.setVisible(false);
            }
        });
        if (this.outputRow != null) {
            if (!this.renderables.contains(this.outputRow.textField)) {
                this.addRenderableWidget(this.outputRow.textField);
            }
            if (!this.renderables.contains(outputRow.button)) {
                this.addRenderableOnly(outputRow.button);
            }
        }

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

            x += 37;
            y += 4;
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, row.label, x, y, 0xFFFFFF);

            x += offsetX + 20;
            y += offsetY;
            row.textField.setWidth(TEXTFIELD_WIDTH);
            row.textField.setHeight(TEXTFIELD_HEIGHT);
            row.textField.setX(x);
            row.textField.setY(y);
            row.textField.setVisible(true);

            x += TEXTFIELD_WIDTH + 10;
            y -= 5;
            row.button.setValid(!row.textField.isChanged());
            row.button.setPosition(x, y);
        }

        if (outputRow != null) {
            var x = OUTPUT_X + 1;
            var y = OUTPUT_Y + 1;
            var renderContext = new SimpleRenderContext(LytRect.empty(), guiGraphics);
            renderContext.renderItem(outputRow.key().wrapForDisplayOrFilter(), x, y, 16, 16);

            x += 37;
            y += 4;
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, outputRow.label, x, y, 0xFFFFFF);

            x += offsetX + 20;
            y += offsetY;
            outputRow.textField.setWidth(TEXTFIELD_WIDTH);
            outputRow.textField.setHeight(TEXTFIELD_HEIGHT);
            outputRow.textField.setX(x);
            outputRow.textField.setY(y);
            outputRow.textField.setVisible(true);

            x += TEXTFIELD_WIDTH + 10;
            y -= 5;
            outputRow.button.setValid(!outputRow.textField.isChanged());
            outputRow.button.setPosition(x, y);
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

    public void update(LinkedHashMap<AEKey, Long> inputs, Pair<AEKey, Long> output) {
        if (inputs.size() != this.rows.size()) {
            this.rows.forEach(row -> this.removeWidget(row.textField));
            this.rows.clear();

            for (var entry : inputs.entrySet()) {
                var textField = addNewNumberField(entry.getValue(), this.rows.size());
                this.rows.add(new InputRow(
                        entry.getKey(), textField, Component.empty().append("Keep:"), new ValidButton(btn -> {})));
            }
        } else {
            for (var row : this.rows) {
                row.textField.setFocused(false);
            }
        }

        if (this.outputRow == null) {
            var textField = addNewNumberField(output.getSecond(), -1);
            this.outputRow = new InputRow(
                    output.getFirst(), textField, Component.empty().append("Limit:"), new ValidButton(btn -> {}));
        } else {
            this.outputRow.textField.setFocused(false);
        }

        resetScrollbar();
    }

    private void resetScrollbar() {
        scrollbar.setHeight(VISIBLE_ROWS * ROW_HEIGHT + (VISIBLE_ROWS - 1) * ROW_SPACING - 2);
        scrollbar.setRange(0, this.rows.size() - VISIBLE_ROWS, 2);
    }

    public record InputRow(AEKey key, NumberTextField textField, Component label, ValidButton button) {}

    private NumberTextField addNewNumberField(long value, int index) {
        var key = InputConstants.getKey("key.keyboard" + ".enter").getDisplayName();
        var tooltip =
                index == -1 ? AAEText.NumberTextFieldOutputHint.text(key) : AAEText.NumberTextFieldInputHint.text(key);

        NumberTextField numberField = new NumberTextField(
                this.style,
                0,
                0,
                0,
                0,
                amount -> {
                    if (index >= 0) {
                        AAENetworkHandler.INSTANCE.sendToServer(new SetStockAmountPacket(index, amount));
                    } else {
                        menu.setMaxCrafted(amount);
                    }
                },
                tooltip);

        numberField.setLongValue(value);

        return numberField;
    }

    static class ValidButton extends AddonIconButton {

        private boolean isValid = false;

        public ValidButton(OnPress onPress) {
            super(onPress);
            this.setDisableBackground(true);
        }

        public void setValid(boolean val) {
            this.isValid = val;
        }

        @Override
        protected AAEIcon getIcon() {
            return isValid ? AAEIcon.VALID_INPUT : AAEIcon.INVALID_INPUT;
        }
    }
}
