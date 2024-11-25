package net.pedroksl.advanced_ae.common.patterns;

import java.util.HashMap;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;

public final class AdvPatternDetailsEncoder {

    public static ItemStack encodeProcessingPattern(
            GenericStack[] sparseInputs, GenericStack[] sparseOutputs, HashMap<AEKey, Direction> dirMap) {
        ItemStack pattern = AAEItems.ADV_PROCESSING_PATTERN.stack();
        AdvPatternEncoding.encodeProcessingPattern(pattern.getOrCreateTag(), sparseInputs, sparseOutputs, dirMap);
        return pattern;
    }
}
