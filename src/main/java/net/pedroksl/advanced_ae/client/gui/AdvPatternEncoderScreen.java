package net.pedroksl.advanced_ae.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.client.AAEHotkeys;
import net.pedroksl.advanced_ae.client.gui.widgets.DirectionInputButton;
import net.pedroksl.advanced_ae.client.gui.widgets.SlotInputButton;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.gui.AdvPatternEncoderMenu;
import net.pedroksl.advanced_ae.network.packet.AdvPatternEncoderChangeDirectionPacket;
import net.pedroksl.advanced_ae.network.packet.AdvPatternEncoderChangeSlotListPacket;
import net.pedroksl.advanced_ae.network.packet.AdvPatternEncoderChangeSlotPacket;

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
    private static final int SLOT_BUTTON_WIDTH = 14;
    private static final int SLOT_BUTTON_GAP = 2;

    // "Slots" mode: one plain text field per row, right where the direction buttons would otherwise be. The
    // player types a comma-separated list of target slots (e.g. "0,1" to split the ingredient 1-per-slot across
    // slot 0 and slot 1). Far simpler than juggling separate occurrence-nav buttons.
    private static final int SLOT_LIST_EDIT_WIDTH = 100;

    private static final Rect2i SLOT_BBOX = new Rect2i(146, 16, SLOT_SIZE, SLOT_SIZE);

    private final ResourceLocation DEFAULT_TEXTURE = AppEng.makeId("textures/guis/adv_pattern_encoder.png");

    private final Scrollbar scrollbar;
    private LinkedHashMap<AEKey, Direction> inputList = new LinkedHashMap<>();
    private LinkedHashMap<AEKey, List<Integer>> slotList = new LinkedHashMap<>();
    private final HashMap<AEKey, DirectionInputButton[]> directionButtons = new HashMap<>();
    private final HashMap<AEKey, SlotInputButton> slotButtons = new HashMap<>();
    private final ArrayList<InputRow> rows = new ArrayList<>();

    /**
     * Toggles between the default "Lados" (direction) row layout and the dedicated "Slots" layout. The latter
     * trades the 7 direction buttons for one plain text box where the player types the full comma-separated
     * list of target slots for that ingredient — needed when the same item appears more than once in a recipe
     * and each occurrence has to land in a different slot (e.g. two separate deepslate units going to two
     * different pedestals).
     */
    private boolean slotsMode = false;

    private Button modeToggleButton;
    private final HashMap<AEKey, EditBox> slotListEditBoxes = new HashMap<>();

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
            var hotkey = AAEHotkeys.INSTANCE.getHotkeyMapping(hotkeyId);
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
        this.slotButtons.forEach((key, button) -> button.visible = false);
        this.slotListEditBoxes.forEach((key, box) -> box.setVisible(false));

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

            int rowY = this.topPos + LIST_ANCHOR_Y + 1 + i * (ROW_HEIGHT + ROW_SPACING);

            if (this.slotsMode) {
                layoutSlotsModeRow(row.key(), rowY);
            } else {
                layoutDirectionModeRow(row.key(), row.dir(), rowY);
            }
        }
    }

    private void layoutDirectionModeRow(AEKey key, @Nullable Direction dir, int rowY) {
        var buttons = this.directionButtons.get(key);
        var highlight = getSelectedDirButton(dir);
        for (var col = 0; col < 7; col++) {
            var button = buttons[col];
            button.setPosition(
                    this.leftPos
                            + LIST_ANCHOR_X
                            + 2
                            + SLOT_SIZE
                            + (col + 1) * DIRECTION_BUTTONS_OFFSET_X
                            + col * DIRECTION_BUTTONS_WIDTH,
                    rowY);
            button.setHighlighted(col == highlight);
            button.visible = true;
        }

        var slotButton = this.slotButtons.get(key);
        if (slotButton != null) {
            slotButton.setPosition(
                    this.leftPos
                            + LIST_ANCHOR_X
                            + 2
                            + SLOT_SIZE
                            + 8 * DIRECTION_BUTTONS_OFFSET_X
                            + 7 * DIRECTION_BUTTONS_WIDTH
                            + SLOT_BUTTON_GAP,
                    rowY);
            slotButton.visible = true;
        }
    }

    private void layoutSlotsModeRow(AEKey key, int rowY) {
        var editBox = this.slotListEditBoxes.get(key);
        if (editBox == null) {
            return;
        }
        editBox.setPosition(this.leftPos + LIST_ANCHOR_X + 2 + SLOT_SIZE + 2, rowY + 1);
        if (!editBox.isFocused()) {
            var slots = this.slotList.getOrDefault(key, List.of());
            String text = slots.isEmpty()
                    ? ""
                    : String.join(",", slots.stream().map(String::valueOf).toList());
            if (!editBox.getValue().equals(text)) {
                editBox.setValue(text);
            }
        }
        editBox.setVisible(true);
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

        // Placed in the otherwise-empty gap of the right-hand column, between the pattern input slot (top) and
        // the encoded-pattern output slot (bottom) — doesn't collide with the title text or the row list.
        this.modeToggleButton = this.addRenderableWidget(Button.builder(modeToggleLabel(), b -> {
                    this.slotsMode = !this.slotsMode;
                    b.setMessage(modeToggleLabel());
                })
                .bounds(this.leftPos + 146, this.topPos + 40, 20, 12)
                .tooltip(Tooltip.create(
                        Component.translatable(AAEText.AdvPatternEncoderModeToggleTooltip.getTranslationKey())))
                .build());

        this.refreshList();

        this.getMenu().onUpdateRequested();
    }

    private Component modeToggleLabel() {
        return Component.translatable(
                this.slotsMode
                        ? AAEText.AdvPatternEncoderModeToggleSlots.getTranslationKey()
                        : AAEText.AdvPatternEncoderModeToggleSides.getTranslationKey());
    }

    public void update(LinkedHashMap<AEKey, Direction> inputList, LinkedHashMap<AEKey, List<Integer>> slotList) {
        // Every keystroke in the "Slots" text field round-trips through the server and comes back here as a
        // fresh sync. If the set of ingredients hasn't actually changed (just their direction/slot values), swap
        // the data in place and let drawFG's existing "don't touch a focused/unchanged field" logic handle the
        // visual refresh — tearing down and recreating the widgets on every keystroke was destroying the very
        // EditBox the player was typing into (killing focus, looking like flicker, and making it uneditable).
        boolean sameKeys = this.inputList.keySet().equals(inputList.keySet());

        this.inputList = inputList;
        this.slotList = slotList;

        if (sameKeys) {
            this.rows.clear();
            for (var key : this.inputList.keySet()) {
                this.rows.add(new InputRow(key, this.inputList.get(key)));
            }
            return;
        }

        this.directionButtons.forEach((k, v) -> {
            for (var btn : v) {
                this.removeWidget(btn);
            }
        });
        this.directionButtons.clear();
        this.slotButtons.forEach((k, btn) -> this.removeWidget(btn));
        this.slotButtons.clear();
        this.slotListEditBoxes.forEach((k, box) -> this.removeWidget(box));
        this.slotListEditBoxes.clear();
        this.rows.clear();

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

            var slots = this.slotList.getOrDefault(key, List.of());

            var slotButton =
                    new SlotInputButton(0, 0, SLOT_BUTTON_WIDTH, DIRECTION_BUTTONS_HEIGHT, this::slotButtonPressed);
            slotButton.setKey(key);
            slotButton.setSlot(slots.isEmpty() ? -1 : slots.get(0));
            slotButton.setTooltip(Tooltip.create(
                    Component.translatable(AAEText.AdvPatternEncoderSlotButtonTooltip.getTranslationKey())));
            slotButton.visible = false;
            slotButtons.put(key, this.addRenderableWidget(slotButton));

            var editBox =
                    new EditBox(this.font, 0, 0, SLOT_LIST_EDIT_WIDTH, DIRECTION_BUTTONS_HEIGHT, Component.empty());
            editBox.setMaxLength(64);
            editBox.setFilter(s -> s.matches("[0-9,\\-]*"));
            editBox.setResponder(text -> onSlotListTextChanged(key, text));
            editBox.setTooltip(Tooltip.create(
                    Component.translatable(AAEText.AdvPatternEncoderSlotListTooltip.getTranslationKey())));
            editBox.setVisible(false);
            slotListEditBoxes.put(key, this.addRenderableWidget(editBox));
        }

        this.resetScrollbar();
    }

    private void onSlotListTextChanged(AEKey key, String text) {
        List<Integer> slots = new ArrayList<>();
        for (var part : text.split(",")) {
            var trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                slots.add(Integer.parseInt(trimmed));
            } catch (NumberFormatException e) {
                // Ignore partial/invalid numbers while the player is still typing.
            }
        }
        PacketDistributor.sendToServer(new AdvPatternEncoderChangeSlotListPacket(key, slots));
    }

    private void directionButtonPressed(Button b) {
        DirectionInputButton button = ((DirectionInputButton) b);
        PacketDistributor.sendToServer(
                new AdvPatternEncoderChangeDirectionPacket(button.getKey(), button.getDirection()));
    }

    private void slotButtonPressed(Button b) {
        SlotInputButton button = ((SlotInputButton) b);
        PacketDistributor.sendToServer(new AdvPatternEncoderChangeSlotPacket(button.getKey(), 0, button.getSlot()));
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
