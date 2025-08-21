package net.pedroksl.advanced_ae.common.items.upgrades;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.definitions.AAEConfig;
import net.pedroksl.advanced_ae.common.definitions.AAEItemDefinition;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEText;

public enum UpgradeType {
    EMPTY("Empty", null, SettingType.NONE, 0, ApplicationType.PASSIVE, AAEItems.QUANTUM_UPGRADE_BASE),

    WALK_SPEED(
            "Walk Speed",
            UpgradeCards::walkSpeed,
            SettingType.NUM_INPUT,
            10,
            ApplicationType.PASSIVE,
            AAEItems.WALK_SPEED_CARD),
    SPRINT_SPEED(
            "Sprint Speed",
            UpgradeCards::sprintSpeed,
            SettingType.NUM_INPUT,
            10,
            ApplicationType.PASSIVE,
            AAEItems.SPRINT_SPEED_CARD),
    STEP_ASSIST("Step Assist", null, SettingType.NUM_INPUT, 5, ApplicationType.PASSIVE_USE, AAEItems.STEP_ASSIST_CARD),
    JUMP_HEIGHT(
            "Jump Height",
            UpgradeCards::jumpHeight,
            SettingType.NUM_INPUT,
            10,
            ApplicationType.PASSIVE_USE,
            AAEItems.JUMP_HEIGHT_CARD),
    LAVA_IMMUNITY(
            "Lava Immunity", null, SettingType.NONE, 10, ApplicationType.PASSIVE_USE, AAEItems.LAVA_IMMUNITY_CARD),
    FLIGHT(
            "Flight",
            UpgradeCards::creativeFlight,
            SettingType.NUM_INPUT,
            10,
            ApplicationType.PASSIVE,
            AAEItems.FLIGHT_CARD),
    WATER_BREATHING(
            "Water Breathing", null, SettingType.NONE, 10, ApplicationType.PASSIVE_USE, AAEItems.WATER_BREATHING_CARD),
    AUTO_FEED(
            "Auto Feed",
            UpgradeCards::autoFeed,
            SettingType.FILTER,
            5,
            ApplicationType.PASSIVE,
            AAEItems.AUTO_FEED_CARD),
    AUTO_STOCK(
            "Auto Stock",
            UpgradeCards::autoStock,
            SettingType.FILTER,
            5,
            ApplicationType.PASSIVE,
            AAEItems.AUTO_STOCK_CARD),
    MAGNET(
            "Magnet",
            UpgradeCards::magnet,
            SettingType.NUM_AND_FILTER,
            5,
            ApplicationType.PASSIVE,
            AAEItems.MAGNET_CARD,
            ExtraSettings.BLACKLIST),
    HP_BUFFER("HP Buffer", null, SettingType.NONE, 10, ApplicationType.BUFF, AAEItems.HP_BUFFER_CARD),
    EVASION("Evasion", null, SettingType.NONE, 10, ApplicationType.BUFF, AAEItems.EVASION_CARD),
    REGENERATION(
            "Regeneration",
            UpgradeCards::regeneration,
            SettingType.NONE,
            10,
            ApplicationType.PASSIVE,
            AAEItems.REGENERATION_CARD),
    STRENGTH("Strength", null, SettingType.NONE, 10, ApplicationType.BUFF, AAEItems.STRENGTH_CARD),
    ATTACK_SPEED("Attack Speed", null, SettingType.NONE, 10, ApplicationType.BUFF, AAEItems.ATTACK_SPEED_CARD),

    LUCK("Luck Boost", null, SettingType.NONE, 10, ApplicationType.BUFF, AAEItems.LUCK_CARD),
    REACH("Reach Boost", null, SettingType.NUM_INPUT, 10, ApplicationType.BUFF, AAEItems.REACH_CARD),
    SWIM_SPEED(
            "Swim Speed",
            UpgradeCards::swimSpeed,
            SettingType.NUM_INPUT,
            5,
            ApplicationType.PASSIVE,
            AAEItems.SWIM_SPEED_CARD),
    NIGHT_VISION("Night Vision", null, SettingType.NONE, 10, ApplicationType.BUFF, AAEItems.NIGHT_VISION_CARD),
    FLIGHT_DRIFT("No Flight Drift", null, SettingType.NUM_INPUT, 10, ApplicationType.BUFF, AAEItems.FLIGHT_DRIFT_CARD),
    CHARGING(
            "ME Recharging",
            UpgradeCards::recharging,
            SettingType.NONE,
            0,
            ApplicationType.PASSIVE,
            AAEItems.RECHARGING_CARD),
    WORKBENCH("Portable Workbench", null, SettingType.NONE, 0, ApplicationType.PASSIVE_USE, AAEItems.WORKBENCH_CARD),
    PICK_CRAFT("Pick-Craft", null, SettingType.NONE, 1000, ApplicationType.PASSIVE_USE, AAEItems.PICK_CRAFT_CARD);
    //    HUD("HUD", null, SettingType.BOOL_LIST, 10, ApplicationType.PASSIVE, AAEItems.HUD_CARD);

    public enum SettingType {
        NONE,
        NUM_INPUT,
        FILTER,
        NUM_AND_FILTER,
        BOOL_LIST
    }

    public enum ApplicationType {
        PASSIVE,
        PASSIVE_USE,
        BUFF
    }

    public enum ExtraSettings {
        NONE,
        BLACKLIST
    }

    public final String name;
    public final Ability ability;
    private final SettingType settingType;
    private final int cost;
    public final ApplicationType applicationType;
    private final AAEItemDefinition<? extends QuantumUpgradeBaseItem> item;
    private final ExtraSettings extra;

    UpgradeType(
            String name,
            @Nullable Ability ability,
            SettingType settingType,
            int cost,
            ApplicationType applicationType,
            AAEItemDefinition<? extends QuantumUpgradeBaseItem> item) {
        this(name, ability, settingType, cost, applicationType, item, ExtraSettings.NONE);
    }

    UpgradeType(
            String name,
            @Nullable Ability ability,
            SettingType settingType,
            int cost,
            ApplicationType applicationType,
            AAEItemDefinition<? extends QuantumUpgradeBaseItem> item,
            ExtraSettings extra) {
        this.name = name;
        this.ability = ability;
        this.settingType = settingType;
        this.cost = cost;
        this.applicationType = applicationType;
        this.item = item;
        this.extra = extra;
    }

    public AAEItemDefinition<? extends QuantumUpgradeBaseItem> item() {
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

    public String getTranslationKey() {
        return "gui.upgrades.advanced_ae." + this.name.replaceAll("\\s+", "") + "Upgrade";
    }

    public Component getTranslatedName() {
        return Component.translatable(getTranslationKey());
    }

    public UpgradeSettings getSettings() {
        return switch (this) {
            case EMPTY,
                    LAVA_IMMUNITY,
                    WATER_BREATHING,
                    AUTO_FEED,
                    AUTO_STOCK,
                    REGENERATION,
                    NIGHT_VISION,
                    CHARGING,
                    WORKBENCH,
                    PICK_CRAFT /*,
                               HUD*/ -> new UpgradeSettings(1);
            case WALK_SPEED -> new UpgradeSettings(1, AAEConfig.instance().getMaxWalkSpeed(), 0.1f);
            case SPRINT_SPEED -> new UpgradeSettings(1, AAEConfig.instance().getMaxSprintSpeed(), 0.1f);
            case STEP_ASSIST -> new UpgradeSettings(1, AAEConfig.instance().getMaxStepHeight());
            case JUMP_HEIGHT -> new UpgradeSettings(1, AAEConfig.instance().getMaxJumpHeight());
            case MAGNET -> new UpgradeSettings(3, AAEConfig.instance().getMaxMagnetRange());
            case HP_BUFFER -> new UpgradeSettings(AAEConfig.instance().getmaxHpBuffer());
            case FLIGHT -> new UpgradeSettings(1, AAEConfig.instance().getMaxFlightSpeed());
            case EVASION -> new UpgradeSettings(AAEConfig.instance().getEvasionChance());
            case STRENGTH -> new UpgradeSettings(AAEConfig.instance().getStrengthBoost());
            case ATTACK_SPEED -> new UpgradeSettings(AAEConfig.instance().getAttackSpeedBoost());
            case LUCK -> new UpgradeSettings(AAEConfig.instance().getLuckBoost());
            case REACH -> new UpgradeSettings(1, AAEConfig.instance().getMaxReachBoost());
            case SWIM_SPEED -> new UpgradeSettings(1, AAEConfig.instance().getMaxSwimSpeedBoost(), 0.1f);
            case FLIGHT_DRIFT -> new UpgradeSettings(0, 100, 1, 50);
        };
    }

    public MutableComponent getTooltip() {
        return switch (this) {
            case EMPTY -> AAEText.UpgradeBaseTooltip.text();
            case WALK_SPEED -> AAEText.WalkSpeedTooltip.text();
            case SPRINT_SPEED -> AAEText.SprintSpeedTooltip.text();
            case STEP_ASSIST -> AAEText.StepAssistTooltip.text(
                    AAEConfig.instance().getMaxStepHeight());
            case JUMP_HEIGHT -> AAEText.JumpHeightTooltip.text(
                    AAEConfig.instance().getMaxJumpHeight());
            case LAVA_IMMUNITY -> AAEText.LavaImmunityTooltip.text();
            case FLIGHT -> AAEText.FlightTooltip.text();
            case WATER_BREATHING -> AAEText.WaterBreathingTooltip.text();
            case AUTO_FEED -> AAEText.AutoFeedTooltip.text();
            case AUTO_STOCK -> AAEText.AutoStockTooltip.text();
            case MAGNET -> AAEText.MagnetTooltip.text(AAEConfig.instance().getMaxMagnetRange());
            case HP_BUFFER -> AAEText.HpBufferTooltip.text(AAEConfig.instance().getmaxHpBuffer());
            case EVASION -> AAEText.EvasionTooltip.text(AAEConfig.instance().getEvasionChance());
            case REGENERATION -> AAEText.RegenerationTooltip.text();
            case STRENGTH -> AAEText.StrengthTooltip.text(AAEConfig.instance().getStrengthBoost());
            case ATTACK_SPEED -> AAEText.AttackSpeedTooltip.text(
                    AAEConfig.instance().getAttackSpeedBoost());
            case LUCK -> AAEText.LuckTooltip.text(AAEConfig.instance().getLuckBoost());
            case REACH -> AAEText.ReachTooltip.text(AAEConfig.instance().getMaxReachBoost());
            case SWIM_SPEED -> AAEText.SwimSpeedTooltip.text();
            case NIGHT_VISION -> AAEText.NightVisionTooltip.text();
            case FLIGHT_DRIFT -> AAEText.FlightDriftTooltip.text();
            case CHARGING -> AAEText.RechargingTooltip.text();
            case WORKBENCH -> AAEText.PortableWorkbenchTooltip.text();
            case PICK_CRAFT -> AAEText.PickCraftTooltip.text();
                //            case HUD -> AAEText.PortableWorkbenchTooltip.text();
        };
    }

    public ExtraSettings getExtraSettings() {
        return this.extra;
    }

    @FunctionalInterface
    public interface Ability {
        boolean execute(Level level, Player player, ItemStack stack);
    }
}
