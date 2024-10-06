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
    StockExportBus("Stock Export Bus", Type.GUI),
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
    TankAmount("%s mb / %s mb", Type.TOOLTIP),
    QuantumArmorConfig("Quantum Armor Configuration", Type.GUI),

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

    ThroughputMonitorValue("%s" + "%s" + "/s", Type.GUI);

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
        EMI_CATEGORY("emi.category"),
        EMI_TEXT("emi.text");

        private final String root;

        Type(String root) {
            this.root = root;
        }
    }
}
