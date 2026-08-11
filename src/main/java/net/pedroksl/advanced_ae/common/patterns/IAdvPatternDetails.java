package net.pedroksl.advanced_ae.common.patterns;

import java.util.HashMap;
import java.util.List;

import net.minecraft.core.Direction;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;

public interface IAdvPatternDetails {
    boolean directionalInputsSet();

    HashMap<AEKey, Direction> getDirectionMap();

    Direction getDirectionSideForInputKey(AEKey key);

    /**
     * @return per-input target slot map. Each value is the ordered list of slots chosen for every occurrence of
     * that key in the pattern (so a repeated ingredient like two separate deepslate rows can target two different
     * slots). A missing entry, empty list, or {@code -1} entry means "no specific slot for that occurrence, insert
     * into any slot".
     */
    default HashMap<AEKey, List<Integer>> getSlotMap() {
        return new HashMap<>();
    }

    /**
     * @return the ordered list of target slots configured for each occurrence of this input key, or an empty list
     * if no specific slot was chosen (fall back to default any-slot insertion).
     */
    default List<Integer> getSlotsForInputKey(AEKey key) {
        return List.of();
    }

    void pushInputsToExternalInventory(KeyCounter[] inputHolder, IPatternDetails.PatternInputSink inputSink);
}
