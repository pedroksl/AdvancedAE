package net.pedroksl.advanced_ae.common.patterns;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import net.minecraft.core.Direction;

public interface AdvPatternDetails {
	boolean directionalInputsSet();

	Direction getDirectionSideForInputKey(AEKey key);

	void pushInputsToExternalInventory(KeyCounter[] inputHolder, IPatternDetails.PatternInputSink inputSink);
}
