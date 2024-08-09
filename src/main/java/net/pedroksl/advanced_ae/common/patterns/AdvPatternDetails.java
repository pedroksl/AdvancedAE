package net.pedroksl.advanced_ae.common.patterns;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.KeyCounter;
import net.minecraft.core.Direction;

public interface AdvPatternDetails {
	boolean directionalInputsSet();

	Direction getDirectionSideForInputSlot(int pIndex);

	void pushInputsToExternalInventory(KeyCounter inputList, IPatternDetails.PatternInputSink inputSink);
}
