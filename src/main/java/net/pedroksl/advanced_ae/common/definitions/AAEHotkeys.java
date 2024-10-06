package net.pedroksl.advanced_ae.common.definitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.helpers.ArmorHotkeyAction;

import appeng.api.features.HotkeyAction;

public class AAEHotkeys {
    public static final Map<String, List<HotkeyAction>> REGISTRY = new HashMap<>();

    public static void init() {
        register(
                AAEItems.QUANTUM_HELMET,
                (player, locator) -> AAEItems.QUANTUM_HELMET.get().openFromEquipmentSlot(player, locator),
                ArmorHotkeyAction.ARMOR_CONFIG);
        register(
                AAEItems.QUANTUM_CHESTPLATE,
                (player, locator) -> AAEItems.QUANTUM_CHESTPLATE.get().openFromEquipmentSlot(player, locator),
                ArmorHotkeyAction.ARMOR_CONFIG);
        register(
                AAEItems.QUANTUM_LEGGINGS,
                (player, locator) -> AAEItems.QUANTUM_LEGGINGS.get().openFromEquipmentSlot(player, locator),
                ArmorHotkeyAction.ARMOR_CONFIG);
        register(
                AAEItems.QUANTUM_BOOTS,
                (player, locator) -> AAEItems.QUANTUM_BOOTS.get().openFromEquipmentSlot(player, locator),
                ArmorHotkeyAction.ARMOR_CONFIG);
    }

    public static void register(ItemLike item, ArmorHotkeyAction.Opener opener, String id) {
        register(new ArmorHotkeyAction(item, opener), id);
    }

    public static synchronized void register(HotkeyAction hotkeyAction, String id) {
        if (REGISTRY.containsKey(id)) {
            REGISTRY.get(id).addFirst(hotkeyAction);
        } else {
            REGISTRY.put(id, new ArrayList<>(List.of(hotkeyAction)));
            AdvancedAE.instance().registerHotkey(id);
        }
    }
}
