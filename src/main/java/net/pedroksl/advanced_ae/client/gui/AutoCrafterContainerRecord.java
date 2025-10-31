package net.pedroksl.advanced_ae.client.gui;

import it.unimi.dsi.fastutil.ints.Int2BooleanArrayMap;

import net.pedroksl.advanced_ae.common.helpers.AutoCraftingContainer;

import appeng.util.inv.AppEngInternalInventory;

/**
 * This class is used on the client-side to represent a quantum crafter and its inventory as it is shown in the
 * {@link QuantumCrafterTermScreen}'s table.
 */
public class AutoCrafterContainerRecord implements Comparable<AutoCrafterContainerRecord> {

    /**
     * Identifier for this quantum crafter  on the server-side. See {@link QuantumCrafterTermScreen}
     */
    private final long serverId;

    // The client-side representation of the machine's inventory, which is only used for display purposes
    private final AppEngInternalInventory inventory;

    private final Int2BooleanArrayMap enabledArray;
    private final Int2BooleanArrayMap invalidArray;

    /**
     * Used to sort this record in the pattern access terminal's table, comes from
     * {@link AutoCraftingContainer#getTerminalSortOrder()}
     */
    private final long order;

    public AutoCrafterContainerRecord(long serverId, int slots, long order) {
        this.inventory = new AppEngInternalInventory(slots);
        this.enabledArray = new Int2BooleanArrayMap(slots);
        this.invalidArray = new Int2BooleanArrayMap(slots);
        this.serverId = serverId;
        this.order = order;
    }

    @Override
    public int compareTo(AutoCrafterContainerRecord o) {
        return Long.compare(this.order, o.order);
    }

    public long getServerId() {
        return this.serverId;
    }

    public AppEngInternalInventory getInventory() {
        return inventory;
    }

    public Int2BooleanArrayMap getEnabledArray() {
        return enabledArray;
    }

    public Int2BooleanArrayMap getInvalidArray() {
        return invalidArray;
    }
}
