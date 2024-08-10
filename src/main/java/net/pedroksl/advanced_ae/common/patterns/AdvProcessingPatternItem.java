package net.pedroksl.advanced_ae.common.patterns;

import appeng.api.stacks.AEItemKey;
import appeng.crafting.pattern.ProcessingPatternItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AdvProcessingPatternItem extends ProcessingPatternItem {
	public AdvProcessingPatternItem(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public AdvProcessingPattern decode(ItemStack stack, Level level, boolean tryRecovery) {
		return decode(AEItemKey.of(stack), level);
	}

	@Override
	public AdvProcessingPattern decode(AEItemKey what, Level level) {
		if (what == null || !what.hasTag()) {
			return null;
		}

		try {
			return new AdvProcessingPattern(what);
		} catch (Exception e) {
			return null;
		}
	}
}
