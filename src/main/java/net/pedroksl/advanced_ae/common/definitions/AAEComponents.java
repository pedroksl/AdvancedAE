package net.pedroksl.advanced_ae.common.definitions;

import java.util.function.Consumer;

import com.glodblock.github.glodium.util.GlodCodecs;
import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.patterns.EncodedAdvProcessingPattern;

public final class AAEComponents {
    public static final DeferredRegister<DataComponentType<?>> DR =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AdvancedAE.MOD_ID);

    public static final DataComponentType<EncodedAdvProcessingPattern> ENCODED_ADV_PROCESSING_PATTERN =
            register("encoded_adv_processing_pattern", builder -> builder.persistent(EncodedAdvProcessingPattern.CODEC)
                    .networkSynchronized(EncodedAdvProcessingPattern.STREAM_CODEC));
    public static final DataComponentType<CompoundTag> STACK_TAG =
            register("generic_nbt", builder -> builder.persistent(CompoundTag.CODEC)
                    .networkSynchronized(GlodCodecs.NBT_STREAM_CODEC));

    public static final DataComponentType<Boolean> WALK_SPEED_UPGRADE = register(
            "walk_speed_upgrade", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DataComponentType<Boolean> SPRINT_SPEED_UPGRADE = register(
            "sprint_speed_upgrade", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DataComponentType<Boolean> STEP_ASSIST_UPGRADE = register(
            "step_assist_upgrade", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DataComponentType<Boolean> JUMP_HEIGHT_UPGRADE = register(
            "jump_height_upgrade", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        DR.register(name, () -> componentType);
        return componentType;
    }
}
