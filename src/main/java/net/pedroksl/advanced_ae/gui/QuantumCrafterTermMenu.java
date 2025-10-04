package net.pedroksl.advanced_ae.gui;

import java.util.*;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.ints.Int2BooleanArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.api.IQuantumCrafterTermMenuHost;
import net.pedroksl.advanced_ae.api.ShowQuantumCrafters;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.helpers.AutoCraftingContainer;
import net.pedroksl.advanced_ae.network.packet.ClearQuantumCrafterTerminalPacket;
import net.pedroksl.advanced_ae.network.packet.QuantumCrafterTerminalClientAction;
import net.pedroksl.advanced_ae.network.packet.QuantumCrafterTerminalPacket;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.storage.ILinkStatus;
import appeng.core.AELog;
import appeng.core.network.clientbound.SetLinkStatusPacket;
import appeng.helpers.InventoryAction;
import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.guisync.LinkStatusAwareMenu;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.IAEItemFilter;

public class QuantumCrafterTermMenu extends AEBaseMenu implements LinkStatusAwareMenu {

    private final IQuantumCrafterTermMenuHost host;

    @GuiSync(1)
    public ShowQuantumCrafters showQuantumCrafters = ShowQuantumCrafters.VISIBLE;

    private ILinkStatus linkStatus = ILinkStatus.ofDisconnected();

    private static long inventorySerial = Long.MIN_VALUE;
    private final Map<AutoCraftingContainer, ContainerTracker> diList = new IdentityHashMap<>();
    private final Long2ObjectOpenHashMap<ContainerTracker> byId = new Long2ObjectOpenHashMap<>();
    /**
     * Tracks hosts that were visible before, even if they no longer match the filter. For
     * {@link ShowQuantumCrafters#NOT_FULL}.
     */
    private final Set<AutoCraftingContainer> pinnedHosts = Collections.newSetFromMap(new IdentityHashMap<>());

    public QuantumCrafterTermMenu(int id, Inventory playerInventory, IQuantumCrafterTermMenuHost host) {
        this(AAEMenus.QUANTUM_CRAFTER_TERMINAL.get(), id, playerInventory, host, true);
    }

    public QuantumCrafterTermMenu(
            MenuType<?> menuType,
            int id,
            Inventory playerInventory,
            IQuantumCrafterTermMenuHost host,
            boolean bindInventory) {
        super(menuType, id, playerInventory, host);
        this.host = host;
        if (bindInventory) {
            this.createPlayerInventorySlots(playerInventory);
        }
    }

    public ShowQuantumCrafters getShownQuantumCrafters() {
        return showQuantumCrafters;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void broadcastChanges() {
        if (isClientSide()) {
            return;
        }

        showQuantumCrafters = this.host.getConfigManager().getSetting(AAESettings.TERMINAL_SHOW_QUANTUM_CRAFTERS);

        super.broadcastChanges();

        updateLinkStatus();

        if (showQuantumCrafters != ShowQuantumCrafters.NOT_FULL) {
            this.pinnedHosts.clear();
        }

        IGrid grid = getGrid();

        var state = new VisitorState();
        if (grid != null) {
            for (var machineClass : grid.getMachineClasses()) {
                if (AutoCraftingContainer.class.isAssignableFrom(machineClass)) {
                    visitPatternProviderHosts(grid, (Class<? extends AutoCraftingContainer>) machineClass, state);
                }
            }

            // Ensure we don't keep references to removed hosts
            pinnedHosts.removeIf(host -> host.getGrid() != grid);
        } else {
            pinnedHosts.clear();
        }

        if (state.total != this.diList.size() || state.forceFullUpdate) {
            sendFullUpdate(grid);
        } else {
            sendIncrementalUpdate();
        }
    }

    @Nullable
    private IGrid getGrid() {
        IActionHost aHost = getActionHost();
        if (aHost != null) {
            IGridNode agn = aHost.getActionableNode();
            if (agn != null && agn.isActive()) {
                return agn.getGrid();
            }
        }
        return null;
    }

    private static class VisitorState {
        // Total number of pattern provider hosts found
        int total;
        // Set to true if any visited machines were missing from diList, or had a different name
        boolean forceFullUpdate;
    }

    private boolean isFull(AutoCraftingContainer logic) {
        for (int i = 0; i < logic.getTerminalPatternInventory().size(); i++) {
            if (logic.getTerminalPatternInventory().getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isVisible(AutoCraftingContainer container) {
        boolean isVisible = container.isVisibleInTerminal();

        return switch (getShownQuantumCrafters()) {
            case VISIBLE -> isVisible;
            case NOT_FULL -> isVisible && (pinnedHosts.contains(container) || !isFull(container));
            case ALL -> true;
        };
    }

    private <T extends AutoCraftingContainer> void visitPatternProviderHosts(
            IGrid grid, Class<T> machineClass, VisitorState state) {
        for (var container : grid.getActiveMachines(machineClass)) {
            if (!isVisible(container)) {
                continue;
            }

            if (getShownQuantumCrafters() == ShowQuantumCrafters.NOT_FULL) {
                pinnedHosts.add(container);
            }

            var t = this.diList.get(container);
            if (t == null) {
                state.forceFullUpdate = true;
            }

            state.total++;
        }
    }

    public void configPattern(long serverId, int slot) {
        if (isClientSide()) {
            PacketDistributor.sendToServer(new QuantumCrafterTerminalClientAction(true, serverId, slot));
            return;
        }

        final ContainerTracker inv = this.byId.get(serverId);
        if (inv == null) {
            // Can occur if the client sent an interaction packet right before an inventory got removed
            return;
        }
        if (slot < 0 || slot >= inv.server.size()) {
            // Client refers to an invalid slot. This should NOT happen
            AELog.warn("Client refers to invalid slot %d of inventory %s", slot, inv.container);
            return;
        }

        var locator = getLocator();
        if (locator != null) {
            var inputs = inv.container.getPatternConfigInputs(slot);
            var output = inv.container.getPatternConfigOutput(slot);

            if (inputs == null || output == null) return;

            QuantumCrafterConfigPatternMenu.open(
                    ((ServerPlayer) this.getPlayer()), locator, inv.container, slot, inputs, output);
        }
    }

    public void toggleEnabledPattern(long serverId, int slot) {
        if (isClientSide()) {
            PacketDistributor.sendToServer(new QuantumCrafterTerminalClientAction(false, serverId, slot));
            return;
        }

        final ContainerTracker inv = this.byId.get(serverId);
        if (inv == null) {
            // Can occur if the client sent an interaction packet right before an inventory got removed
            return;
        }
        if (slot < 0 || slot >= inv.server.size()) {
            // Client refers to an invalid slot. This should NOT happen
            AELog.warn("Client refers to invalid slot %d of inventory %s", slot, inv.container);
            return;
        }

        inv.container.toggleEnablePattern(slot);
    }

    @Override
    public void doAction(ServerPlayer player, InventoryAction action, int slot, long id) {
        final ContainerTracker inv = this.byId.get(id);
        if (inv == null) {
            // Can occur if the client sent an interaction packet right before an inventory got removed
            return;
        }
        if (slot < 0 || slot >= inv.server.size()) {
            // Client refers to an invalid slot. This should NOT happen
            AELog.warn("Client refers to invalid slot %d of inventory %s", slot, inv.container);
            return;
        }

        final ItemStack is = inv.server.getStackInSlot(slot);

        var patternSlot = new FilteredInternalInventory(inv.server.getSlotInv(slot), new PatternSlotFilter());

        var carried = getCarried();
        switch (action) {
            case PICKUP_OR_SET_DOWN -> {
                if (!carried.isEmpty()) {
                    ItemStack inSlot = patternSlot.getStackInSlot(0);
                    if (inSlot.isEmpty()) {
                        setCarried(patternSlot.addItems(carried));
                    } else {
                        inSlot = inSlot.copy();
                        final ItemStack inHand = carried.copy();

                        patternSlot.setItemDirect(0, ItemStack.EMPTY);
                        setCarried(ItemStack.EMPTY);

                        setCarried(patternSlot.addItems(inHand.copy()));

                        if (getCarried().isEmpty()) {
                            setCarried(inSlot);
                        } else {
                            setCarried(inHand);
                            patternSlot.setItemDirect(0, inSlot);
                        }
                    }
                } else {
                    setCarried(patternSlot.getStackInSlot(0));
                    patternSlot.setItemDirect(0, ItemStack.EMPTY);
                }
            }
            case SPLIT_OR_PLACE_SINGLE -> {
                if (!carried.isEmpty()) {
                    ItemStack extra = carried.split(1);
                    if (!extra.isEmpty()) {
                        extra = patternSlot.addItems(extra);
                    }
                    if (!extra.isEmpty()) {
                        carried.grow(extra.getCount());
                    }
                } else if (!is.isEmpty()) {
                    setCarried(patternSlot.extractItem(0, (is.getCount() + 1) / 2, false));
                }
            }
            case SHIFT_CLICK -> {
                var stack = patternSlot.getStackInSlot(0).copy();
                if (!player.getInventory().add(stack)) {
                    patternSlot.setItemDirect(0, stack);
                } else {
                    patternSlot.setItemDirect(0, ItemStack.EMPTY);
                }
            }
            case MOVE_REGION -> {
                for (int x = 0; x < inv.server.size(); x++) {
                    var stack = inv.server.getStackInSlot(x);
                    if (!player.getInventory().add(stack)) {
                        patternSlot.setItemDirect(0, stack);
                    } else {
                        patternSlot.setItemDirect(0, ItemStack.EMPTY);
                    }
                }
            }
            case CREATIVE_DUPLICATE -> {
                if (player.getAbilities().instabuild && carried.isEmpty()) {
                    setCarried(is.isEmpty() ? ItemStack.EMPTY : is.copy());
                }
            }
        }
    }

    private void sendFullUpdate(@Nullable IGrid grid) {
        this.byId.clear();
        this.diList.clear();

        sendPacketToClient(new ClearQuantumCrafterTerminalPacket());

        if (grid == null) {
            return;
        }

        for (var machineClass : grid.getMachineClasses()) {
            var containerClass = tryCastMachineToContainer(machineClass);
            if (containerClass == null) {
                continue;
            }

            for (var container : grid.getActiveMachines(containerClass)) {
                if (isVisible(container)) {
                    this.diList.put(
                            container,
                            new ContainerTracker(
                                    container,
                                    container.getTerminalPatternInventory(),
                                    container.getEnabledPatternSlots(),
                                    container.getInvalidPatternSlots()));
                }
            }
        }

        for (var inv : this.diList.values()) {
            this.byId.put(inv.serverId, inv);
            sendPacketToClient(inv.createFullPacket());
        }
    }

    private void sendIncrementalUpdate() {
        for (var inv : this.diList.values()) {
            var packet = inv.createUpdatePacket();
            if (packet != null) {
                sendPacketToClient(packet);
            }
        }
    }

    private static class ContainerTracker {

        private final AutoCraftingContainer container;
        private final long sortBy;
        private final long serverId = inventorySerial++;
        // This is used to track the inventory contents we sent to the client for change detection
        private final InternalInventory client;
        // This is used to track the enabled slots we sent to the client for change detection
        private final List<Boolean> clientEnabled;
        // This is used to track the invalid  slots we sent to the client for change detection
        private final List<Boolean> clientInvalid;
        // This is a reference to the real inventory used by this machine
        private final InternalInventory server;
        // This is a reference to the real enabled list used by this machine
        private final List<Boolean> serverEnabled;
        // This is a reference to the real invalid list used by this machine
        private final List<Boolean> serverInvalid;

        public ContainerTracker(
                AutoCraftingContainer container,
                InternalInventory patterns,
                List<Boolean> enabledList,
                List<Boolean> invalidList) {
            this.container = container;
            this.server = patterns;
            this.client = new AppEngInternalInventory(this.server.size());
            this.serverEnabled = enabledList;
            this.clientEnabled = new ArrayList<>(enabledList);
            this.serverInvalid = invalidList;
            this.clientInvalid = new ArrayList<>(invalidList);
            this.sortBy = container.getTerminalSortOrder();
        }

        public QuantumCrafterTerminalPacket createFullPacket() {
            var slots = new Int2ObjectArrayMap<ItemStack>(server.size());
            var enabledArray = new Int2BooleanArrayMap(server.size());
            var invalidArray = new Int2BooleanArrayMap(server.size());
            for (int i = 0; i < server.size(); i++) {
                var stack = server.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    slots.put(i, stack);
                }
                enabledArray.put(i, serverEnabled.get(i).booleanValue());
                invalidArray.put(i, serverInvalid.get(i).booleanValue());
            }

            return QuantumCrafterTerminalPacket.fullUpdate(
                    serverId, server.size(), sortBy, slots, enabledArray, invalidArray);
        }

        @Nullable
        public QuantumCrafterTerminalPacket createUpdatePacket() {
            var changedSlots = detectChangedSlots();
            if (changedSlots == null) {
                return null;
            }

            var slots = new Int2ObjectArrayMap<ItemStack>(changedSlots.size());
            var enabledArray = new Int2BooleanArrayMap(changedSlots.size());
            var invalidArray = new Int2BooleanArrayMap(changedSlots.size());
            for (int i = 0; i < changedSlots.size(); i++) {
                var slot = changedSlots.getInt(i);
                var stack = server.getStackInSlot(slot);
                // "update" client side.
                client.setItemDirect(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
                slots.put(slot, stack);
                var enabled = serverEnabled.get(slot);
                // "update" client side.
                clientEnabled.set(slot, enabled);
                enabledArray.put(slot, enabled.booleanValue());
                var invalid = serverInvalid.get(slot);
                clientInvalid.set(slot, invalid);
                invalidArray.put(slot, invalid.booleanValue());
            }

            return QuantumCrafterTerminalPacket.incrementalUpdate(serverId, slots, enabledArray, invalidArray);
        }

        @Nullable
        private IntList detectChangedSlots() {
            IntList changedSlots = null;
            for (int x = 0; x < server.size(); x++) {
                if (isDifferent(server.getStackInSlot(x), client.getStackInSlot(x))
                        || serverEnabled.get(x) != clientEnabled.get(x)
                        || serverInvalid.get(x) != clientInvalid.get(x)) {
                    if (changedSlots == null) {
                        changedSlots = new IntArrayList();
                    }
                    changedSlots.add(x);
                }
            }
            return changedSlots;
        }

        private static boolean isDifferent(ItemStack a, ItemStack b) {
            if (a.isEmpty() && b.isEmpty()) {
                return false;
            }

            if (a.isEmpty() || b.isEmpty()) {
                return true;
            }

            return !ItemStack.matches(a, b);
        }
    }

    private static Class<? extends AutoCraftingContainer> tryCastMachineToContainer(Class<?> machineClass) {
        if (AutoCraftingContainer.class.isAssignableFrom(machineClass)) {
            return machineClass.asSubclass(AutoCraftingContainer.class);
        }
        return null;
    }

    private static class PatternSlotFilter implements IAEItemFilter {
        @Override
        public boolean allowExtract(InternalInventory inv, int slot, int amount) {
            return true;
        }

        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return !stack.isEmpty() && PatternDetailsHelper.isEncodedPattern(stack);
        }
    }

    protected void updateLinkStatus() {
        var linkStatus = host.getLinkStatus();
        if (!Objects.equals(this.linkStatus, linkStatus)) {
            this.linkStatus = linkStatus;
            sendPacketToClient(new SetLinkStatusPacket(linkStatus));
        }
    }

    public ILinkStatus getLinkStatus() {
        return linkStatus;
    }

    @Override
    public void setLinkStatus(ILinkStatus linkStatus) {
        this.linkStatus = linkStatus;
    }
}
