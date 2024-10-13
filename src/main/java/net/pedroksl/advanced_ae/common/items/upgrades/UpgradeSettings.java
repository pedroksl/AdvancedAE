package net.pedroksl.advanced_ae.common.items.upgrades;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class UpgradeSettings {

    public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeSettings> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            UpgradeSettings::getMinValue,
            ByteBufCodecs.INT,
            UpgradeSettings::getMaxValue,
            ByteBufCodecs.FLOAT,
            UpgradeSettings::getMultiplier,
            ByteBufCodecs.INT,
            UpgradeSettings::getDefaultValue,
            UpgradeSettings::new);

    public int minValue;
    public int maxValue;
    public float multiplier = 1;
    public int defaultValue;

    public UpgradeSettings(int value) {
        this(value, value);
    }

    public UpgradeSettings(int min, int max) {
        this(min, max, 1);
    }

    public UpgradeSettings(int min, int max, float multiplier) {
        this(min, max, multiplier, max);
    }

    public UpgradeSettings(int min, int max, float multiplier, int defaultValue) {
        this.minValue = min;
        this.maxValue = max;
        this.multiplier = multiplier;
        this.defaultValue = defaultValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public int getDefaultValue() {
        return defaultValue;
    }
}
