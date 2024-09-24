package net.pedroksl.advanced_ae.api;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import appeng.api.config.Setting;
import appeng.api.config.YesNo;

public final class AAESettings {
    private static final Map<String, Setting<?>> SETTINGS = new HashMap<>();
    public static final Setting<YesNo> ME_EXPORT = register("me_export", YesNo.YES, YesNo.NO);

    private AAESettings() {}

    private static synchronized <T extends Enum<T>> Setting<T> register(String name, Class<T> enumClass) {
        Preconditions.checkState(!SETTINGS.containsKey(name));
        Setting<T> setting = new Setting<>(name, enumClass);
        SETTINGS.put(name, setting);
        return setting;
    }

    @SafeVarargs
    private static synchronized <T extends Enum<T>> Setting<T> register(String name, T firstOption, T... moreOptions) {
        Preconditions.checkState(!SETTINGS.containsKey(name));
        Setting<T> setting = new Setting<>(name, firstOption.getDeclaringClass(), EnumSet.of(firstOption, moreOptions));
        SETTINGS.put(name, setting);
        return setting;
    }

    public static Setting<?> getOrThrow(String name) {
        var setting = SETTINGS.get(name);
        if (setting == null) {
            throw new IllegalArgumentException("Unknown setting '" + name + "'");
        }
        return setting;
    }
}
