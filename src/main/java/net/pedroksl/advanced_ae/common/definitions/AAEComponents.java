package net.pedroksl.advanced_ae.common.definitions;

import java.util.*;
import java.util.function.Consumer;

import com.glodblock.github.glodium.util.GlodCodecs;
import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
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

    public static final DataComponentType<List<TagKey<Item>>> TAG_FILTER =
            register("tag_filter", builder -> builder.persistent(Codec.list(TagKey.codec(Registries.ITEM)))
                    .networkSynchronized(ByteBufCodecs.fromCodec(Codec.list(TagKey.codec(Registries.ITEM)))));
    public static final Map<UpgradeType, DataComponentType<Boolean>> UPGRADE_TOGGLE = new HashMap<>();
    public static final Map<UpgradeType, DataComponentType<Integer>> UPGRADE_VALUE = new HashMap<>();

    public static void init() {
        for (var upgrade : UpgradeType.values()) {
            DataComponentType<Boolean> toggle =
                    register(upgrade.name() + "_toggle", builder -> builder.persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL));
            DataComponentType<Integer> value =
                    register(upgrade.name() + "_value", builder -> builder.persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT));
            UPGRADE_TOGGLE.put(upgrade, toggle);
            UPGRADE_VALUE.put(upgrade, value);
        }
    }

    private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        DR.register(name, () -> componentType);
        return componentType;
    }
}
