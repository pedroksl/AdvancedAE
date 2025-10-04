package net.pedroksl.advanced_ae.common.helpers;

import java.util.LinkedHashMap;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import org.jetbrains.annotations.Nullable;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.stacks.AEKey;
import appeng.menu.locator.MenuHostLocator;

public interface AutoCraftingContainer {
    /**
     * Get the grid the container is currently connected to. Used to track if the container disconnects, so it can be
     * removed from the terminal.
     */
    @Nullable
    IGrid getGrid();

    /**
     * Get the locator of the attached block entity. Used to change pattern configurations.
     */
    @Nullable
    MenuHostLocator getLocator();

    /**
     * @return True if this container should be shown in the pattern access terminal.
     */
    default boolean isVisibleInTerminal() {
        return true;
    }

    /**
     * @return The inventory to store patterns in.
     */
    InternalInventory getTerminalPatternInventory();

    List<Boolean> getEnabledPatternSlots();

    List<Boolean> getInvalidPatternSlots();

    default long getTerminalSortOrder() {
        return 0;
    }

    void toggleEnablePattern(int slot);

    LinkedHashMap<AEKey, Long> getPatternConfigInputs(int index);

    Pair<AEKey, Long> getPatternConfigOutput(int index);

    void setStockAmount(int index, int inputIndex, long amount);

    void setMaxCrafted(int index, long amount);
}
