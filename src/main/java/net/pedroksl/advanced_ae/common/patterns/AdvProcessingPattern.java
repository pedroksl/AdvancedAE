package net.pedroksl.advanced_ae.common.patterns;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.crafting.pattern.AEProcessingPattern;
import net.minecraft.core.Direction;

import java.util.Objects;

public class AdvProcessingPattern extends AEProcessingPattern implements AdvPatternDetails {

	private final Direction[] fromSides;

	public AdvProcessingPattern(AEItemKey definition) {
		super(definition);

		var tag = Objects.requireNonNull(definition.getTag());

		this.fromSides = AdvPatternEncoding.getProcessingInputDirections(tag);
	}

	@Override
	public boolean directionalInputsSet() {
		for (Direction side : fromSides) {
			if (side != null) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Direction getDirectionSideForInputSlot(int pIndex) {
		return fromSides[pIndex];
	}

	@Override
	public void pushInputsToExternalInventory(KeyCounter inputList, IPatternDetails.PatternInputSink inputSink) {
		var sparceInputs = this.getSparseInputs();
		for (var sparseInput : sparceInputs) {
			if (sparseInput == null) {
				continue;
			}

			var key = sparseInput.what();
			var amount = sparseInput.amount();
			long available = inputList.get(key);

			if (available < amount) {
				throw new RuntimeException("Expected at least %d of %s when pushing pattern, but only %d available"
						.formatted(amount, key, available));
			}

			inputSink.pushInput(key, amount);
			inputList.remove(key, amount);
		}
	}
}
