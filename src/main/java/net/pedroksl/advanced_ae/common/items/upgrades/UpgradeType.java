package net.pedroksl.advanced_ae.common.items.upgrades;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;

import appeng.core.definitions.ItemDefinition;

public enum UpgradeType {
    EMPTY(null, SettingType.NONE, 0, ExtraSettings.NONE, AAEItems.QUANTUM_UPGRADE_BASE, null),

    WALK_SPEED(
            UpgradeCards::walkSpeed,
            SettingType.NUM_INPUT,
            10,
            ExtraSettings.NONE,
            AAEItems.WALK_SPEED_CARD,
            new UpgradeSettings(1, 80, 0.1f)),
    SPRINT_SPEED(
            UpgradeCards::sprintSpeed,
            SettingType.NUM_INPUT,
            10,
            ExtraSettings.NONE,
            AAEItems.SPRINT_SPEED_CARD,
            new UpgradeSettings(1, 80, 0.1f)),
    STEP_ASSIST(
            UpgradeCards::stepAssist,
            SettingType.NUM_INPUT,
            1,
            ExtraSettings.NONE,
            AAEItems.STEP_ASSIST_CARD,
            new UpgradeSettings(1, 3)),
    JUMP_HEIGHT(
            UpgradeCards::jumpHeight,
            SettingType.NUM_INPUT,
            10,
            ExtraSettings.NONE,
            AAEItems.JUMP_HEIGHT_CARD,
            new UpgradeSettings(1, 3));

    public enum SettingType {
        NONE,
        NUM_INPUT,
        FILTER
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
    private UpgradeSettings settings = null;

    UpgradeType(
            Ability ability,
            SettingType settingType,
            int cost,
            ExtraSettings extraSettings,
            ItemDefinition<? extends QuantumUpgradeBaseItem> item) {
        this.ability = ability;
        this.settingType = settingType;
        this.cost = cost;
        this.extraSettings = extraSettings;
        this.item = item;
    }

    UpgradeType(
            Ability ability,
            SettingType settingType,
            int cost,
            ExtraSettings extraSettings,
            ItemDefinition<? extends QuantumUpgradeBaseItem> item,
            UpgradeSettings settings) {
        this(ability, settingType, cost, extraSettings, item);
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

    public UpgradeSettings getSettings() {
        return settings;
    }

    @FunctionalInterface
    public interface Ability {
        boolean execute(Level level, Player player, ItemStack stack);
    }
}
