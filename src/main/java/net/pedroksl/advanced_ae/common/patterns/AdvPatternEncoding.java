/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2021, TeamAppliedEnergistics, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package net.pedroksl.advanced_ae.common.patterns;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.Objects;

/**
 * Helper functions to work with patterns, mostly related to (de)serialization.
 */
class AdvPatternEncoding {
	private static final String NBT_INPUTS = "in";
	private static final String NBT_OUTPUTS = "out";
	private static final String NBT_INPUT_DIRECTIONS = "dir";

	public static GenericStack[] getProcessingInputs(CompoundTag nbt) {
		return getMixedList(nbt, "in", 81);
	}

	public static GenericStack[] getProcessingOutputs(CompoundTag nbt) {
		return getMixedList(nbt, "out", 27);
	}

	public static GenericStack[] getMixedList(CompoundTag nbt, String nbtKey, int maxSize) {
		Objects.requireNonNull(nbt, "Pattern must have a tag.");
		ListTag tag = nbt.getList(nbtKey, 10);
		Preconditions.checkArgument(tag.size() <= maxSize, "Cannot use more than " + maxSize + " ingredients");
		GenericStack[] result = new GenericStack[tag.size()];

		for (int x = 0; x < tag.size(); ++x) {
			CompoundTag entry = tag.getCompound(x);
			if (!entry.isEmpty()) {
				GenericStack stack = GenericStack.readTag(entry);
				if (stack == null) {
					throw new IllegalArgumentException("Pattern references missing stack: " + entry);
				}

				result[x] = stack;
			}
		}

		return result;
	}

	public static HashMap<AEKey, Direction> getInputDirections(CompoundTag nbt) {
		Objects.requireNonNull(nbt, "Pattern must have a tag.");

		ListTag tag = nbt.getList(NBT_INPUT_DIRECTIONS, Tag.TAG_COMPOUND);
		Preconditions.checkArgument(tag.size() <= 9, "Cannot use more than 9 ingredients");

		HashMap<AEKey, Direction> dirMap = new HashMap<>();
		for (int x = 0; x < tag.size(); x++) {
			CompoundTag compTag = tag.getCompound(x);
			AEKey key = AEKey.fromTagGeneric(compTag.getCompound("aekey"));

			var intTag = compTag.getInt("dir");
			Direction dir = intTag == -1 ? null : Direction.from3DDataValue(intTag);

			dirMap.put(key, dir);
		}
		return dirMap;
	}

	public static void encodeProcessingPattern(CompoundTag tag, GenericStack[] sparseInputs,
	                                           GenericStack[] sparseOutputs, HashMap<AEKey, Direction> dirMap) {
		tag.put(NBT_INPUTS, encodeStackList(sparseInputs));
		tag.put(NBT_OUTPUTS, encodeStackList(sparseOutputs));
		tag.put(NBT_INPUT_DIRECTIONS, encodeDirectionList(dirMap));
	}

	private static ListTag encodeStackList(GenericStack[] stacks) {
		ListTag tag = new ListTag();
		boolean foundStack = false;
		GenericStack[] var3 = stacks;
		int var4 = stacks.length;

		for (int var5 = 0; var5 < var4; ++var5) {
			GenericStack stack = var3[var5];
			tag.add(GenericStack.writeTag(stack));
			if (stack != null && stack.amount() > 0L) {
				foundStack = true;
			}
		}

		Preconditions.checkArgument(foundStack, "List passed to pattern must contain at least one stack.");
		return tag;
	}

	private static ListTag encodeDirectionList(HashMap<AEKey, Direction> dirMap) {
		ListTag tag = new ListTag();
		for (var entry : dirMap.entrySet()) {
			CompoundTag dirTag = new CompoundTag();
			dirTag.put("aekey", entry.getKey().toTagGeneric());
			Direction dir = entry.getValue();
			if (dir == null) {
				dirTag.put("dir", IntTag.valueOf(-1));
			} else {
				dirTag.put("dir", IntTag.valueOf(dir.get3DDataValue()));
			}
			tag.add(dirTag);
		}
		return tag;
	}
}
