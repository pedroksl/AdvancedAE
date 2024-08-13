package net.pedroksl.advanced_ae.common.patterns;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.IPatternDetailsDecoder;
import appeng.api.stacks.AEItemKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AdvPatternDetailsDecoder implements IPatternDetailsDecoder {

	public static final AdvPatternDetailsDecoder INSTANCE = new AdvPatternDetailsDecoder();

	@Override
	public boolean isEncodedPattern(ItemStack stack) {
		return stack.getItem() instanceof AdvProcessingPatternItem;
	}

	@Nullable
	@Override
	public IPatternDetails decodePattern(AEItemKey what, Level level) {
		if (level == null || !(what.getItem() instanceof AdvProcessingPatternItem advProcessingPatternItem)) {
			return null;
		}

		return advProcessingPatternItem.decode(what, level);
	}

	@Nullable
	@Override
	public IPatternDetails decodePattern(ItemStack what, Level level, boolean tryRecovery) {
		if (level == null || !(what.getItem() instanceof AdvProcessingPatternItem advProcessingPatternItem)) {
			return null;
		}

		return advProcessingPatternItem.decode(what, level, tryRecovery);
	}
}
