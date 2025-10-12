package net.pedroksl.advanced_ae.api;

import net.pedroksl.ae2addonlib.api.AddonSettings;

import appeng.api.config.Setting;
import appeng.api.config.YesNo;

public final class AAESettings extends AddonSettings {

    public static final Setting<YesNo> ME_EXPORT = register("me_export", YesNo.YES, YesNo.NO);
    public static final Setting<YesNo> FILTERED_IMPORT = register("filtered_import", YesNo.YES, YesNo.NO);
    public static final Setting<YesNo> QUANTUM_CRAFTER_TERMINAL =
            register("quantum_crafter_terminal", YesNo.YES, YesNo.NO);
    public static final Setting<ShowQuantumCrafters> TERMINAL_SHOW_QUANTUM_CRAFTERS =
            register("show_quantum_crafters", ShowQuantumCrafters.class);
    public static final Setting<YesNo> REGULATE_STOCK = register("regulate_stock", YesNo.YES, YesNo.NO);
}
