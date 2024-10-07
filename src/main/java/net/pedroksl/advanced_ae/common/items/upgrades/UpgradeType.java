package net.pedroksl.advanced_ae.common.items.upgrades;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;

import appeng.core.definitions.ItemDefinition;

public enum UpgradeType {
    EMPTY(null, SettingType.NONE, 0, ExtraSettings.NONE, AAEItems.QUANTUM_UPGRADE_BASE, null),

    WALK_SPEED(UpgradeCards::walkSpeed, SettingType.NUMINPUT, 10, ExtraSettings.NONE, AAEItems.WALK_SPEED_CARD,
            AAEComponents.WALK_SPEED_UPGRADE, new UpgradeSettings(0.1f, 8f)),
    SPRINT_SPEED(UpgradeCards::sprintSpeed, SettingType.NUMINPUT, 10, ExtraSettings.NONE, AAEItems.SPRINT_SPEED_CARD, AAEComponents.SPRINT_SPEED_UPGRADE),
    STEP_ASSIST(UpgradeCards::stepAssist, SettingType.TOGGLE, 1, ExtraSettings.NONE, AAEItems.STEP_ASSIST_CARD, AAEComponents.STEP_ASSIST_UPGRADE),
    JUMP_HEIGHT(UpgradeCards::jumpHeight, SettingType.NUMINPUT, 10, ExtraSettings.NONE, AAEItems.JUMP_HEIGHT_CARD, AAEComponents.JUMP_HEIGHT_UPGRADE);

    public enum SettingType {
        NONE,
        TOGGLE,
        NUMINPUT
    }

    public enum ExtraSettings {
        NONE,
        TRUE
    }

    public final Ability ability;
    private final SettingType settingType;
    private final int cost;
    private final ExtraSettings extraSettings;
    private final ItemDefinition<? extends QuantumUpgradeBaseItem> item;
    private final DataComponentType<Boolean> component;

    private boolean toggled = true;
    private UpgradeSettings settings = null;
    private

    UpgradeType(
            Ability ability,
            SettingType settingType,
            int cost,
            ExtraSettings extraSettings,
            ItemDefinition<? extends QuantumUpgradeBaseItem> item,
            DataComponentType<Boolean> component) {
        this.ability = ability;
        this.settingType = settingType;
        this.cost = cost;
        this.extraSettings = extraSettings;
        this.item = item;
        this.component = component;
    }

    UpgradeType(
            Ability ability,
            SettingType settingType,
            int cost,
            ExtraSettings extraSettings,
            ItemDefinition<? extends QuantumUpgradeBaseItem> item,
            DataComponentType<Boolean> component,
            UpgradeSettings settings) {
        this(ability, settingType, cost, extraSettings, item, component);
        this.settings = settings;
    }

    public ItemDefinition<? extends QuantumUpgradeBaseItem> item() {
        return this.item;
    }

    public SettingType getSettingType() {
        return this.settingType;
    }

    public int getCost() {
        return this.cost;
    }

    public ExtraSettings getExtraSettings() {
        return this.extraSettings;
    }

    public DataComponentType<Boolean> getComponent() {
        return this.component;
    }

    @FunctionalInterface
    public interface Ability {
        boolean execute(Level level, Player player, ItemStack stack);
    }
}
