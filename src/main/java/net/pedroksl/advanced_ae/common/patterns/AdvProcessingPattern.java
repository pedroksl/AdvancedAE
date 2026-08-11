package net.pedroksl.advanced_ae.common.patterns;

import java.util.*;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.ae2addonlib.util.NullableDirection;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.crafting.PatternDetailsTooltip;
import appeng.api.ids.AEComponents;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.crafting.pattern.AEProcessingPattern;

public class AdvProcessingPattern implements IPatternDetails, IAdvPatternDetails {

    private final AEItemKey definition;
    private final List<GenericStack> sparseInputs, sparseOutputs;
    private final AdvProcessingPattern.Input[] inputs;
    private final List<GenericStack> condensedOutputs;
    private final LinkedHashMap<AEKey, Direction> dirMap = new LinkedHashMap<>();
    /**
     * One entry per distinct input key, holding the target slot chosen for EACH occurrence of that key in the
     * original (uncondensed) sparse input list, in row order. E.g. two separate deepslate rows targeting slots 2
     * and 3 respectively become {@code deepslate -> [2, 3]}. A missing/short list or {@code NO_SLOT} entries fall
     * back to the default any-slot insertion for that occurrence's share of the amount.
     */
    private final LinkedHashMap<AEKey, List<Integer>> slotMap = new LinkedHashMap<>();

    public AdvProcessingPattern(AEItemKey definition) {
        this.definition = definition;

        EncodedAdvProcessingPattern encodedPattern = definition.get(AAEComponents.ENCODED_ADV_PROCESSING_PATTERN);
        if (encodedPattern == null) {
            throw new IllegalArgumentException(
                    "Given item does not encode an advanced processing pattern: " + definition);
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
                this.dirMap.put(input.what(), direction.getDirection());
            }
        }
        for (var entry : encodedPattern.slotEntries()) {
            this.slotMap.put(entry.key(), new ArrayList<>(entry.slots()));
        }
    }

    public static void encode(
            ItemStack stack,
            List<GenericStack> sparseInputs,
            List<GenericStack> sparseOutputs,
            @Nullable HashMap<AEKey, Direction> dirMap) {
        encode(stack, sparseInputs, sparseOutputs, dirMap, null);
    }

    public static void encode(
            ItemStack stack,
            List<GenericStack> sparseInputs,
            List<GenericStack> sparseOutputs,
            @Nullable HashMap<AEKey, Direction> dirMap,
            @Nullable HashMap<AEKey, List<Integer>> slotMap) {
        if (sparseInputs.stream().noneMatch(Objects::nonNull)) {
            throw new IllegalArgumentException("At least one input must be non-null.");
        } else {
            Objects.requireNonNull(sparseOutputs.getFirst(), "The first (primary) output must be non-null.");

            NullableDirection[] nullDirArray = new NullableDirection[sparseInputs.size()];
            Arrays.fill(nullDirArray, NullableDirection.NULLDIR);
            List<NullableDirection> directionList = Arrays.asList(nullDirArray);

            for (var x = 0; x < sparseInputs.size(); x++) {
                var input = sparseInputs.get(x);
                if (input == null) {
                    continue;
                }
                if (dirMap != null) {
                    directionList.set(x, NullableDirection.fromDirection(dirMap.get(input.what())));
                }
            }

            List<EncodedAdvProcessingPattern.KeySlots> slotEntries = List.of();
            if (slotMap != null) {
                slotEntries = slotMap.entrySet().stream()
                        .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                        .map(e -> new EncodedAdvProcessingPattern.KeySlots(e.getKey(), e.getValue()))
                        .toList();
            }

            stack.set(
                    AAEComponents.ENCODED_ADV_PROCESSING_PATTERN,
                    new EncodedAdvProcessingPattern(sparseInputs, sparseOutputs, directionList, slotEntries));
        }
    }

    public AEProcessingPattern getAEProcessingPattern(Level level) {
        var stack = PatternDetailsHelper.encodeProcessingPattern(this.getSparseInputs(), this.getSparseOutputs());
        if (stack == null) return null;

        var pattern = PatternDetailsHelper.decodePattern(stack, level);
        if (!(pattern instanceof AEProcessingPattern aePattern)) {
            return null;
        }

        return aePattern;
    }

    @Override
    public int hashCode() {
        return definition.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
                && obj.getClass() == getClass()
                && ((AdvProcessingPattern) obj).definition.equals(definition);
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

    public LinkedHashMap<AEKey, Direction> getDirectionMap() {
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
    public LinkedHashMap<AEKey, List<Integer>> getSlotMap() {
        return slotMap;
    }

    @Override
    public List<Integer> getSlotsForInputKey(AEKey key) {
        return this.slotMap.getOrDefault(key, List.of());
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

    public static PatternDetailsTooltip getInvalidPatternTooltip(
            ItemStack stack, Level level, @Nullable Exception cause, TooltipFlag flags) {
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
            this.template = new GenericStack[] {new GenericStack(stack.what(), 1)};
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

        @Nullable
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
