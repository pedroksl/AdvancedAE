package net.pedroksl.advanced_ae.client;

import java.util.HashMap;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.common.definitions.AAEHotkeys;
import net.pedroksl.advanced_ae.network.packet.AAEHotkeyPacket;

import appeng.core.network.ServerboundPacket;

public class Hotkeys {
    private static final HashMap<String, AAEHotkey> HOTKEYS = new HashMap<>();

    private static boolean finalized;

    private static AAEHotkey createHotkey(String id) {
        var defaultHotkey = getDefaultHotkey(id);

        if (finalized) {
            throw new IllegalStateException("Hotkey registration already finalized!");
        }
        return new AAEHotkey(id, new KeyMapping("key.advanced_ae." + id, defaultHotkey, "key.advanced_ae.category"));
    }

    private static void registerHotkey(AAEHotkey hotkey) {
        HOTKEYS.put(hotkey.name(), hotkey);
    }

    public static void finalizeRegistration(Consumer<KeyMapping> register) {
        for (var value : HOTKEYS.values()) {
            register.accept(value.mapping());
        }
        finalized = true;
    }

    public static void registerHotkey(String id) {
        registerHotkey(createHotkey(id));
    }

    public static void checkHotkeys() {
        HOTKEYS.forEach((name, hotkey) -> hotkey.check());
    }

    @Nullable
    public static AAEHotkey getHotkeyMapping(@Nullable String id) {
        return HOTKEYS.get(id);
    }

    public record AAEHotkey(String name, KeyMapping mapping) {
        public void check() {
            while (this.mapping().consumeClick()) {
                ServerboundPacket message = new AAEHotkeyPacket(this);
                PacketDistributor.sendToServer(message);
            }
        }
    }

    private static int getDefaultHotkey(String id) {
        return AAEHotkeys.getDefaultHotkey(id);
    }
}
