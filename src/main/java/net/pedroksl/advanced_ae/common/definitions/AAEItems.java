package net.pedroksl.advanced_ae.common.definitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.items.AdvPatternEncoderItem;
import net.pedroksl.advanced_ae.common.items.AdvPatternProviderCapacityUpgradeItem;
import net.pedroksl.advanced_ae.common.items.AdvPatternProviderUpgradeItem;
import net.pedroksl.advanced_ae.common.items.ShatteredSingularityItem;
import net.pedroksl.advanced_ae.common.items.armors.QuantumBoots;
import net.pedroksl.advanced_ae.common.items.armors.QuantumChestplate;
import net.pedroksl.advanced_ae.common.items.armors.QuantumHelmet;
import net.pedroksl.advanced_ae.common.items.armors.QuantumLeggings;
import net.pedroksl.advanced_ae.common.items.upgrades.QuantumUpgradeBaseItem;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.common.parts.AdvPatternProviderPart;
import net.pedroksl.advanced_ae.common.parts.SmallAdvPatternProviderPart;
import net.pedroksl.advanced_ae.common.parts.StockExportBusPart;
import net.pedroksl.advanced_ae.common.parts.ThroughputMonitorPart;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPattern;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.core.definitions.ItemDefinition;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;

public class AAEItems {
    public static final DeferredRegister.Items DR = DeferredRegister.createItems(AdvancedAE.MOD_ID);

    private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    public static List<ItemDefinition<?>> getItems() {
        return Collections.unmodifiableList(ITEMS);
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
            part("Stock Export Bus", "stock_export_bus_part", StockExportBusPart.class, StockExportBusPart::new);
    public static final ItemDefinition<PartItem<ThroughputMonitorPart>> THROUGHPUT_MONITOR = part(
            "ME Throughput Monitor", "throughput_monitor", ThroughputMonitorPart.class, ThroughputMonitorPart::new);

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
    public static final ItemDefinition<Item> SHATTERED_SINGULARITY =
            item("Shattered Singularity", "shattered_singularity", ShatteredSingularityItem::new);

    public static final ItemDefinition<AdvPatternEncoderItem> ADV_PATTERN_ENCODER =
            item("Advanced Pattern Encoder", "adv_pattern_encoder", p -> new AdvPatternEncoderItem(p.stacksTo(1)));

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

    private static <T extends Item> ItemDefinition<T> item(
            String englishName, String id, Function<Item.Properties, T> factory) {
        var definition = new ItemDefinition<>(englishName, DR.registerItem(id, factory));
        ITEMS.add(definition);
        return definition;
    }

    private static <T extends IPart> ItemDefinition<PartItem<T>> part(
            String englishName, String id, Class<T> partClass, Function<IPartItem<T>, T> factory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return item(englishName, id, p -> new PartItem<>(p, partClass, factory));
    }
}
