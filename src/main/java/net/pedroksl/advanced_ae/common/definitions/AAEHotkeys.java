package net.pedroksl.advanced_ae.common.definitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.helpers.ArmorHotkeyAction;

import appeng.api.features.HotkeyAction;
import appeng.hotkeys.CuriosHotkeyAction;
import appeng.hotkeys.InventoryHotkeyAction;

public class AAEHotkeys {
    public static final Map<String, List<HotkeyAction>> REGISTRY = new HashMap<>();

    public static final String ARMOR_CONFIG = "quantum_armor_config";
    public static final String PATTERN_ENCODER_HOTKEY = "pattern_encoder_action";

    public static void init() {
        registerArmorAction(
                AAEItems.QUANTUM_HELMET,
                (player, locator) -> AAEItems.QUANTUM_HELMET.get().openFromEquipmentSlot(player, locator),
                ARMOR_CONFIG);
        registerArmorAction(
                AAEItems.QUANTUM_CHESTPLATE,
                (player, locator) -> AAEItems.QUANTUM_CHESTPLATE.get().openFromEquipmentSlot(player, locator),
                ARMOR_CONFIG);
        registerArmorAction(
                AAEItems.QUANTUM_LEGGINGS,
                (player, locator) -> AAEItems.QUANTUM_LEGGINGS.get().openFromEquipmentSlot(player, locator),
                ARMOR_CONFIG);
        registerArmorAction(
                AAEItems.QUANTUM_BOOTS,
                (player, locator) -> AAEItems.QUANTUM_BOOTS.get().openFromEquipmentSlot(player, locator),
                ARMOR_CONFIG);
        register(
                AAEItems.ADV_PATTERN_ENCODER,
                (player, locator) -> AAEItems.ADV_PATTERN_ENCODER.get().openFromInventory(player, locator),
                PATTERN_ENCODER_HOTKEY);
    }

    public static void register(ItemLike item, InventoryHotkeyAction.Opener opener, String id) {
        register(new InventoryHotkeyAction(item, opener), id);
        register(new CuriosHotkeyAction(item, opener), id);
    }

    public static void registerArmorAction(ItemLike item, ArmorHotkeyAction.Opener opener, String id) {
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
