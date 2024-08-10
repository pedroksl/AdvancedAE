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

import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.Objects;

/**
 * Helper functions to work with patterns, mostly related to (de)serialization.
 */
class AdvPatternEncoding {
	private static final String NBT_INPUT_DIRECTIONS = "dir";

	public static Direction[] getInputDirections(CompoundTag nbt) {
		Objects.requireNonNull(nbt, "Pattern must have a tag.");

		ListTag tag = nbt.getList(NBT_INPUT_DIRECTIONS, Tag.TAG_COMPOUND);
		Preconditions.checkArgument(tag.size() <= 9, "Cannot use more than 9 ingredients");

		var result = new Direction[tag.size()];
		for (int x = 0; x < tag.size(); ++x) {
			var intTag = tag.getInt(x);
			if (intTag != -1) {
				result[x] = Direction.from3DDataValue(tag.getInt(x));
			}
		}
		return result;
	}

	public static void encodeDirectionList(CompoundTag tag, Direction[] sides) {
		tag.put(NBT_INPUT_DIRECTIONS, encodeDirectionList(sides));
	}

	private static ListTag encodeDirectionList(Direction[] directions) {
		ListTag tag = new ListTag();
		for (var direction : directions) {
			if (direction == null) {
				tag.add(IntTag.valueOf(-1));
			} else {
				tag.add(IntTag.valueOf(direction.get3DDataValue()));
			}
		}
		return tag;
	}
}
