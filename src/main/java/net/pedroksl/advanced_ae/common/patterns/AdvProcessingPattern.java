package net.pedroksl.advanced_ae.common.patterns;

import appeng.api.stacks.AEItemKey;
import appeng.crafting.pattern.AEProcessingPattern;
import net.minecraft.core.Direction;

import java.util.Objects;

public class AdvProcessingPattern extends AEProcessingPattern {

	private final Direction[] fromSides;

	public AdvProcessingPattern(AEItemKey definition) {
		super(definition);

		var tag = Objects.requireNonNull(definition.getTag());

		this.fromSides = AdvProcessingPatternEncoding.getProcessingInputDirections(tag);
	}

	public boolean directionalInputsSet() {
		for (Direction side : fromSides) {
			if (side != null) {
				return true;
			}
		}

		return false;
	}
}
