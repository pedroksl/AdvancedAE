package net.pedroksl.advanced_ae.common.definitions;

import java.util.function.Supplier;

import net.minecraft.world.inventory.MenuType;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;
import net.pedroksl.advanced_ae.common.entities.QuantumCrafterEntity;
import net.pedroksl.advanced_ae.common.entities.ReactionChamberEntity;
import net.pedroksl.advanced_ae.common.helpers.PortableCellWorkbenchMenuHost;
import net.pedroksl.advanced_ae.common.inventory.AdvPatternEncoderHost;
import net.pedroksl.advanced_ae.common.inventory.QuantumArmorMenuHost;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;
import net.pedroksl.advanced_ae.common.parts.ImportExportBusPart;
import net.pedroksl.advanced_ae.common.parts.StockExportBusPart;
import net.pedroksl.advanced_ae.gui.*;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderMenu;
import net.pedroksl.advanced_ae.gui.advpatternprovider.SmallAdvPatternProviderMenu;
import net.pedroksl.advanced_ae.gui.patternencoder.AdvPatternEncoderMenu;
import net.pedroksl.advanced_ae.gui.quantumcomputer.QuantumComputerMenu;
import net.pedroksl.ae2addonlib.registry.MenuRegistry;

import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;

public class AAEMenus extends MenuRegistry {

    public static final AAEMenus INSTANCE = new AAEMenus();

    AAEMenus() {
        super(AdvancedAE.MOD_ID);
    }

    public static final Supplier<MenuType<QuantumComputerMenu>> QUANTUM_COMPUTER =
            create("quantum_computer", QuantumComputerMenu::new, AdvCraftingBlockEntity.class);

    public static final Supplier<MenuType<AdvPatternProviderMenu>> ADV_PATTERN_PROVIDER =
            create("adv_pattern_provider", AdvPatternProviderMenu::new, AdvPatternProviderLogicHost.class);
    public static final Supplier<MenuType<SmallAdvPatternProviderMenu>> SMALL_ADV_PATTERN_PROVIDER =
            create("small_adv_pattern_provider", SmallAdvPatternProviderMenu::new, AdvPatternProviderLogicHost.class);
    public static final Supplier<MenuType<ReactionChamberMenu>> REACTION_CHAMBER =
            create("reaction_chamber", ReactionChamberMenu::new, ReactionChamberEntity.class);
    public static final Supplier<MenuType<AdvPatternEncoderMenu>> ADV_PATTERN_ENCODER =
            create("adv_pattern_encoder", AdvPatternEncoderMenu::new, AdvPatternEncoderHost.class);
    public static final Supplier<MenuType<QuantumCrafterMenu>> QUANTUM_CRAFTER =
            create("quantum_crafter", QuantumCrafterMenu::new, QuantumCrafterEntity.class);

    public static final Supplier<MenuType<StockExportBusMenu>> STOCK_EXPORT_BUS =
            create("stock_export_bus", StockExportBusMenu::new, StockExportBusPart.class);
    public static final Supplier<MenuType<ImportExportBusMenu>> IMPORT_EXPORT_BUS =
            create("import_export_bus", ImportExportBusMenu::new, ImportExportBusPart.class);

    public static final Supplier<MenuType<QuantumCrafterConfigPatternMenu>> CRAFTER_PATTERN_CONFIG =
            create("quantum_crafter_pattern_config", QuantumCrafterConfigPatternMenu::new, QuantumCrafterEntity.class);

    public static final Supplier<MenuType<QuantumArmorConfigMenu>> QUANTUM_ARMOR_CONFIG =
            create("quantum_armor_config", QuantumArmorConfigMenu::new, QuantumArmorMenuHost.class);
    public static final Supplier<MenuType<QuantumArmorNumInputConfigMenu>> QUANTUM_ARMOR_NUM_INPUT =
            create("quantum_armor_num_input", QuantumArmorNumInputConfigMenu::new, ISubMenuHost.class);
    public static final Supplier<MenuType<QuantumArmorFilterConfigMenu>> QUANTUM_ARMOR_FILTER_CONFIG =
            create("quantum_armor_filter_config", QuantumArmorFilterConfigMenu::new, ISubMenuHost.class);
    public static final Supplier<MenuType<QuantumArmorMagnetMenu>> QUANTUM_ARMOR_MAGNET =
            create("quantum_armor_magnet", QuantumArmorMagnetMenu::new, ISubMenuHost.class);
    public static final Supplier<MenuType<QuantumArmorStyleConfigMenu>> QUANTUM_ARMOR_STYLE_CONFIG =
            create("quantum_armor_style_config", QuantumArmorStyleConfigMenu::new, QuantumArmorMenuHost.class);
    public static final Supplier<MenuType<PortableWorkbenchMenu>> PORTABLE_WORKBENCH =
            create("portable_workbench", PortableWorkbenchMenu::new, PortableCellWorkbenchMenuHost.class);

    private static <M extends AEBaseMenu, H> Supplier<MenuType<M>> create(
            String id, MenuTypeBuilder.MenuFactory<M, H> factory, Class<H> host) {
        return create(AdvancedAE.MOD_ID, id, factory, host);
    }
}
