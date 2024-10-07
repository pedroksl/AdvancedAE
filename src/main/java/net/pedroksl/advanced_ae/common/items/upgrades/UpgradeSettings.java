package net.pedroksl.advanced_ae.common.items.upgrades;

public class UpgradeSettings {

	public float minValue;
	public float maxValue;
	public float defaultValue;

	public UpgradeSettings(float min, float max) {
		this.minValue = min;
		this.maxValue = max;
		this.defaultValue = max;
	}

	public UpgradeSettings(float min, float max, float defaultValue) {
		this.minValue = min;
		this.maxValue = max;
		this.defaultValue = defaultValue;
	}
}
