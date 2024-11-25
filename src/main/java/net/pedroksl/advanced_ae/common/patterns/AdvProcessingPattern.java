package net.pedroksl.advanced_ae.common.patterns;

import java.util.HashMap;
import java.util.Objects;

import net.minecraft.core.Direction;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.crafting.pattern.AEProcessingPattern;

public class AdvProcessingPattern extends AEProcessingPattern implements AdvPatternDetails {

    private final HashMap<AEKey, Direction> dirMap;

    public AdvProcessingPattern(AEItemKey definition) {
        super(definition);

        var tag = Objects.requireNonNull(definition.getTag());

        this.dirMap = AdvPatternEncoding.getInputDirections(tag);
    }

    public HashMap<AEKey, Direction> getDirectionMap() {
        return dirMap;
    }

    @Override
    public boolean directionalInputsSet() {
        return dirMap != null && !dirMap.isEmpty();
    }

    @Override
    public Direction getDirectionSideForInputKey(AEKey key) {
        return this.dirMap.get(key);
    }

    @Override
    public void pushInputsToExternalInventory(KeyCounter[] inputHolder, IPatternDetails.PatternInputSink inputSink) {
        super.pushInputsToExternalInventory(inputHolder, inputSink);
    }
}
