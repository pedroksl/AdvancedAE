package net.pedroksl.advanced_ae.common.definitions;

import java.util.List;

import net.minecraft.world.item.Item;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.items.AdvPatternEncoderItem;
import net.pedroksl.advanced_ae.common.items.AdvPatternProviderCapacityUpgradeItem;
import net.pedroksl.advanced_ae.common.items.AdvPatternProviderUpgradeItem;
import net.pedroksl.advanced_ae.common.items.QuantumCrafterWirelessTerminalItem;
import net.pedroksl.advanced_ae.common.items.armors.*;
import net.pedroksl.advanced_ae.common.items.upgrades.QuantumUpgradeBaseItem;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.common.parts.*;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPattern;
import net.pedroksl.advanced_ae.xmod.wtlib.AE2wtlibPlugin;
import net.pedroksl.ae2addonlib.registry.AddonItems;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.MaterialItem;
import appeng.items.parts.PartItem;

public class AAEItems extends AddonItems {

    public static final AAEItems INSTANCE = new AAEItems();

    AAEItems() {
        super(AdvancedAE.MOD_ID);
    }

    public static List<ItemDefinition<?>> getQuantumArmor() {
        return getItems().stream()
                .filter(item -> item.stack().getItem() instanceof QuantumArmorBase)
                .toList();
    }

    public static List<ItemDefinition<?>> getQuantumCards() {
        return getItems().stream()
                .filter(item -> item.stack().getItem() instanceof QuantumUpgradeBaseItem)
                .toList();
    }

    public static final ItemDefinition<PartItem<AdvPatternProviderPart>> ADV_PATTERN_PROVIDER = part(
            "Advanced Extended Pattern Provider",
            "adv_pattern_provider_part",
            AdvPatternProviderPart.class,
            AdvPatternProviderPart::new);
    public static final ItemDefinition<PartItem<SmallAdvPatternProviderPart>> SMALL_ADV_PATTERN_PROVIDER = part(
            "Advanced Pattern Provider",
            "small_adv_pattern_provider_part",
            SmallAdvPatternProviderPart.class,
            SmallAdvPatternProviderPart::new);
    public static final ItemDefinition<PartItem<StockExportBusPart>> STOCK_EXPORT_BUS =
            part("ME Stock Export Bus", "stock_export_bus_part", StockExportBusPart.class, StockExportBusPart::new);
    public static final ItemDefinition<PartItem<ImportExportBusPart>> IMPORT_EXPORT_BUS =
            part("ME Import Export Bus", "import_export_bus_part", ImportExportBusPart.class, ImportExportBusPart::new);
    public static final ItemDefinition<PartItem<AdvancedIOBusPart>> ADVANCED_IO_BUS =
            part("ME Advanced IO Bus", "advanced_io_bus_part", AdvancedIOBusPart.class, AdvancedIOBusPart::new);
    public static final ItemDefinition<PartItem<ThroughputMonitorPart>> THROUGHPUT_MONITOR = part(
            "ME Throughput Monitor", "throughput_monitor", ThroughputMonitorPart.class, ThroughputMonitorPart::new);
    public static final ItemDefinition<PartItem<QuantumCrafterTerminalPart>> QUANTUM_CRAFTER_TERMINAL = part(
            "Quantum Crafter Terminal",
            "quantum_crafter_terminal",
            QuantumCrafterTerminalPart.class,
            QuantumCrafterTerminalPart::new);
    public static final ItemDefinition<QuantumCrafterWirelessTerminalItem> QUANTUM_CRAFTER_WIRELESS_TERMINAL = item(
            "Wireless Quantum Crafter Terminal", "wireless_quantum_crafter_terminal", p -> AE2wtlibPlugin.TERMINAL);

    public static final ItemDefinition<Item> ADV_PROCESSING_PATTERN = item(
            "Advanced Processing Pattern",
            "adv_processing_pattern",
            p -> PatternDetailsHelper.encodedPatternItemBuilder(AdvProcessingPattern::new)
                    .invalidPatternTooltip(AdvProcessingPattern::getInvalidPatternTooltip)
                    .build());

    public static final ItemDefinition<Item> ADV_PATTERN_PROVIDER_UPGRADE = item(
            "Advanced Pattern Provider Upgrade", "adv_pattern_provider_upgrade", AdvPatternProviderUpgradeItem::new);
    public static final ItemDefinition<Item> ADV_PATTERN_PROVIDER_CAPACITY_UPGRADE = item(
            "Advanced Pattern Provider Capacity Upgrade",
            "adv_pattern_provider_capacity_upgrade",
            AdvPatternProviderCapacityUpgradeItem::new);
    public static final ItemDefinition<MaterialItem> QUANTUM_INFUSED_DUST =
            item("Quantum Infused Dust", "quantum_infused_dust", MaterialItem::new);
    public static final ItemDefinition<MaterialItem> QUANTUM_ALLOY =
            item("Quantum Alloy", "quantum_alloy", MaterialItem::new);
    public static final ItemDefinition<MaterialItem> QUANTUM_ALLOY_PLATE =
            item("Quantum Alloy Plate", "quantum_alloy_plate", MaterialItem::new);
    public static final ItemDefinition<MaterialItem> SHATTERED_SINGULARITY =
            item("Shattered Singularity", "shattered_singularity", MaterialItem::new);
    public static final ItemDefinition<MaterialItem> QUANTUM_PROCESSOR_PRESS =
            item("Inscriber Quantum Press", "quantum_processor_press", MaterialItem::new);
    public static final ItemDefinition<MaterialItem> QUANTUM_PROCESSOR_PRINT =
            item("Printed Quantum Circuit", "printed_quantum_processor", MaterialItem::new);
    public static final ItemDefinition<MaterialItem> QUANTUM_PROCESSOR =
            item("Quantum Processor", "quantum_processor", MaterialItem::new);
    public static final ItemDefinition<MaterialItem> QUANTUM_STORAGE_COMPONENT =
            item("Quantum Storage Component", "quantum_storage_component", MaterialItem::new);

    public static final ItemDefinition<AdvPatternEncoderItem> ADV_PATTERN_ENCODER =
            item("Advanced Pattern Encoder", "adv_pattern_encoder", AdvPatternEncoderItem::new);

    public static final ItemDefinition<Item> MONITOR_CONFIGURATOR =
            item("Throughput Monitor Configurator", "throughput_monitor_configurator", Item::new);

    public static final ItemDefinition<QuantumHelmet> QUANTUM_HELMET =
            item("Quantum Helmet", "quantum_helmet", QuantumHelmet::new);
    public static final ItemDefinition<QuantumChestplate> QUANTUM_CHESTPLATE =
            item("Quantum Chestplate", "quantum_chestplate", QuantumChestplate::new);
    public static final ItemDefinition<QuantumLeggings> QUANTUM_LEGGINGS =
            item("Quantum Leggings", "quantum_leggings", QuantumLeggings::new);
    public static final ItemDefinition<QuantumBoots> QUANTUM_BOOTS =
            item("Quantum Boots", "quantum_boots", QuantumBoots::new);

    public static final ItemDefinition<QuantumUpgradeBaseItem> QUANTUM_UPGRADE_BASE =
            item("Quantum Upgrade Base Card", "quantum_upgrade_base", QuantumUpgradeBaseItem::new);
    public static final ItemDefinition<QuantumUpgradeBaseItem> WALK_SPEED_CARD =
            item("Walk Speed Card", "walk_speed_card", p -> new QuantumUpgradeBaseItem(UpgradeType.WALK_SPEED, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> SPRINT_SPEED_CARD = item(
            "Sprint Speed Card", "sprint_speed_card", p -> new QuantumUpgradeBaseItem(UpgradeType.SPRINT_SPEED, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> STEP_ASSIST_CARD =
            item("Step Assist Card", "step_assist_card", p -> new QuantumUpgradeBaseItem(UpgradeType.STEP_ASSIST, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> JUMP_HEIGHT_CARD =
            item("Jump Height Card", "jump_height_card", p -> new QuantumUpgradeBaseItem(UpgradeType.JUMP_HEIGHT, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> LAVA_IMMUNITY_CARD = item(
            "Lava Immunity Card", "lava_immunity_card", p -> new QuantumUpgradeBaseItem(UpgradeType.LAVA_IMMUNITY, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> FLIGHT_CARD =
            item("Flight Card", "flight_card", p -> new QuantumUpgradeBaseItem(UpgradeType.FLIGHT, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> WATER_BREATHING_CARD = item(
            "Water Breathing Card",
            "water_breathing_card",
            p -> new QuantumUpgradeBaseItem(UpgradeType.WATER_BREATHING, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> AUTO_FEED_CARD =
            item("Auto Feeding Card", "auto_feeding_card", p -> new QuantumUpgradeBaseItem(UpgradeType.AUTO_FEED, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> AUTO_STOCK_CARD =
            item("Auto Stock Card", "auto_stock_card", p -> new QuantumUpgradeBaseItem(UpgradeType.AUTO_STOCK, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> MAGNET_CARD =
            item("Magnet Card", "magnet_card", p -> new QuantumUpgradeBaseItem(UpgradeType.MAGNET, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> HP_BUFFER_CARD =
            item("HP Buffer Card", "hp_buffer_card", p -> new QuantumUpgradeBaseItem(UpgradeType.HP_BUFFER, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> EVASION_CARD =
            item("Evasion Card", "evasion_card", p -> new QuantumUpgradeBaseItem(UpgradeType.EVASION, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> REGENERATION_CARD = item(
            "Regeneration Card", "regeneration_card", p -> new QuantumUpgradeBaseItem(UpgradeType.REGENERATION, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> STRENGTH_CARD =
            item("Strength Card", "strength_card", p -> new QuantumUpgradeBaseItem(UpgradeType.STRENGTH, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> ATTACK_SPEED_CARD = item(
            "Attack Speed Card", "attack_speed_card", p -> new QuantumUpgradeBaseItem(UpgradeType.ATTACK_SPEED, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> LUCK_CARD =
            item("Luck Card", "luck_card", p -> new QuantumUpgradeBaseItem(UpgradeType.LUCK, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> REACH_CARD =
            item("Reach Card", "reach_card", p -> new QuantumUpgradeBaseItem(UpgradeType.REACH, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> SWIM_SPEED_CARD =
            item("Swim Speed Card", "swim_speed_card", p -> new QuantumUpgradeBaseItem(UpgradeType.SWIM_SPEED, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> NIGHT_VISION_CARD = item(
            "Night Vision Card", "night_vision_card", p -> new QuantumUpgradeBaseItem(UpgradeType.NIGHT_VISION, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> FLIGHT_DRIFT_CARD = item(
            "Flight Drift Card", "flight_drift_card", p -> new QuantumUpgradeBaseItem(UpgradeType.FLIGHT_DRIFT, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> RECHARGING_CARD =
            item("ME Recharging Card", "recharging_card", p -> new QuantumUpgradeBaseItem(UpgradeType.CHARGING, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> WORKBENCH_CARD = item(
            "Portable Workbench Card",
            "portable_workbench_card",
            p -> new QuantumUpgradeBaseItem(UpgradeType.WORKBENCH, p));
    public static final ItemDefinition<QuantumUpgradeBaseItem> PICK_CRAFT_CARD =
            item("Pick Craft Card", "pick_craft_card", p -> new QuantumUpgradeBaseItem(UpgradeType.PICK_CRAFT, p));
    //    public static final ItemDefinition<QuantumUpgradeBaseItem> HUD_CARD =
    //            item("HUD Card", "hud_card", p -> new QuantumUpgradeBaseItem(UpgradeType.HUD, p));
}
