package net.pedroksl.advanced_ae.common.items.upgrades;

import net.minecraft.network.FriendlyByteBuf;

public class UpgradeSettings {

    public int minValue;
    public int maxValue;
    public float multiplier;
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

    public static UpgradeSettings fromBytes(FriendlyByteBuf stream) {
        var minValue = stream.readInt();
        var maxValue = stream.readInt();
        var multiplier = stream.readFloat();
        var defaultValue = stream.readInt();

        return new UpgradeSettings(minValue, maxValue, multiplier, defaultValue);
    }

    public void toBytes(FriendlyByteBuf data) {
        data.writeInt(minValue);
        data.writeInt(maxValue);
        data.writeFloat(multiplier);
        data.writeInt(defaultValue);
    }
}
