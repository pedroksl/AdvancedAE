package net.pedroksl.advanced_ae.common.patterns;

import appeng.api.stacks.GenericStack;
import appeng.core.definitions.AEItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.pedroksl.advanced_ae.common.helpers.NullableDirection;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public record EncodedAdvProcessingPattern(
		List<GenericStack> sparseInputs,
		List<GenericStack> sparseOutputs,
		List<NullableDirection> directionList) {
	public EncodedAdvProcessingPattern {
		sparseInputs = Collections.unmodifiableList(sparseInputs);
		sparseOutputs = Collections.unmodifiableList(sparseOutputs);
		directionList = Collections.unmodifiableList(directionList);
	}

	public static final Codec<EncodedAdvProcessingPattern> CODEC = RecordCodecBuilder.create(builder -> builder.group(
					GenericStack.FAULT_TOLERANT_NULLABLE_LIST_CODEC.fieldOf("sparseInputs")
							.forGetter(EncodedAdvProcessingPattern::sparseInputs),
					GenericStack.FAULT_TOLERANT_NULLABLE_LIST_CODEC.fieldOf("sparseOutputs")
							.forGetter(EncodedAdvProcessingPattern::sparseOutputs),
					NullableDirection.FAULT_TOLERANT_NULLABLE_LIST_CODEC.fieldOf("directionMap")
							.forGetter(EncodedAdvProcessingPattern::directionList))
			.apply(builder, EncodedAdvProcessingPattern::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EncodedAdvProcessingPattern> STREAM_CODEC = StreamCodec
			.composite(
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
//	@Override
//	public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag advancedTooltips) {
//		if (!stack.hasTag()) {
//			// This can be called very early to index tooltips for search. In those cases,
//			// there is no encoded pattern present.
//			return;
//		}
//
//		var details = decode(stack, level, false);
//		if (details == null) {
//			super.appendHoverText(stack, level, lines, advancedTooltips);
//			return;
//		}
//
//		if (stack.hasCustomHoverName()) {
//			stack.resetHoverName();
//		}
//
//		var in = details.getInputs();
//		var out = details.getOutputs();
//
//		var label = GuiText.Produces.text().copy().append(": ").withStyle(ChatFormatting.GRAY);
//		var and = Component.literal(" ").copy().append(GuiText.And.text())
//				.append(" ").withStyle(ChatFormatting.GRAY);
//		var with = GuiText.With.text().copy().append(": ").withStyle(ChatFormatting.GRAY);
//
//		boolean first = true;
//		for (var anOut : out) {
//			if (anOut == null) {
//				continue;
//			}
//
//			lines.add(Component.empty().append(first ? label : and).append(getStackComponent(anOut)));
//			first = false;
//		}
//
//		first = true;
//		for (var anIn : in) {
//			if (anIn == null) {
//				continue;
//			}
//
//			var primaryInputTemplate = anIn.getPossibleInputs()[0];
//			var primaryInput = new GenericStack(primaryInputTemplate.what(),
//					primaryInputTemplate.amount() * anIn.getMultiplier());
//			var inputDirection = details.getDirectionSideForInputKey(primaryInputTemplate.what());
//			var dirText = inputDirection == null ? "" : " (" + inputDirection.toString().toUpperCase().charAt(0) + ")";
//			lines.add(Component.empty().append(first ? with : and).append(getStackComponent(primaryInput)).append(dirText));
//			first = false;
//		}
//	}
