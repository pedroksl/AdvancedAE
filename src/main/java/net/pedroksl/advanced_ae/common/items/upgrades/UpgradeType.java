package net.pedroksl.advanced_ae.common.items.upgrades;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;

import appeng.core.definitions.ItemDefinition;

public enum UpgradeType {
    EMPTY("Empty", null, SettingType.NONE, 0, ApplicationType.PASSIVE, AAEItems.QUANTUM_UPGRADE_BASE, null),

    WALK_SPEED(
            "Walk Speed",
            UpgradeCards::walkSpeed,
            SettingType.NUM_INPUT,
            10,
            ApplicationType.PASSIVE,
            AAEItems.WALK_SPEED_CARD,
            new UpgradeSettings(1, 80, 0.1f)),
    SPRINT_SPEED(
            "Sprint Speed",
            UpgradeCards::sprintSpeed,
            SettingType.NUM_INPUT,
            10,
            ApplicationType.PASSIVE,
            AAEItems.SPRINT_SPEED_CARD,
            new UpgradeSettings(1, 80, 0.1f)),
    STEP_ASSIST(
            "Step Assist",
            null,
            SettingType.NUM_INPUT,
            1,
            ApplicationType.PASSIVE_USE,
            AAEItems.STEP_ASSIST_CARD,
            new UpgradeSettings(1, 3)),
    JUMP_HEIGHT(
            "Jump Height",
            UpgradeCards::jumpHeight,
            SettingType.NUM_INPUT,
            10,
            ApplicationType.PASSIVE_USE,
            AAEItems.JUMP_HEIGHT_CARD,
            new UpgradeSettings(1, 3)),
    FLIGHT("Flight", null, SettingType.NONE, 10, ApplicationType.PASSIVE_USE, AAEItems.FLIGHT_CARD);

    public enum SettingType {
        NONE,
        NUM_INPUT,
        FILTER
    }

    public enum ApplicationType {
        PASSIVE,
        PASSIVE_USE,
        BUFF
    }

    public final String name;
    public final Ability ability;
    private final SettingType settingType;
    private final int cost;
    public final ApplicationType applicationType;
    private final ItemDefinition<? extends QuantumUpgradeBaseItem> item;
    private UpgradeSettings settings = null;

    UpgradeType(
            String name,
            @Nullable Ability ability,
            SettingType settingType,
            int cost,
            ApplicationType applicationType,
            ItemDefinition<? extends QuantumUpgradeBaseItem> item) {
        this.name = name;
        this.ability = ability;
        this.settingType = settingType;
        this.cost = cost;
        this.applicationType = applicationType;
        this.item = item;
    }

    UpgradeType(
            String name,
            @Nullable Ability ability,
            SettingType settingType,
            int cost,
            ApplicationType applicationType,
            ItemDefinition<? extends QuantumUpgradeBaseItem> item,
            UpgradeSettings settings) {
        this(name, ability, settingType, cost, applicationType, item);
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

    public ApplicationType getApplicationType() {
        return this.applicationType;
    }

    public UpgradeSettings getSettings() {
        return settings;
    }

    @FunctionalInterface
    public interface Ability {
        boolean execute(Level level, Player player, ItemStack stack);
    }
}
