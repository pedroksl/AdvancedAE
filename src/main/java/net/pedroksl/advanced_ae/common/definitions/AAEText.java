package net.pedroksl.advanced_ae.common.definitions;

import net.pedroksl.advanced_ae.AdvancedAE;

import appeng.core.localization.LocalizationEnum;

public enum AAEText implements LocalizationEnum {
    AcceleratorThreads("Provides 4 co-processing threads per block.", Type.TOOLTIP),
    ModName("Advanced AE", Type.GUI),
    AdvPatternProvider("ME Adv. Pattern Provider", Type.GUI),
    AdvPatternEncoder("Advanced Pattern Provider", Type.GUI),
    AnyButton("Target the face adjacent to the adv. pattern provider", Type.TOOLTIP),
    NorthButton("Target the NORTH face of the machine.", Type.TOOLTIP),
    EastButton("Target the EAST face of the machine.", Type.TOOLTIP),
    SouthButton("Target the SOUTH face of the machine.", Type.TOOLTIP),
    WestButton("Target the WEST face of the machine.", Type.TOOLTIP),
    UpButton("Target the UP face of the machine.", Type.TOOLTIP),
    DownButton("Target the DOWN face of the machine.", Type.TOOLTIP);

    private final String englishText;
    private final Type type;

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
        TOOLTIP("gui.tooltips");

        private final String root;

        Type(String root) {
            this.root = root;
        }
    }
}
