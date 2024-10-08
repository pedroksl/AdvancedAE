package net.pedroksl.advanced_ae.common.items.upgrades;

public class UpgradeSettings {

    public int minValue;
    public int maxValue;
    public float multiplier = 1;
    public int defaultValue;

    public UpgradeSettings(int min, int max) {
        this.minValue = min;
        this.maxValue = max;
        this.defaultValue = max;
    }

    public UpgradeSettings(int min, int max, float multiplier) {
        this.minValue = min;
        this.maxValue = max;
        this.multiplier = multiplier;
        this.defaultValue = max;
    }

    public UpgradeSettings(int min, int max, float multiplier, int defaultValue) {
        this.minValue = min;
        this.maxValue = max;
        this.multiplier = multiplier;
        this.defaultValue = defaultValue;
    }
}
