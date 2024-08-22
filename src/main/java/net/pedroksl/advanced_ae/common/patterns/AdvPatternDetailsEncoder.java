package net.pedroksl.advanced_ae.common.patterns;

import java.util.HashMap;
import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;

public final class AdvPatternDetailsEncoder {

    public static ItemStack encodeProcessingPattern(
            List<GenericStack> sparseInputs, List<GenericStack> sparseOutputs, HashMap<AEKey, Direction> dirMap) {
        ItemStack stack = new ItemStack(AAEItems.ADV_PROCESSING_PATTERN);
        AdvProcessingPattern.encode(stack, sparseInputs, sparseOutputs, dirMap);
        return stack;
    }
}
