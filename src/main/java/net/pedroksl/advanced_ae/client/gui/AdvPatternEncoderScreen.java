package net.pedroksl.advanced_ae.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.client.Hotkeys;
import net.pedroksl.advanced_ae.client.gui.widgets.DirectionInputButton;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.gui.AdvPatternEncoderMenu;
import net.pedroksl.advanced_ae.network.packet.AdvPatternEncoderChangeDirectionPacket;

import appeng.api.stacks.AEKey;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.Scrollbar;
import appeng.core.AppEng;
import appeng.menu.slot.FakeSlot;

public class AdvPatternEncoderScreen extends AEBaseScreen<AdvPatternEncoderMenu> {

    private static final int ROW_HEIGHT = 18;
    private static final int SLOT_SIZE = ROW_HEIGHT;
    private static final int ROW_SPACING = 2;
    private static final int VISIBLE_ROWS = 3;

    private static final int LIST_ANCHOR_X = 18;
    private static final int LIST_ANCHOR_Y = 22;
    private static final int DIRECTION_BUTTONS_OFFSET_X = 1;
    private static final int DIRECTION_BUTTONS_WIDTH = 12;
    private static final int DIRECTION_BUTTONS_HEIGHT = 14;

    private static final Rect2i SLOT_BBOX = new Rect2i(146, 16, SLOT_SIZE, SLOT_SIZE);

    private final ResourceLocation DEFAULT_TEXTURE = AppEng.makeId("textures/guis/adv_pattern_encoder.png");

    private final Scrollbar scrollbar;
    private LinkedHashMap<AEKey, Direction> inputList = new LinkedHashMap<>();
    private final HashMap<AEKey, DirectionInputButton[]> directionButtons = new HashMap<>();
    private final ArrayList<InputRow> rows = new ArrayList<>();

    public AdvPatternEncoderScreen(
            AdvPatternEncoderMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.scrollbar = widgets.addScrollBar("scrollbar", Scrollbar.SMALL);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isCloseHotkey(keyCode, scanCode)) {
            this.getPlayer().closeContainer();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean isCloseHotkey(int keyCode, int scanCode) {
        var hotkeyId = getMenu().getHost().getCloseHotkey();
        if (hotkeyId != null) {
            var hotkey = Hotkeys.getHotkeyMapping(hotkeyId);
            if (hotkey != null) {
                return hotkey.mapping().matches(keyCode, scanCode);
            }
        }
        return false;
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        this.menu.slots.removeIf(slot -> slot instanceof FakeSlot);
        this.directionButtons.forEach((key, value) -> {
            for (int x = 0; x < 7; x++) {
                value[x].visible = false;
            }
        });

        final int scrollLevel = scrollbar.getCurrentScroll();
        int visibleRows = Math.min(VISIBLE_ROWS, this.inputList.size());
        int i = 0;
        for (; i < visibleRows; ++i) {
            int currentRow = scrollLevel + i;
            if (currentRow >= this.inputList.size()) {
                break;
            }

            InputRow row = this.rows.get(currentRow);
            guiGraphics.renderItem(
                    row.key().wrapForDisplayOrFilter(),
                    LIST_ANCHOR_X + 1,
                    LIST_ANCHOR_Y + 1 + i * (ROW_HEIGHT + ROW_SPACING));

            var buttons = this.directionButtons.get(row.key);
            var highlight = getSelectedDirButton(row.dir);
            for (var col = 0; col < 7; col++) {
                var button = buttons[col];
                button.setPosition(
                        this.leftPos
                                + LIST_ANCHOR_X
                                + 2
                                + SLOT_SIZE
                                + (col + 1) * DIRECTION_BUTTONS_OFFSET_X
                                + col * DIRECTION_BUTTONS_WIDTH,
                        this.topPos + LIST_ANCHOR_Y + 1 + i * (ROW_HEIGHT + ROW_SPACING));
                button.setHighlighted(col == highlight);
                button.visible = true;
            }
        }
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);

        int currentX = offsetX + LIST_ANCHOR_X;
        int currentY = offsetY + LIST_ANCHOR_Y;

        int visibleRows = Math.min(VISIBLE_ROWS, this.inputList.size());
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
    }

    @Override
    public void init() {
        super.init();
        this.refreshList();

        this.getMenu().onUpdateRequested();
    }

    public void update(LinkedHashMap<AEKey, Direction> inputList) {
        this.inputList.clear();
        this.directionButtons.forEach((k, v) -> {
            for (var btn : v) {
                this.removeWidget(btn);
            }
        });
        this.directionButtons.clear();
        this.rows.clear();

        this.inputList = inputList;
        this.refreshList();
    }

    private void refreshList() {
        for (var key : this.inputList.keySet()) {
            this.rows.add(new InputRow(key, this.inputList.get(key)));

            DirectionInputButton[] buttons = new DirectionInputButton[7];
            for (var x = 0; x < 7; x++) {
                var button = new DirectionInputButton(
                        0,
                        0,
                        DIRECTION_BUTTONS_WIDTH,
                        DIRECTION_BUTTONS_HEIGHT,
                        getDirButtonTextures(x),
                        this::directionButtonPressed);
                button.setTooltip(Tooltip.create(getDirButtonText(x)));
                button.setKey(key);
                button.setIndex(x);
                button.visible = false;
                buttons[x] = this.addRenderableWidget(button);
            }

            directionButtons.put(key, buttons);
        }

        this.resetScrollbar();
    }

    private void directionButtonPressed(Button b) {
        DirectionInputButton button = ((DirectionInputButton) b);
        PacketDistributor.sendToServer(
                new AdvPatternEncoderChangeDirectionPacket(button.getKey(), button.getDirection()));
    }

    private int getSelectedDirButton(@Nullable Direction dir) {
        if (dir == null) return 0;

        return switch (dir) {
            case NORTH -> 1;
            case EAST -> 2;
            case SOUTH -> 3;
            case WEST -> 4;
            case UP -> 5;
            case DOWN -> 6;
        };
    }

    private Pair<ResourceLocation, ResourceLocation> getDirButtonTextures(int index) {
        return switch (index) {
            case 1 -> new Pair<>(
                    AdvancedAE.makeId("textures/guis/north_button.png"),
                    AdvancedAE.makeId("textures/guis/north_button_selected.png"));
            case 2 -> new Pair<>(
                    AdvancedAE.makeId("textures/guis/east_button.png"),
                    AdvancedAE.makeId("textures/guis/east_button_selected.png"));
            case 3 -> new Pair<>(
                    AdvancedAE.makeId("textures/guis/south_button.png"),
                    AdvancedAE.makeId("textures/guis/south_button_selected.png"));
            case 4 -> new Pair<>(
                    AdvancedAE.makeId("textures/guis/west_button.png"),
                    AdvancedAE.makeId("textures/guis/west_button_selected.png"));
            case 5 -> new Pair<>(
                    AdvancedAE.makeId("textures/guis/up_button.png"),
                    AdvancedAE.makeId("textures/guis/up_button_selected.png"));
            case 6 -> new Pair<>(
                    AdvancedAE.makeId("textures/guis/down_button.png"),
                    AdvancedAE.makeId("textures/guis/down_button_selected.png"));
            default -> new Pair<>(
                    AdvancedAE.makeId("textures/guis/any_button.png"),
                    AdvancedAE.makeId("textures/guis/any_button_selected.png"));
        };
    }

    private Component getDirButtonText(int index) {
        return switch (index) {
            case 1 -> Component.translatable(AAEText.NorthButton.getTranslationKey());
            case 2 -> Component.translatable(AAEText.EastButton.getTranslationKey());
            case 3 -> Component.translatable(AAEText.SouthButton.getTranslationKey());
            case 4 -> Component.translatable(AAEText.WestButton.getTranslationKey());
            case 5 -> Component.translatable(AAEText.UpButton.getTranslationKey());
            case 6 -> Component.translatable(AAEText.DownButton.getTranslationKey());
            default -> Component.translatable(AAEText.AnyButton.getTranslationKey());
        };
    }

    private void resetScrollbar() {
        scrollbar.setHeight(VISIBLE_ROWS * ROW_HEIGHT + (VISIBLE_ROWS - 1) * ROW_SPACING - 2);
        scrollbar.setRange(0, this.inputList.size() - VISIBLE_ROWS, 2);
    }

    public record InputRow(AEKey key, @Nullable Direction dir) {}
}
