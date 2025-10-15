package net.pedroksl.advanced_ae.xmod;

import net.pedroksl.ae2addonlib.util.AddonEnum;

public enum Addons implements AddonEnum {
    EXTENDEDAE("Extended AE"),
    APPMEK("Applied Mekanistics"),
    APPFLUX("Applied Flux"),
    MEGACELLS("MEGACells"),
    MEKANISM("Mekanism"),
    IRIS("Iris"),
    CURIOS("Curios"),
    INVTWEAKS("Inventory Tweaks"),
    DARKMODEEVERYWHERE("Dark Mode Everywhere"),
    APOTHIC_ENCHANTING("Apothic Enchanting");

    private final String modName;

    Addons(String modName) {
        this.modName = modName;
    }

    @Override
    public String getModId() {
        return name().toLowerCase();
    }

    @Override
    public String getModName() {
        return this.modName;
    }
}
