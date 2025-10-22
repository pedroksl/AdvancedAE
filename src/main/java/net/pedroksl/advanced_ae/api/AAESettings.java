package net.pedroksl.advanced_ae.api;

import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.ae2addonlib.api.SettingsRegistry;

import appeng.api.config.Setting;
import appeng.api.config.YesNo;

public final class AAESettings extends SettingsRegistry {

    public static final Setting<YesNo> ME_EXPORT = register("me_export", YesNo.YES, YesNo.NO);
    public static final Setting<YesNo> FILTERED_IMPORT = register("filtered_import", YesNo.YES, YesNo.NO);
    public static final Setting<YesNo> QUANTUM_CRAFTER_TERMINAL =
            register("quantum_crafter_terminal", YesNo.YES, YesNo.NO);
    public static final Setting<ShowQuantumCrafters> TERMINAL_SHOW_QUANTUM_CRAFTERS =
            register(AdvancedAE.MOD_ID, "show_quantum_crafters", ShowQuantumCrafters.class);
    public static final Setting<YesNo> REGULATE_STOCK = register("regulate_stock", YesNo.YES, YesNo.NO);

    @SafeVarargs
    private static synchronized <T extends Enum<T>> Setting<T> register(String name, T firstOption, T... moreOptions) {
        return register(AdvancedAE.MOD_ID, name, firstOption, moreOptions);
    }

    public static Setting<?> getOrThrow(String name) {
        return getOrThrow(AdvancedAE.MOD_ID, name);
    }
}
