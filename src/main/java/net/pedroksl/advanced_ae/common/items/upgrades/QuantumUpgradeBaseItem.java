package net.pedroksl.advanced_ae.common.items.upgrades;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;

public class QuantumUpgradeBaseItem extends Item {
    private final UpgradeType type;

    public QuantumUpgradeBaseItem(Properties properties) {
        super(properties);
        this.type = UpgradeType.EMPTY;
    }

    public QuantumUpgradeBaseItem(UpgradeType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public UpgradeType getType() {
        return type;
    }

    public UpgradeType.SettingType getSettingType() {
        return type.getSettingType();
    }

    public int getCost() {
        return type.getCost();
    }

    public UpgradeType.ExtraSettings getExtraSettings() {
        return type.getExtraSettings();
    }
}
