package net.pedroksl.advanced_ae.common.items.upgrades;

public enum UpgradeType {
	EMPTY(SettingType.NONE, 0, ExtraSettings.NONE),
	WALK_SPEED(SettingType.NUMINPUT, 10, ExtraSettings.NONE),
	RUN_SPEED(SettingType.NUMINPUT, 10, ExtraSettings.NONE),
	STEP_ASSIST(SettingType.TOGGLE, 1, ExtraSettings.NONE),
	JUMP_HEIGHT(SettingType.NUMINPUT, 10, ExtraSettings.NONE);





	public enum SettingType {
		NONE,
		TOGGLE,
		NUMINPUT
	}

	public enum ExtraSettings {
		NONE,
		TRUE
	}

	UpgradeType(SettingType settingType, int cost, ExtraSettings extraSettings) {

	}
}
