package net.pedroksl.advanced_ae.common.definitions;

import net.pedroksl.advanced_ae.AdvancedAE;

import appeng.core.localization.LocalizationEnum;

public enum AAEText implements LocalizationEnum {
    ModName("Advanced AE", Type.GUI),
    AcceleratorThreads("Provides 8 co-processing threads per block.", Type.TOOLTIP),
    CoreTooltip("Provides 256M crafting storage and 8 co-processing threads.", Type.TOOLTIP),
    AdvPatternProvider("ME Adv. Pattern Provider", Type.GUI),
    AdvPatternEncoder("Advanced Pattern Provider", Type.GUI),
    ReactionChamber("Reaction Chamber", Type.GUI),
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

    ReactionChamberEnergy("Used Power: %d" + "k FE", Type.EMI_TEXT),

    PatternProviderUpgrade(
            "Upgrades a normal or extended pattern provider to the advanced version with the same "
                    + "amount of pattern slots",
            Type.TOOLTIP),
    PatternProviderCapacityUpgrade(
            "Upgrades an Advanced Pattern Provider to the maximum amount of pattern slots", Type.TOOLTIP);

    private final String englishText;
    private final Type type;

    public static final int TOOLTIP_DEFAULT_COLOR = 0x5E5E5E;

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
