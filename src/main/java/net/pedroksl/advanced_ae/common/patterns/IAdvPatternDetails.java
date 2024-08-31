package net.pedroksl.advanced_ae.common.patterns;

import java.util.HashMap;

import net.minecraft.core.Direction;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;

public interface IAdvPatternDetails {
    boolean directionalInputsSet();

    HashMap<AEKey, Direction> getDirectionMap();

    Direction getDirectionSideForInputKey(AEKey key);

    void pushInputsToExternalInventory(KeyCounter[] inputHolder, IPatternDetails.PatternInputSink inputSink);
}
