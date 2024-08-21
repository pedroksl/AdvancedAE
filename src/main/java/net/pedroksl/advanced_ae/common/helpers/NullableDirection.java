package net.pedroksl.advanced_ae.common.helpers;

import java.util.List;
import java.util.function.IntFunction;

import com.mojang.serialization.Codec;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import io.netty.buffer.ByteBuf;

public enum NullableDirection implements StringRepresentable {
    DOWN(0, Direction.DOWN),
    UP(1, Direction.UP),
    NORTH(2, Direction.NORTH),
    SOUTH(3, Direction.SOUTH),
    WEST(4, Direction.WEST),
    EAST(5, Direction.EAST),
    NULLDIR(6, null);

    public static final IntFunction<NullableDirection> BY_ID =
            ByIdMap.continuous(NullableDirection::getIndex, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    private final int index;
    private final Direction dir;

    NullableDirection(int index, @Nullable Direction dir) {
        this.index = index;
        this.dir = dir;
    }

    public int getIndex() {
        return this.index;
    }

    public Direction getDirection() {
        return this.dir;
    }

    public static NullableDirection fromDirection(Direction dir) {
        if (dir == null) return NULLDIR;

        return switch (dir) {
            case Direction.DOWN -> DOWN;
            case Direction.UP -> UP;
            case Direction.NORTH -> NORTH;
            case Direction.SOUTH -> SOUTH;
            case Direction.WEST -> WEST;
            case Direction.EAST -> EAST;
        };
    }

    @SuppressWarnings("deprecation")
    public static final StringRepresentable.EnumCodec<NullableDirection> CODEC =
            StringRepresentable.fromEnum(NullableDirection::values);

    public static final StreamCodec<ByteBuf, NullableDirection> STREAM_CODEC =
            ByteBufCodecs.idMapper(BY_ID, NullableDirection::getIndex);

    public static final Codec<List<@Nullable NullableDirection>> FAULT_TOLERANT_NULLABLE_LIST_CODEC =
            new NullableDirectionListCodec(CODEC);

    @Override
    public @NotNull String getSerializedName() {
        if (this.dir == null) {
            return "null";
        }

        return this.dir.name();
    }
}
