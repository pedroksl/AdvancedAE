package net.pedroksl.advanced_ae.common.patterns;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import net.minecraft.core.Direction;

import java.util.HashMap;

public interface AdvPatternDetails {
	boolean directionalInputsSet();

	HashMap<AEKey, Direction> getDirectionMap();

	Direction getDirectionSideForInputKey(AEKey key);

	void pushInputsToExternalInventory(KeyCounter[] inputHolder, IPatternDetails.PatternInputSink inputSink);
}
