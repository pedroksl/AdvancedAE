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

import appeng.api.stacks.GenericStack;
import appeng.core.definitions.AEItems;

public record EncodedAdvProcessingPattern(
        List<GenericStack> sparseInputs, List<GenericStack> sparseOutputs, List<NullableDirection> directionList) {
    public EncodedAdvProcessingPattern {
        sparseInputs = Collections.unmodifiableList(sparseInputs);
        sparseOutputs = Collections.unmodifiableList(sparseOutputs);
        directionList = Collections.unmodifiableList(directionList);
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
                            .forGetter(EncodedAdvProcessingPattern::directionList))
            .apply(builder, EncodedAdvProcessingPattern::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EncodedAdvProcessingPattern> STREAM_CODEC =
            StreamCodec.composite(
                    GenericStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    EncodedAdvProcessingPattern::sparseInputs,
                    GenericStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    EncodedAdvProcessingPattern::sparseOutputs,
                    NullableDirection.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    EncodedAdvProcessingPattern::directionList,
                    EncodedAdvProcessingPattern::new);

    public boolean containsMissingContent() {
        return Stream.concat(sparseInputs.stream(), sparseOutputs.stream())
                .anyMatch(stack -> stack != null && AEItems.MISSING_CONTENT.is(stack.what()));
    }
}
