package net.pedroksl.advanced_ae.common.patterns;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.pedroksl.ae2addonlib.util.NullableDirection;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.core.definitions.AEItems;

public record EncodedAdvProcessingPattern(
        List<GenericStack> sparseInputs,
        List<GenericStack> sparseOutputs,
        List<NullableDirection> directionList,
        List<KeySlots> slotEntries) {
    /** Sentinel value meaning "no specific slot chosen, use default any-slot insertion". */
    public static final int NO_SLOT = -1;

    /**
     * Target slots configured for a given input key, independent of how many {@code sparseInputs} rows that key
     * occupies — a recipe that needs "2x deepslate" is usually a SINGLE row with amount 2, not two separate rows,
     * so the slot list can't be tied 1:1 to row count. Each entry in {@code slots} is one target slot; amount is
     * split across them in order when the pattern is pushed (see AdvPatternProviderLogic#splitAmount).
     */
    public record KeySlots(AEKey key, List<Integer> slots) {
        public static final Codec<KeySlots> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                        AEKey.CODEC.fieldOf("key").forGetter(KeySlots::key),
                        Codec.INT.listOf().fieldOf("slots").forGetter(KeySlots::slots))
                .apply(builder, KeySlots::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, KeySlots> STREAM_CODEC = StreamCodec.composite(
                AEKey.STREAM_CODEC,
                KeySlots::key,
                ByteBufCodecs.INT.apply(ByteBufCodecs.list()),
                KeySlots::slots,
                KeySlots::new);
    }

    public EncodedAdvProcessingPattern {
        sparseInputs = Collections.unmodifiableList(sparseInputs);
        sparseOutputs = Collections.unmodifiableList(sparseOutputs);
        directionList = Collections.unmodifiableList(directionList);
        slotEntries = Collections.unmodifiableList(slotEntries);
    }

    public static final Codec<EncodedAdvProcessingPattern> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                    GenericStack.FAULT_TOLERANT_NULLABLE_LIST_CODEC
                            .fieldOf("sparseInputs")
                            .forGetter(EncodedAdvProcessingPattern::sparseInputs),
                    GenericStack.FAULT_TOLERANT_NULLABLE_LIST_CODEC
                            .fieldOf("sparseOutputs")
                            .forGetter(EncodedAdvProcessingPattern::sparseOutputs),
                    NullableDirection.FAULT_TOLERANT_NULLABLE_LIST_CODEC
                            .fieldOf("directionMap")
                            .forGetter(EncodedAdvProcessingPattern::directionList),
                    KeySlots.CODEC
                            .listOf()
                            .optionalFieldOf("slotMap", List.of())
                            .forGetter(EncodedAdvProcessingPattern::slotEntries))
            .apply(builder, EncodedAdvProcessingPattern::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EncodedAdvProcessingPattern> STREAM_CODEC =
            StreamCodec.composite(
                    GenericStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    EncodedAdvProcessingPattern::sparseInputs,
                    GenericStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    EncodedAdvProcessingPattern::sparseOutputs,
                    NullableDirection.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    EncodedAdvProcessingPattern::directionList,
                    KeySlots.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    EncodedAdvProcessingPattern::slotEntries,
                    EncodedAdvProcessingPattern::new);

    public boolean containsMissingContent() {
        return Stream.concat(sparseInputs.stream(), sparseOutputs.stream())
                .anyMatch(stack -> stack != null && AEItems.MISSING_CONTENT.is(stack.what()));
    }
}
