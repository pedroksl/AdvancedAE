package net.pedroksl.advanced_ae.client.gui;

import java.util.*;

import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.api.ShowQuantumCrafters;
import net.pedroksl.advanced_ae.client.gui.widgets.AAESettingToggleButton;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.gui.QuantumCrafterTermMenu;

import appeng.api.config.Settings;
import appeng.api.config.TerminalStyle;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.storage.ILinkStatus;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AECheckbox;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.Scrollbar;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.AEConfig;
import appeng.core.AppEng;
import appeng.core.localization.GuiText;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.InventoryAction;

import guideme.color.ConstantColor;
import guideme.document.LytRect;
import guideme.render.SimpleRenderContext;

public class QuantumCrafterTermScreen<C extends QuantumCrafterTermMenu> extends AEBaseScreen<C> {
    private static final int GUI_WIDTH = 195;
    private static final int GUI_TOP_AND_BOTTOM_PADDING = 54;

    private static final int GUI_PADDING_X = 8;
    private static final int GUI_PADDING_Y = 6;

    private static final int GUI_HEADER_HEIGHT = 17;
    private static final int GUI_FOOTER_HEIGHT = 99;
    private static final int COLUMNS = 9;

    /**
     * Additional margin in pixel for a text row inside the scrolling box.
     */
    private static final int PATTERN_PROVIDER_NAME_MARGIN_X = 2;

    /**
     * The maximum length for the string of a text row in pixel.
     */
    private static final int TEXT_MAX_WIDTH = 155;

    /**
     * Height of a table-row in pixels.
     */
    private static final int ROW_HEIGHT = 18;

    /**
     * Size of a slot in both x and y dimensions in pixel, most likely always the same as ROW_HEIGHT.
     */
    private static final int SLOT_SIZE = ROW_HEIGHT;

    // Bounding boxes of key areas in the UI texture.
    // The upper part of the UI, anything above the scrollable area (incl. its top border)
    private static final Rect2i HEADER_BBOX = new Rect2i(0, 0, GUI_WIDTH, GUI_HEADER_HEIGHT);
    // Background for a text row in the scroll-box.
    // Spans across the whole texture including the right and left borders including the scrollbar.
    // Covers separate textures for the top, middle and bottoms rows for more customization.
    private static final Rect2i ROW_TEXT_TOP_BBOX = new Rect2i(0, 17, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_TEXT_MIDDLE_BBOX = new Rect2i(0, 53, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_TEXT_BOTTOM_BBOX = new Rect2i(0, 89, GUI_WIDTH, ROW_HEIGHT);
    // Background for a inventory row in the scroll-box.
    // Spans across the whole texture including the right and left borders including the scrollbar.
    // Covers separate textures for the top, middle and bottoms rows for more customization.
    private static final Rect2i ROW_INVENTORY_TOP_BBOX = new Rect2i(0, 35, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_INVENTORY_MIDDLE_BBOX = new Rect2i(0, 71, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_INVENTORY_BOTTOM_BBOX = new Rect2i(0, 107, GUI_WIDTH, ROW_HEIGHT);
    // This is the lower part of the UI, anything below the scrollable area (incl. its bottom border)
    private static final Rect2i FOOTER_BBOX = new Rect2i(0, 125, GUI_WIDTH, GUI_FOOTER_HEIGHT);

    private final HashMap<Long, AutoCrafterContainerRecord> byId = new HashMap<>();
    private final HashMap<Long, Int2ObjectMap<Button>> configButtons = new HashMap<>();
    private final HashMap<Long, Int2ObjectMap<AECheckbox>> enableButtons = new HashMap<>();
    private final ArrayList<AutoCrafterContainerRecord> records = new ArrayList<>();
    private final ArrayList<Row> rows = new ArrayList<>();

    private final Map<String, Set<Object>> cachedSearches = new WeakHashMap<>();
    private final Scrollbar scrollbar;
    private final AETextField searchField;
    private final Map<ItemStack, String> patternSearchText = new WeakHashMap<>();

    private int visibleRows = 0;

    private final AAESettingToggleButton<ShowQuantumCrafters> showQuantumCrafters;

    public QuantumCrafterTermScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.scrollbar = widgets.addScrollBar("scrollbar", Scrollbar.BIG);
        this.imageWidth = GUI_WIDTH;

        // Add a terminalstyle button
        TerminalStyle terminalStyle = AEConfig.instance().getTerminalStyle();
        this.addToLeftToolbar(
                new SettingToggleButton<>(Settings.TERMINAL_STYLE, terminalStyle, this::toggleTerminalStyle));

        showQuantumCrafters = AAESettingToggleButton.serverButton(
                AAESettings.TERMINAL_SHOW_QUANTUM_CRAFTERS, ShowQuantumCrafters.VISIBLE);

        this.addToLeftToolbar(showQuantumCrafters);

        this.searchField = widgets.addTextField("search");
        this.searchField.setResponder(str -> this.refreshList());
        this.searchField.setPlaceholder(GuiText.SearchPlaceholder.text());
    }

    @Override
    public void init() {
        this.visibleRows = Math.max(
                2,
                config.getTerminalStyle()
                        .getRows((this.height - GUI_HEADER_HEIGHT - GUI_FOOTER_HEIGHT - GUI_TOP_AND_BOTTOM_PADDING)
                                / ROW_HEIGHT));
        // Render inventory in correct place.
        this.imageHeight = GUI_HEADER_HEIGHT + GUI_FOOTER_HEIGHT + this.visibleRows * ROW_HEIGHT;

        super.init();

        // Autofocus search field
        this.setInitialFocus(this.searchField);
        this.configButtons.forEach((k, m) -> m.forEach((key, value) -> {
            setVisibility(value, false);
            addRenderableWidget(value);
        }));
        this.enableButtons.forEach((k, m) -> m.forEach((key, value) -> {
            setVisibility(value, false);
            addRenderableWidget(value);
        }));

        // numLines may have changed, recalculate scroll bar.
        this.resetScrollbar();
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        this.menu.slots.removeIf(slot -> slot instanceof PatternSlot);
        this.configButtons.forEach((k, m) -> m.forEach((key, value) -> setVisibility(value, false)));
        this.enableButtons.forEach((k, m) -> m.forEach((key, value) -> setVisibility(value, false)));

        int textColor = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB();
        var level = Minecraft.getInstance().level;

        final int scrollLevel = scrollbar.getCurrentScroll();
        int i = 0;
        for (; i < this.visibleRows; ++i) {
            if (scrollLevel + i < this.rows.size()) {
                var row = this.rows.get(scrollLevel + i);
                if (row instanceof ConfigRow configRow) {
                    var configMap = this.configButtons.get(configRow.serverId);
                    for (int col = 0; col < configRow.slots; col++) {
                        var button = configMap.get(col);
                        button.setPosition(offsetX + col * SLOT_SIZE + GUI_PADDING_X, offsetY + (i + 1) * SLOT_SIZE);
                    }
                    configMap.forEach((key, value) -> setVisibility(value, true));
                } else if (row instanceof SlotsRow slotsRow) {
                    // Note: We have to shift everything after the header up by 1 to avoid black line duplication.
                    for (int col = 0; col < slotsRow.slots; col++) {
                        var slot = new PatternSlot(
                                slotsRow.container,
                                slotsRow.offset + col,
                                col * SLOT_SIZE + GUI_PADDING_X,
                                (i + 1) * SLOT_SIZE);
                        this.menu.slots.add(slot);

                        // Indicate invalid patterns
                        var pattern = slotsRow.container.getInventory().getStackInSlot(slotsRow.offset + col);
                        if (!pattern.isEmpty() && PatternDetailsHelper.decodePattern(pattern, level) == null
                                || slotsRow.container.getInvalidArray().get(slotsRow.offset + col)) {
                            guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x7fff0000);
                        }
                    }
                } else if (row instanceof EnabledRow enabledRow
                        && this.enableButtons.containsKey(enabledRow.container.getServerId())) {
                    var buttonMap = this.enableButtons.get(enabledRow.container.getServerId());
                    for (int col = 0; col < enabledRow.slots; col++) {
                        var button = buttonMap.get(col);
                        button.setPosition(
                                offsetX + col * SLOT_SIZE + GUI_PADDING_X + 1, offsetY + (i + 1) * SLOT_SIZE);
                        button.setSelected(
                                enabledRow.container.getEnabledArray().get(enabledRow.offset + col));
                    }
                    buttonMap.forEach((key, value) -> setVisibility(value, true));
                }
            }
        }

        // Draw an overlay indicating the grid is disconnected
        renderLinkStatus(guiGraphics, getMenu().getLinkStatus());
    }

    private void renderLinkStatus(GuiGraphics guiGraphics, ILinkStatus linkStatus) {
        // Draw an overlay indicating the grid is disconnected
        if (!linkStatus.connected()) {
            var renderContext = new SimpleRenderContext(LytRect.empty(), guiGraphics);

            var rect = new LytRect(GUI_PADDING_X - 1, GUI_HEADER_HEIGHT, COLUMNS * 18, visibleRows * ROW_HEIGHT);

            renderContext.fillRect(rect, new ConstantColor(0x3f000000));

            // Draw the disconnect status on top of the grid
            var statusDescription = linkStatus.statusDescription();
            if (statusDescription != null) {
                renderContext.renderTextCenteredIn(statusDescription.getString(), ERROR_TEXT_STYLE, rect);
            }
        }
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        if (btn == 1 && this.searchField.isMouseOver(xCoord, yCoord)) {
            this.searchField.setValue("");
            // Don't return immediately to also grab focus.
        }

        return super.mouseClicked(xCoord, yCoord, btn);
    }

    @Override
    protected void slotClicked(Slot slot, int slotIdx, int mouseButton, ClickType clickType) {
        if (slot instanceof PatternSlot machineSlot) {
            InventoryAction action = null;

            switch (clickType) {
                case PICKUP: // pickup / set-down.
                    action = mouseButton == 1
                            ? InventoryAction.SPLIT_OR_PLACE_SINGLE
                            : InventoryAction.PICKUP_OR_SET_DOWN;
                    break;
                case QUICK_MOVE:
                    action = mouseButton == 1 ? InventoryAction.PICKUP_SINGLE : InventoryAction.SHIFT_CLICK;
                    break;

                case CLONE: // creative dupe:
                    if (getPlayer().getAbilities().instabuild) {
                        action = InventoryAction.CREATIVE_DUPLICATE;
                    }

                    break;

                case THROW: // drop item:
                default:
            }

            if (action != null) {
                final InventoryActionPacket p = new InventoryActionPacket(
                        action, machineSlot.slot, machineSlot.getMachineInv().getServerId());
                PacketDistributor.sendToServer(p);
            }

            return;
        }

        super.slotClicked(slot, slotIdx, mouseButton, clickType);
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        // Draw the top of the dialog
        blit(guiGraphics, offsetX, offsetY, HEADER_BBOX);

        final int scrollLevel = scrollbar.getCurrentScroll();

        int currentY = offsetY + GUI_HEADER_HEIGHT;

        // Draw the footer now so slots will draw on top of it
        blit(guiGraphics, offsetX, currentY + this.visibleRows * ROW_HEIGHT, FOOTER_BBOX);

        for (int i = 0; i < this.visibleRows; ++i) {
            // Draw the dialog background for this row
            // Skip 1 pixel for the first row in order to not over-draw on the top scrollbox border,
            // and do the same but for the bottom border on the last row
            boolean firstLine = i == 0;
            boolean lastLine = i == this.visibleRows - 1;

            // Draw the background for the slots in an inventory row
            Rect2i bbox = selectRowBackgroundBox(false, firstLine, lastLine);
            blit(guiGraphics, offsetX, currentY, bbox);
            if (scrollLevel + i < this.rows.size()) {
                var row = this.rows.get(scrollLevel + i);
                if (row instanceof SlotsRow slotsRow) {
                    bbox = selectRowBackgroundBox(true, firstLine, lastLine);
                    bbox.setWidth(GUI_PADDING_X + SLOT_SIZE * slotsRow.slots - 1);
                    blit(guiGraphics, offsetX, currentY, bbox);
                }
            }

            currentY += ROW_HEIGHT;
        }
    }

    private Rect2i selectRowBackgroundBox(boolean isInvLine, boolean firstLine, boolean lastLine) {
        if (isInvLine) {
            if (firstLine) {
                return ROW_INVENTORY_TOP_BBOX;
            } else if (lastLine) {
                return ROW_INVENTORY_BOTTOM_BBOX;
            } else {
                return ROW_INVENTORY_MIDDLE_BBOX;
            }
        } else if (firstLine) {
            return ROW_TEXT_TOP_BBOX;
        } else if (lastLine) {
            return ROW_TEXT_BOTTOM_BBOX;
        } else {
            return ROW_TEXT_MIDDLE_BBOX;
        }
    }

    @Override
    public boolean charTyped(char character, int key) {
        if (character == ' ' && this.searchField.getValue().isEmpty()) {
            return true;
        }
        return super.charTyped(character, key);
    }

    public void clear() {
        this.byId.clear();
        // invalid caches on refresh
        this.cachedSearches.clear();
        this.refreshList();
    }

    public void postFullUpdate(
            long inventoryId,
            long sortBy,
            int inventorySize,
            Int2ObjectMap<ItemStack> slots,
            Int2BooleanMap enabledArray,
            Int2BooleanMap invalidArray) {
        var record = new AutoCrafterContainerRecord(inventoryId, inventorySize, sortBy);
        this.byId.put(inventoryId, record);

        var inventory = record.getInventory();
        for (var entry : slots.int2ObjectEntrySet()) {
            inventory.setItemDirect(entry.getIntKey(), entry.getValue());
        }

        var enabled = record.getEnabledArray();
        for (var entry : enabledArray.int2BooleanEntrySet()) {
            enabled.put(entry.getIntKey(), entry.getBooleanValue());
        }

        var invalid = record.getInvalidArray();
        for (var entry : invalidArray.int2BooleanEntrySet()) {
            invalid.put(entry.getIntKey(), entry.getBooleanValue());
        }

        // invalid caches on refresh
        this.cachedSearches.clear();
        this.refreshList();
    }

    public void postIncrementalUpdate(
            long inventoryId,
            Int2ObjectMap<ItemStack> slots,
            Int2BooleanMap enabledArray,
            Int2BooleanMap invalidArray) {
        var record = byId.get(inventoryId);
        if (record == null) {
            return;
        }

        var inventory = record.getInventory();
        for (var entry : slots.int2ObjectEntrySet()) {
            inventory.setItemDirect(entry.getIntKey(), entry.getValue());
        }

        var enabled = record.getEnabledArray();
        for (var entry : enabledArray.int2BooleanEntrySet()) {
            enabled.put(entry.getIntKey(), entry.getBooleanValue());
        }

        var invalid = record.getInvalidArray();
        for (var entry : invalidArray.int2BooleanEntrySet()) {
            invalid.put(entry.getIntKey(), entry.getBooleanValue());
        }
    }

    @Override
    public void updateBeforeRender() {
        super.updateBeforeRender();
        this.showQuantumCrafters.set(this.menu.getShownQuantumCrafters());
    }

    /**
     * Rebuilds the list of pattern providers.
     * <p>
     * Respects a search term if present (ignores case) and adding only matching patterns.
     */
    private void refreshList() {
        this.records.clear();
        this.configButtons.forEach((k, m) -> m.forEach((key, value) -> removeWidget(value)));
        this.configButtons.clear();
        this.enableButtons.forEach((k, m) -> m.forEach((key, value) -> removeWidget(value)));
        this.enableButtons.clear();

        final String searchFilterLowerCase = this.searchField.getValue().toLowerCase();

        final Set<Object> cachedSearch = this.getCacheForSearchTerm(searchFilterLowerCase);
        final boolean rebuild = cachedSearch.isEmpty();

        for (AutoCrafterContainerRecord entry : this.byId.values()) {
            // ignore inventory if not doing a full rebuild or cache already marks it as miss.
            if (!rebuild && !cachedSearch.contains(entry)) {
                continue;
            }

            // Shortcut to skip any filter if search term is ""/empty
            boolean found = searchFilterLowerCase.isEmpty();

            // Search if the current inventory holds a pattern containing the search term.
            if (!found) {
                for (ItemStack itemStack : entry.getInventory()) {
                    found = this.itemStackMatchesSearchTerm(itemStack, searchFilterLowerCase);
                    if (found) {
                        break;
                    }
                }
            }

            // if found, filter skipped or machine name matching the search term, add it
            if (found) {
                this.records.add(entry);
                cachedSearch.add(entry);
            } else {
                cachedSearch.remove(entry);
            }
        }

        this.rows.clear();
        this.rows.ensureCapacity(this.getMaxRows());

        var containers = new ArrayList<>(this.records);
        Collections.sort(containers);
        for (var container : containers) {
            // Wrap the container inventory slots
            var inventory = container.getInventory();
            for (var offset = 0; offset < inventory.size(); offset += COLUMNS) {
                var slots = Math.min(inventory.size() - offset, COLUMNS);

                Int2ObjectMap<Button> configButtons = new Int2ObjectArrayMap<>();
                Int2ObjectMap<AECheckbox> enabledButtons = new Int2ObjectArrayMap<>();
                for (var col = 0; col < COLUMNS; col++) {
                    var index = offset + col;
                    var cfgButton = new QuantumCrafterScreen.ConfigButton(
                            b -> menu.configPattern(container.getServerId(), index));
                    configButtons.put(index, this.addRenderableWidget(cfgButton));

                    var enabledButton = new AECheckbox(0, 0, 12, 12, this.style, Component.empty());
                    enabledButton.setChangeListener(() -> menu.toggleEnabledPattern(container.getServerId(), index));
                    enabledButton.setRadio(true);
                    enabledButton.setTooltip(Tooltip.create(AAEText.EnablePatternButton.text()));
                    enabledButtons.put(index, this.addRenderableWidget(enabledButton));
                }
                this.configButtons.put(container.getServerId(), configButtons);
                this.enableButtons.put(container.getServerId(), enabledButtons);

                this.rows.add(new ConfigRow(container.getServerId(), slots));
                this.rows.add(new SlotsRow(container, offset, slots));
                this.rows.add(new EnabledRow(container, offset, slots));
            }
        }

        // lines may have changed - recalculate scroll bar.
        this.resetScrollbar();
    }

    /**
     * Should be called whenever this.lines.size() or this.numLines changes.
     */
    private void resetScrollbar() {
        // Needs to take the border into account, so offset for 1 px on the top and bottom.
        scrollbar.setHeight(this.visibleRows * ROW_HEIGHT - 2);
        scrollbar.setRange(0, this.rows.size() - this.visibleRows, 2);
    }

    private boolean itemStackMatchesSearchTerm(ItemStack itemStack, String searchTerm) {
        if (itemStack.isEmpty()) {
            return false;
        }

        // Potential later use to filter by input
        return patternSearchText
                .computeIfAbsent(itemStack, this::getPatternSearchText)
                .contains(searchTerm);
    }

    private String getPatternSearchText(ItemStack stack) {
        var level = menu.getPlayer().level();
        var text = new StringBuilder();
        var pattern = PatternDetailsHelper.decodePattern(stack, level);

        if (pattern != null) {
            for (var output : pattern.getOutputs()) {
                output.what().getDisplayName().visit(content -> {
                    text.append(content.toLowerCase());
                    return Optional.empty();
                });
                text.append('\n');
            }
        }

        return text.toString();
    }

    /**
     * Tries to retrieve a cache for a with search term as keyword.
     * <p>
     * If this cache should be empty, it will populate it with an earlier cache if available or at least the cache for
     * the empty string.
     *
     * @param searchTerm the corresponding search
     * @return a Set matching a superset of the search term
     */
    private Set<Object> getCacheForSearchTerm(String searchTerm) {
        if (!this.cachedSearches.containsKey(searchTerm)) {
            this.cachedSearches.put(searchTerm, new HashSet<>());
        }

        final Set<Object> cache = this.cachedSearches.get(searchTerm);

        if (cache.isEmpty() && searchTerm.length() > 1) {
            cache.addAll(this.getCacheForSearchTerm(searchTerm.substring(0, searchTerm.length() - 1)));
        }

        return cache;
    }

    private void reinitialize() {
        this.children().removeAll(this.renderables);
        this.renderables.clear();
        this.init();
    }

    private void toggleTerminalStyle(SettingToggleButton<TerminalStyle> btn, boolean backwards) {
        TerminalStyle next = btn.getNextValue(backwards);
        AEConfig.instance().setTerminalStyle(next);
        btn.set(next);
        this.reinitialize();
    }

    private void setVisibility(AbstractButton b, boolean visibility) {
        b.visible = visibility;
        b.active = visibility;
    }

    /**
     * The max amount of unique names and each inv row. Not affected by the filtering.
     *
     * @return max amount of unique names and each inv row
     */
    private int getMaxRows() {
        return this.byId.size() * 3;
    }

    /**
     * A version of blit that lets us pass a source rectangle
     *
     * @see GuiGraphics#blit(ResourceLocation, int, int, int, int, int, int)
     */
    private void blit(GuiGraphics guiGraphics, int offsetX, int offsetY, Rect2i srcRect) {
        var texture = AppEng.makeId("textures/guis/quantum_crafter_terminal.png");
        guiGraphics.blit(
                texture, offsetX, offsetY, srcRect.getX(), srcRect.getY(), srcRect.getWidth(), srcRect.getHeight());
    }

    protected int getVisibleRows() {
        return visibleRows;
    }

    sealed interface Row {}

    /**
     * A row containing a header for a group.
     */
    record ConfigRow(long serverId, int slots) implements Row {}

    /**
     * A row containing slots for a subset of a pattern container inventory.
     */
    record SlotsRow(AutoCrafterContainerRecord container, int offset, int slots) implements Row {}

    record EnabledRow(AutoCrafterContainerRecord container, int offset, int slots) implements Row {}
}
