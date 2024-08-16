package net.pedroksl.advanced_ae.common.patterns;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsTooltip;
import appeng.api.ids.AEComponents;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.AAESingletons;

import javax.annotation.Nullable;
import java.util.*;

public class AdvProcessingPattern implements IPatternDetails, AdvPatternDetails {

	private final AEItemKey definition;
	private final List<GenericStack> sparseInputs, sparseOutputs;
	private final AdvProcessingPattern.Input[] inputs;
	private final List<GenericStack> condensedOutputs;
	private final HashMap<AEKey, Direction> dirMap = new HashMap<>();

	public AdvProcessingPattern(AEItemKey definition) {
		this.definition = definition;

		EncodedAdvProcessingPattern encodedPattern = definition.get(AAESingletons.ENCODED_ADV_PROCESSING_PATTERN);
		if (encodedPattern == null) {
			throw new IllegalArgumentException("Given item does not encode an advanced processing pattern: " + definition);
		} else if (encodedPattern.containsMissingContent()) {
			throw new IllegalArgumentException("Pattern references missing content");
		}
		this.sparseInputs = encodedPattern.sparseInputs();
		this.sparseOutputs = encodedPattern.sparseOutputs();
		var condensedInputs = condenseStacks(sparseInputs);
		this.inputs = new AdvProcessingPattern.Input[condensedInputs.size()];
		for (int i = 0; i < inputs.length; ++i) {
			inputs[i] = new AdvProcessingPattern.Input(condensedInputs.get(i));
		}

		// Ordering is preserved by condenseStacks
		this.condensedOutputs = condenseStacks(sparseOutputs);

		var inputs = encodedPattern.sparseInputs();
		var directions = encodedPattern.directionList();
		for (var x = 0; x < inputs.size(); x++) {
			var input = inputs.get(x);
			var direction = directions.get(x);

			if (input != null) {
				this.dirMap.put(input.what(), direction);
			}
		}
	}

	public static void encode(ItemStack stack, List<GenericStack> sparseInputs, List<GenericStack> sparseOutputs,
	                          @Nullable HashMap<AEKey, Direction> dirMap) {
		if (sparseInputs.stream().noneMatch(Objects::nonNull)) {
			throw new IllegalArgumentException("At least one input must be non-null.");
		} else {
			Objects.requireNonNull(sparseOutputs.get(0), "The first (primary) output must be non-null.");

			List<Direction> directionList = new ArrayList<>(sparseInputs.size());
			if (dirMap != null) {
				for (var input : sparseInputs) {
					if (input == null) {
						directionList.add(null);
					} else {
						directionList.add(dirMap.get(input.what()));
					}
				}
			}
			stack.set(AAESingletons.ENCODED_ADV_PROCESSING_PATTERN, new EncodedAdvProcessingPattern(sparseInputs,
					sparseOutputs, directionList));
		}
	}

	@Override
	public int hashCode() {
		return definition.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == getClass() && ((AdvProcessingPattern) obj).definition.equals(definition);
	}

	@Override
	public AEItemKey getDefinition() {
		return definition;
	}

	@Override
	public IInput[] getInputs() {
		return inputs;
	}

	@Override
	public List<GenericStack> getOutputs() {
		return condensedOutputs;
	}

	public List<GenericStack> getSparseInputs() {
		return sparseInputs;
	}

	public List<GenericStack> getSparseOutputs() {
		return sparseOutputs;
	}

	public HashMap<AEKey, Direction> getDirectionMap() {
		return dirMap;
	}

	@Override
	public boolean directionalInputsSet() {
		return !dirMap.isEmpty();
	}

	@Override
	public Direction getDirectionSideForInputKey(AEKey key) {
		return this.dirMap.get(key);
	}

	@Override
	public void pushInputsToExternalInventory(KeyCounter[] inputHolder, PatternInputSink inputSink) {
		if (sparseInputs.size() == inputs.length) {
			// No compression -> no need to reorder
			IPatternDetails.super.pushInputsToExternalInventory(inputHolder, inputSink);
			return;
		}

		var allInputs = new KeyCounter();
		for (var counter : inputHolder) {
			allInputs.addAll(counter);
		}

		// Push according to sparse input order
		for (var sparseInput : sparseInputs) {
			if (sparseInput == null) {
				continue;
			}

			var key = sparseInput.what();
			var amount = sparseInput.amount();
			long available = allInputs.get(key);

			if (available < amount) {
				throw new RuntimeException("Expected at least %d of %s when pushing pattern, but only %d available"
						.formatted(amount, key, available));
			}

			inputSink.pushInput(key, amount);
			allInputs.remove(key, amount);
		}
	}

	public static PatternDetailsTooltip getInvalidPatternTooltip(ItemStack stack, Level level,
	                                                             @org.jetbrains.annotations.Nullable Exception cause, TooltipFlag flags) {
		var tooltip = new PatternDetailsTooltip(PatternDetailsTooltip.OUTPUT_TEXT_PRODUCES);

		var encodedPattern = stack.get(AEComponents.ENCODED_PROCESSING_PATTERN);
		if (encodedPattern != null) {
			encodedPattern.sparseInputs().stream().filter(Objects::nonNull).forEach(tooltip::addInput);
			encodedPattern.sparseOutputs().stream().filter(Objects::nonNull).forEach(tooltip::addOutput);
		}

		return tooltip;
	}

	private static class Input implements IInput {
		private final GenericStack[] template;
		private final long multiplier;

		private Input(GenericStack stack) {
			this.template = new GenericStack[]{new GenericStack(stack.what(), 1)};
			this.multiplier = stack.amount();
		}

		@Override
		public GenericStack[] getPossibleInputs() {
			return template;
		}

		@Override
		public long getMultiplier() {
			return multiplier;
		}

		@Override
		public boolean isValid(AEKey input, Level level) {
			return input.matches(template[0]);
		}

		@org.jetbrains.annotations.Nullable
		@Override
		public AEKey getRemainingKey(AEKey template) {
			return null;
		}
	}

	private static ListTag encodeStackList(GenericStack[] stacks, HolderLookup.Provider registries) {
		ListTag tag = new ListTag();
		boolean foundStack = false;
		for (var stack : stacks) {
			tag.add(GenericStack.writeTag(registries, stack));
			if (stack != null && stack.amount() > 0) {
				foundStack = true;
			}
		}
		Preconditions.checkArgument(foundStack, "List passed to pattern must contain at least one stack.");
		return tag;
	}

	private static List<GenericStack> condenseStacks(List<GenericStack> sparseInput) {
		// Use a linked map to preserve ordering.
		var map = new LinkedHashMap<AEKey, Long>();

		for (var input : sparseInput) {
			if (input != null) {
				map.merge(input.what(), input.amount(), Long::sum);
			}
		}

		if (map.isEmpty()) {
			throw new IllegalStateException("No pattern here!");
		}

		List<GenericStack> out = new ArrayList<>(map.size());
		for (var entry : map.entrySet()) {
			out.add(new GenericStack(entry.getKey(), entry.getValue()));
		}
		return out;
	}
}
