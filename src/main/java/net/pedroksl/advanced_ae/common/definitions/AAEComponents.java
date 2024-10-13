package net.pedroksl.advanced_ae.common.definitions;

import java.util.*;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.common.patterns.EncodedAdvProcessingPattern;

import appeng.api.stacks.GenericStack;

public final class AAEComponents {
    public static final DeferredRegister<DataComponentType<?>> DR =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AdvancedAE.MOD_ID);

    public static final DataComponentType<EncodedAdvProcessingPattern> ENCODED_ADV_PROCESSING_PATTERN =
            register("encoded_adv_processing_pattern", builder -> builder.persistent(EncodedAdvProcessingPattern.CODEC)
                    .networkSynchronized(EncodedAdvProcessingPattern.STREAM_CODEC));
    public static final DataComponentType<CompoundTag> STACK_TAG =
            register("generic_nbt", builder -> builder.persistent(CompoundTag.CODEC)
                    .networkSynchronized(ByteBufCodecs.COMPOUND_TAG));
    public static final DataComponentType<Integer> INT_TAG =
            register("generic_int", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final Map<UpgradeType, DataComponentType<Boolean>> UPGRADE_TOGGLE = new HashMap<>();
    public static final Map<UpgradeType, DataComponentType<Integer>> UPGRADE_VALUE = new HashMap<>();
    public static final Map<UpgradeType, DataComponentType<List<GenericStack>>> UPGRADE_FILTER = new HashMap<>();
    public static final Map<UpgradeType, DataComponentType<Boolean>> UPGRADE_EXTRA = new HashMap<>();

    public static void init() {
        for (var upgrade : UpgradeType.values()) {
            DataComponentType<Boolean> toggle =
                    register(upgrade.name() + "_toggle", builder -> builder.persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL));
            DataComponentType<Integer> value =
                    register(upgrade.name() + "_value", builder -> builder.persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT));
            DataComponentType<List<GenericStack>> filter =
                    register(upgrade.name() + "_filter", builder -> builder.persistent(Codec.list(GenericStack.CODEC))
                            .networkSynchronized(GenericStack.STREAM_CODEC.apply(ByteBufCodecs.list())));
            UPGRADE_TOGGLE.put(upgrade, toggle);
            UPGRADE_VALUE.put(upgrade, value);
            UPGRADE_FILTER.put(upgrade, filter);
            if (upgrade.getExtraSettings() != UpgradeType.ExtraSettings.NONE) {
                DataComponentType<Boolean> extra =
                        register(upgrade.name() + "_extra", builder -> builder.persistent(Codec.BOOL)
                                .networkSynchronized(ByteBufCodecs.BOOL));
                UPGRADE_EXTRA.put(upgrade, extra);
            }
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
