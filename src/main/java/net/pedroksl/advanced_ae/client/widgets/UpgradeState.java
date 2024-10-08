package net.pedroksl.advanced_ae.client.widgets;

import java.util.List;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeSettings;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

public class UpgradeState {

    private final UpgradeType type;
    private final UpgradeSettings settings;
    private boolean enabled;
    private int currentValue;
    private final List<TagKey<Item>> filter;

    public UpgradeState(UpgradeType type, UpgradeSettings settings, boolean enabled, int currentValue) {
        this(type, settings, enabled, currentValue, List.of());
    }

    public UpgradeState(
            UpgradeType type, UpgradeSettings settings, boolean enabled, int currentValue, List<TagKey<Item>> filter) {
        this.type = type;
        this.settings = settings;
        this.enabled = enabled;
        this.currentValue = currentValue;
        this.filter = filter;
    }

    public UpgradeType getType() {
        return type;
    }

    public UpgradeSettings getSettings() {
        return settings;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public List<TagKey<Item>> getFilter() {
        return filter;
    }

    public void addTag(TagKey<Item> tag) {
        filter.add(tag);
    }

    public void removeTag(TagKey<Item> tag) {
        filter.remove(tag);
    }
}
