package net.pedroksl.advanced_ae.common.definitions;

import net.pedroksl.advanced_ae.AdvancedAE;

import appeng.core.localization.LocalizationEnum;

public enum AAEText implements LocalizationEnum {
    ModName("Advanced AE", Type.GUI),
    QuantumStructureTooltip(
            "Used in the outside layer of the Quantum Computer Multiblock. Maximum multiblock size is %1$dx%1$dx%1$d.",
            Type.TOOLTIP),
    AcceleratorThreads("Provides %d co-processing threads per block.", Type.TOOLTIP),
    MultiThreaderMultiplication(
            "Multiplies the amount of co-processors in the Quantum Computer Multiblock by %d. Limited to %d per multiblock.",
            Type.TOOLTIP),
    DataEntanglerMultiplication(
            "Multiplies the total storage in the Quantum Computer Multiblock by %d. Limited to %d per multiblock.",
            Type.TOOLTIP),
    CoreTooltip("Provides 256M crafting storage and %d co-processing threads.", Type.TOOLTIP),
    AdvPatternProvider("ME Adv. Pattern Provider", Type.GUI),
    AdvPatternProviderEmiDesc(
            "Created by upgrading an Advanced Pattern Provider with a capacity upgrade", Type.EMI_TEXT),
    AdvPatternEncoder("Advanced Pattern Encoder", Type.GUI),
    ReactionChamber("Reaction Chamber", Type.GUI),
    QuantumCrafter("Quantum Crafter", Type.GUI),
    StockExportBus("ME Stock Export Bus", Type.GUI),
    ImportExportBus("ME Import Export Bus", Type.GUI),
    EmiReactionChamber("Reaction Chamber", Type.EMI_CATEGORY),
    AnyButton("Target the face adjacent to the adv. pattern provider", Type.TOOLTIP),
    NorthButton("Target the NORTH face of the machine.", Type.TOOLTIP),
    EastButton("Target the EAST face of the machine.", Type.TOOLTIP),
    SouthButton("Target the SOUTH face of the machine.", Type.TOOLTIP),
    WestButton("Target the WEST face of the machine.", Type.TOOLTIP),
    UpButton("Target the UP face of the machine.", Type.TOOLTIP),
    DownButton("Target the DOWN face of the machine.", Type.TOOLTIP),
    ClearButton("Clear", Type.TOOLTIP),
    ClearFluidButtonHint("Flush the remaining fluid from the machine.", Type.TOOLTIP),
    ClearSidesButtonHint("Disable output from all sides of the machine.", Type.TOOLTIP),
    TankEmpty("Empty", Type.TOOLTIP),
    TankAmount("%s / %s B", Type.TOOLTIP),
    QuantumArmorTooltip("Upgrades:", Type.TOOLTIP),
    UpgradeTooltip("Upgrade available for:", Type.TOOLTIP),
    UpgradeNotInstalled(" (Not Installed)", Type.TOOLTIP),
    UpgradeNotInstalledMessage("%s is not installed.", Type.GUI),
    UpgradeNotEnabledMessage("%s is not enabled.", Type.GUI),
    ItemNotCraftable("No patterns available for Pick-Crafting the current target.", Type.GUI),
    NoAvailableTarget("No suitable target available for Pick-Craft.", Type.GUI),
    QuantumArmorConfig("Quantum Armor Config", Type.GUI),
    QuantumArmorSetting("Setting Config", Type.GUI),
    MagnetRangeSlider("Pickup Range", Type.GUI),

    ReactionChamberEnergy("Used Power: %d" + "k FE", Type.EMI_TEXT),
    ShatteredSingularityDescription(
            "This item is produced by chemical reaction in the reaction chamber and has "
                    + "several applications in quantum computing.",
            Type.EMI_TEXT),

    PatternProviderUpgrade(
            "Upgrades a normal or extended pattern provider to the advanced version with the same "
                    + "amount of pattern slots",
            Type.TOOLTIP),
    PatternProviderCapacityUpgrade(
            "Upgrades an Advanced Pattern Provider to the maximum amount of pattern slots", Type.TOOLTIP),

    PatternInventory("Pattern Input", Type.GUI),
    OutputInventory("Output", Type.GUI),

    MeExport("ME Export", Type.TOOLTIP),
    MeExportOn("Produced items will be exported to the ME System.", Type.TOOLTIP),
    MeExportOff("Produces items will be exported to allowed nearby containers", Type.TOOLTIP),
    FilteredImport("Filtered Import", Type.TOOLTIP),
    FilteredImportOn("Input must match crafting outputs while crafting jobs are active", Type.TOOLTIP),
    FilteredImportOff("Input is unrestricted", Type.TOOLTIP),
    DirectionalOutput("Directional Output", Type.TOOLTIP),
    DirectionOutputHint("Configure which directions are allowed for output auto-export", Type.TOOLTIP),

    NumberTextFieldInputHint("Confirm the input using the %s key.", Type.TOOLTIP),
    NumberTextFieldOutputHint(
            "Confirm the input using the %s key.\nSetting the value to 0 removes the limit.", Type.TOOLTIP),
    QuantumCrafterPatternConfig("Pattern Configuration", Type.GUI),
    ConfigurePatternButton("Configure Pattern", Type.TOOLTIP),
    EnablePatternButton("Enabled/Disable", Type.TOOLTIP),

    SetAmountButtonHint("Middle click to set the amount to keep in stock", Type.GUI),
    SetAmount("Set Stock Amount", Type.GUI),

    ThroughputMonitorValue("%s" + "%s" + "/s", Type.GUI),
    OverdriveThroughputMonitorValue("%s" + "%s" + "/t", Type.GUI),
    SlowThroughputMonitorValue("%s" + "%s" + "/m", Type.GUI),

    WalkSpeedUpgrade("Walk Speed", Type.UPGRADE),
    SprintSpeedUpgrade("Sprint Speed", Type.UPGRADE),
    StepAssistUpgrade("Step Assist", Type.UPGRADE),
    JumpHeightUpgrade("Jump Height", Type.UPGRADE),
    LavaImmunityUpgrade("Lava Immunity", Type.UPGRADE),
    FlightUpgrade("Flight", Type.UPGRADE),
    WaterBreathingUpgrade("Water Breathing", Type.UPGRADE),
    AutoFeedUpgrade("Auto Feed", Type.UPGRADE),
    AutoStockUpgrade("Auto Stock", Type.UPGRADE),
    MagnetUpgrade("Magnet", Type.UPGRADE),
    HpBufferUpgrade("HP Buffer", Type.UPGRADE),
    EvasionUpgrade("Evasion", Type.UPGRADE),
    RegenerationUpgrade("Regeneration", Type.UPGRADE),
    StrengthUpgrade("Strength", Type.UPGRADE),
    AttackSpeedUpgrade("Attack Speed", Type.UPGRADE),
    LuckUpgrade("Luck Boost", Type.UPGRADE),
    ReachUpgrade("Reach Boost", Type.UPGRADE),
    SwimSpeedUpgrade("Swim Speed", Type.UPGRADE),
    NightVisionUpgrade("Night Vision", Type.UPGRADE),
    FlightDriftUpgrade("No Flight Drift", Type.UPGRADE),
    RechargingUpgrade("ME Recharging", Type.UPGRADE),
    WorkbenchUpgrade("Portable Workbench", Type.UPGRADE),
    PickCraftUpgrade("Pick-Craft", Type.UPGRADE),

    QuantumUpgradeTooltip("Quantum Armor Upgrade", Type.TOOLTIP),
    UpgradeBaseTooltip("Base upgrade card required to craft the others.", Type.TOOLTIP),
    WalkSpeedTooltip("Boosts the walking speed.", Type.TOOLTIP),
    SprintSpeedTooltip("Boosts the sprinting speed.", Type.TOOLTIP),
    StepAssistTooltip("Enables step assist. Maximum step of %s.", Type.TOOLTIP),
    JumpHeightTooltip("Boosts the jump height by %s.", Type.TOOLTIP),
    LavaImmunityTooltip("Makes the user immune to lava and fire.", Type.TOOLTIP),
    FlightTooltip("Enables Creative Flight.", Type.TOOLTIP),
    WaterBreathingTooltip("Enables breathing under water.", Type.TOOLTIP),
    AutoFeedTooltip("Configurable to auto-feed the user with food from the ME system.", Type.TOOLTIP),
    AutoStockTooltip("Configurable to regulate an exact amount of items to be in the user's inventory.", Type.TOOLTIP),
    MagnetTooltip("Configurable to pull items from the world to user inventory. Maximum range of %s.", Type.TOOLTIP),
    HpBufferTooltip("Boosts the user's max health by +%s.", Type.TOOLTIP),
    EvasionTooltip("Enables damage avoidance of any type with a %s" + "%% chance.", Type.TOOLTIP),
    RegenerationTooltip("Enables a regeneration effect.", Type.TOOLTIP),
    StrengthTooltip("Boosts the user's strength by %s.", Type.TOOLTIP),
    AttackSpeedTooltip("Boosts the user's attack speed by %s.", Type.TOOLTIP),
    LuckTooltip("Boosts the user's luck by %s.", Type.TOOLTIP),
    ReachTooltip("Boosts the user's reach by %s.", Type.TOOLTIP),
    SwimSpeedTooltip("Boosts the user's swimming speed.", Type.TOOLTIP),
    NightVisionTooltip("Enables Night Vision.", Type.TOOLTIP),
    FlightDriftTooltip("Removes flight drift. Requires basic flight upgrade.", Type.TOOLTIP),
    RechargingTooltip(
            "Enables armor recharging if linked to the grid. When installed in the chest slot, also recharges the inventory slots.",
            Type.TOOLTIP),
    PortableWorkbenchTooltip("Enables the use of a portable cell workbench.", Type.TOOLTIP),
    PickCraftTooltip(
            "Enables the ability to request craft of the targeted block if a pattern is available in the ME system.",
            Type.TOOLTIP),
    HudTooltip("Adds a HUD with configurable safety checks and information about the ME System.", Type.TOOLTIP),

    InsufficientPower("Insufficient Power", Type.TOOLTIP),
    InsufficientPowerDetails(
            "Unable to extract enough power for full speed progress. Check guide for possible " + "solutions.",
            Type.TOOLTIP),

    QuantumArmorHotkeyTooltip("Press [%s] with this equipped to configure.", Type.TOOLTIP),
    QuantumArmorStableFootingTooltip("Negates mining speed penalty when flying.", Type.TOOLTIP),
    QuantumArmorFallDamageTooltip("Negates fall damage when worn.", Type.TOOLTIP);

    private final String englishText;
    private final Type type;

    public static final int TOOLTIP_DEFAULT_COLOR = 0x7E7E7E;

    AAEText(String englishText, Type type) {
        this.englishText = englishText;
        this.type = type;
    }

    @Override
    public String getEnglishText() {
        return englishText;
    }

    @Override
    public String getTranslationKey() {
        return String.format("%s.%s.%s", type.root, AdvancedAE.MOD_ID, name());
    }

    private enum Type {
        GUI("gui"),
        TOOLTIP("gui.tooltips"),
        UPGRADE("gui.upgrade"),
        EMI_CATEGORY("emi.category"),
        EMI_TEXT("emi.text");

        private final String root;

        Type(String root) {
            this.root = root;
        }
    }
}
