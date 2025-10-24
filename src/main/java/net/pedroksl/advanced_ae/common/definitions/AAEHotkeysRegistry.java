package net.pedroksl.advanced_ae.common.definitions;

import org.lwjgl.glfw.GLFW;

import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.client.AAEHotkeys;
import net.pedroksl.advanced_ae.common.helpers.ToggleUpgradeCardAction;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.ae2addonlib.registry.HotkeyRegistry;

public final class AAEHotkeysRegistry extends HotkeyRegistry {

    public static final AAEHotkeysRegistry INSTANCE = new AAEHotkeysRegistry();

    AAEHotkeysRegistry() {
        super(AdvancedAE.MOD_ID, id -> Keys.valueOf(id).getDefaultHotkey(), AAEHotkeys.INSTANCE::registerHotkey);
    }

    public enum Keys {
        ARMOR_CONFIG("quantum_armor_config", "Open Quantum Armor Configuration", GLFW.GLFW_KEY_N),
        PATTERN_ENCODER("pattern_encoder_action", "Open Advanced Pattern Encoder"),
        QUANTUM_MAGNET_UPGRADE("quantum_magnet_upgrade", "Toggle Quantum Armor Magnet", GLFW.GLFW_KEY_G),
        QUANTUM_AUTO_STOCK_UPGRADE("quantum_auto_stock_upgrade", "Toggle Quantum Armor Auto Stock", GLFW.GLFW_KEY_J),
        QUANTUM_NIGHT_VISION_UPGRADE("quantum_night_vision_upgrade", "Toggle Quantum Armor Night Vision"),
        PORTABLE_WORKBENCH("portable_workbench", "Open Portable Workbench", GLFW.GLFW_KEY_C),
        PICK_CRAFT("pick_craft", "Attempt crafting targeted block", GLFW.GLFW_KEY_V);

        private final String id;
        private final String englishTranslation;
        private final int defaultHotkey;

        Keys(String id, String englishTranslation) {
            this(id, englishTranslation, GLFW.GLFW_KEY_UNKNOWN);
        }

        Keys(String id, String englishTranslation, int defaultHotekey) {
            this.id = id;
            this.englishTranslation = englishTranslation;
            this.defaultHotkey = defaultHotekey;
        }

        public String getId() {
            return this.id;
        }

        public String getEnglishTranslation() {
            return this.englishTranslation;
        }

        public int getDefaultHotkey() {
            return this.defaultHotkey;
        }
    }

    public void init() {
        registerArmorAction(
                AAEItems.QUANTUM_HELMET,
                (player, slot, stack) -> AAEItems.QUANTUM_HELMET.get().openFromEquipmentSlot(player, slot, stack),
                Keys.ARMOR_CONFIG.id);
        registerArmorAction(
                AAEItems.QUANTUM_CHESTPLATE,
                (player, slot, stack) -> AAEItems.QUANTUM_CHESTPLATE.get().openFromEquipmentSlot(player, slot, stack),
                Keys.ARMOR_CONFIG.id);
        registerArmorAction(
                AAEItems.QUANTUM_LEGGINGS,
                (player, slot, stack) -> AAEItems.QUANTUM_LEGGINGS.get().openFromEquipmentSlot(player, slot, stack),
                Keys.ARMOR_CONFIG.id);
        registerArmorAction(
                AAEItems.QUANTUM_BOOTS,
                (player, slot, stack) -> AAEItems.QUANTUM_BOOTS.get().openFromEquipmentSlot(player, slot, stack),
                Keys.ARMOR_CONFIG.id);
        register(
                AAEItems.ADV_PATTERN_ENCODER,
                (player, locator) -> AAEItems.ADV_PATTERN_ENCODER.get().openFromInventory(player, locator),
                Keys.PATTERN_ENCODER.id);

        registerToggleUpgradeAction(
                AAEItems.QUANTUM_HELMET,
                (player, stack) -> AAEItems.QUANTUM_HELMET.get().toggleUpgrade(stack, UpgradeType.MAGNET, player),
                Keys.QUANTUM_MAGNET_UPGRADE.id);
        registerToggleUpgradeAction(
                AAEItems.QUANTUM_HELMET,
                (player, stack) -> AAEItems.QUANTUM_HELMET.get().toggleUpgrade(stack, UpgradeType.AUTO_STOCK, player),
                Keys.QUANTUM_AUTO_STOCK_UPGRADE.id);
        registerToggleUpgradeAction(
                AAEItems.QUANTUM_HELMET,
                (player, stack) -> AAEItems.QUANTUM_HELMET.get().toggleUpgrade(stack, UpgradeType.NIGHT_VISION, player),
                Keys.QUANTUM_NIGHT_VISION_UPGRADE.id);

        registerArmorAction(
                AAEItems.QUANTUM_HELMET,
                (player, slot, stack) -> AAEItems.QUANTUM_HELMET.get().openPortableWorkbench(player, slot, stack),
                Keys.PORTABLE_WORKBENCH.id);
        registerArmorAction(
                AAEItems.QUANTUM_CHESTPLATE,
                (player, slot, stack) -> AAEItems.QUANTUM_CHESTPLATE.get().attemptCraftingTarget(player, slot, stack),
                Keys.PICK_CRAFT.id);
    }

    public void registerToggleUpgradeAction(ItemLike item, ToggleUpgradeCardAction.Opener opener, String id) {
        register(new ToggleUpgradeCardAction(item, opener), id);
    }
}
