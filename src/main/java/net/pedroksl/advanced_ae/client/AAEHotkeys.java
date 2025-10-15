package net.pedroksl.advanced_ae.client;

import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.ae2addonlib.client.Hotkeys;

public class AAEHotkeys extends Hotkeys {

    public static final AAEHotkeys INSTANCE = new AAEHotkeys();

    AAEHotkeys() {
        super(AdvancedAE.MOD_ID);
    }
}
