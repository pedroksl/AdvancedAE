package net.pedroksl.advanced_ae.xmod;

import net.pedroksl.ae2addonlib.util.AddonEnum;

public enum Addons implements AddonEnum {
    EXPATTERNPROVIDER("Extended AE"),
    APPMEK("Applied Mekanistics"),
    AE2WTLIB("AE2 Wireless Terminals Lib"),
    APPFLUX("Applied Flux"),
    MEGACELLS("MEGACells"),
    MEKANISM("Mekanism"),
    IRIS("Iris"),
    CURIOS("Curios"),
    APOTHIC_ENCHANTING("Apothic Enchanting");

    private final String modName;

    Addons(String modName) {
        this.modName = modName;
    }

    public String getModId() {
        return name().toLowerCase();
    }

    public String getModName() {
        return this.modName;
    }
}
