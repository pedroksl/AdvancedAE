package net.pedroksl.advanced_ae.common.definitions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

public final class AAENbt {
    public static final String STACK_TAG = "generic_nbt";

    public static final String PORTABLE_CELL_STACK_TAG = "portable_cell_stack";

    public static final String NIGHT_VISION_ACTIVATED = "night_vision_on";

    public static final Map<UpgradeType, String> UPGRADE_TAG = new HashMap<>();

    public static final String UPGRADE_TOGGLE = "enabled";
    public static final String UPGRADE_VALUE = "value";
    public static final String UPGRADE_FILTER = "filter";
    public static final String UPGRADE_EXTRA = "extra";

    public static void init() {
        for (var upgrade : UpgradeType.values()) {
            UPGRADE_TAG.put(upgrade, upgrade.name().toLowerCase(Locale.ROOT) + "_tag");
        }
    }
}
