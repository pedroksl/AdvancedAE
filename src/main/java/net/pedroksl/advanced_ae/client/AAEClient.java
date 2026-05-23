package net.pedroksl.advanced_ae.client;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.client.gui.*;
import net.pedroksl.advanced_ae.client.item.QuantumArmorItemModel;
import net.pedroksl.advanced_ae.client.renderer.QuantumComputerModel;
import net.pedroksl.advanced_ae.client.renderer.ReactionChamberRenderer;
import net.pedroksl.advanced_ae.client.renderer.ThroughputMonitorRenderer;
import net.pedroksl.advanced_ae.common.definitions.AAEBlockEntities;
import net.pedroksl.advanced_ae.common.definitions.AAEFluids;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.parts.ThroughputMonitorPart;
import net.pedroksl.advanced_ae.gui.AdvancedIOBusMenu;
import net.pedroksl.advanced_ae.gui.QuantumCrafterTermMenu;
import net.pedroksl.advanced_ae.gui.StockExportBusMenu;
import net.pedroksl.ae2addonlib.util.WaterBasedFluidType;

import appeng.client.InitScreens;
import appeng.client.api.renderer.parts.RegisterPartRendererEvent;
import appeng.core.AELog;

@SuppressWarnings("unused")
@Mod(value = AdvancedAE.MOD_ID, dist = Dist.CLIENT)
public class AAEClient extends AdvancedAE {

    private static AAEClient INSTANCE;

    // Recipes synchronized from the server
    private RecipeMap recipeMap = RecipeMap.EMPTY;
    private final Set<RecipeType<?>> knownRecipeTypes = Collections.newSetFromMap(new IdentityHashMap<>());

    public AAEClient(IEventBus eventBus, ModContainer container) {
        super(eventBus, container);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        NeoForge.EVENT_BUS.register(AAEClientPlayerEvents.class);

        eventBus.addListener(AAEClient::initScreens);
        eventBus.addListener(AAEClient::initRenderers);
        eventBus.addListener(AAEClient::initClientExtensions);
        eventBus.addListener(AAEClient::registerItemModels);
        eventBus.addListener(AAEClient::initFluidModels);
        eventBus.addListener(this::registerHotkeys);

        eventBus.addListener(new AAEClientNetworkHandler()::registerPackets);

        INSTANCE = this;

        eventBus.addListener(this::registerPartRenderers);
        eventBus.addListener(this::registerBlockStateModels);

        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> {
            AAEHotkeys.INSTANCE.checkHotkeys();
        });

        NeoForge.EVENT_BUS.addListener(this::receiveRecipes);
    }

    public RecipeMap getRecipeMapForType(Level level, RecipeType<?> recipeType) {
        if (level instanceof ClientLevel) {
            if (!knownRecipeTypes.contains(recipeType)) {
                AELog.warn("Haven't received recipes of type {} from server yet.", recipeType);
                return RecipeMap.EMPTY;
            }

            return recipeMap;
        }

        return super.getRecipeMapForType(level, recipeType);
    }

    private static void initScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(
                event, AAEMenus.QUANTUM_COMPUTER.get(), QuantumComputerScreen::new, "/screens/quantum_computer.json");

        InitScreens.register(
                event,
                AAEMenus.ADV_PATTERN_PROVIDER.get(),
                AdvPatternProviderScreen::new,
                "/screens/adv_pattern_provider.json");
        InitScreens.register(
                event,
                AAEMenus.SMALL_ADV_PATTERN_PROVIDER.get(),
                SmallAdvPatternProviderScreen::new,
                "/screens/small_adv_pattern_provider.json");
        InitScreens.register(
                event,
                AAEMenus.ADV_PATTERN_ENCODER.get(),
                AdvPatternEncoderScreen::new,
                "/screens/adv_pattern_encoder.json");
        InitScreens.register(
                event, AAEMenus.REACTION_CHAMBER.get(), ReactionChamberScreen::new, "/screens/reaction_chamber.json");
        InitScreens.register(
                event, AAEMenus.QUANTUM_CRAFTER.get(), QuantumCrafterScreen::new, "/screens/quantum_crafter.json");
        InitScreens.<QuantumCrafterTermMenu, QuantumCrafterTermScreen<QuantumCrafterTermMenu>>register(
                event,
                AAEMenus.QUANTUM_CRAFTER_TERMINAL.get(),
                QuantumCrafterTermScreen::new,
                "/screens/quantum_crafter_terminal.json");
        //        InitScreens.<QuantumCrafterWirelessTermMenu, QuantumCrafterWirelessTermScreen>register(
        //                event,
        //                AAEMenus.QUANTUM_CRAFTER_WIRELESS_TERMINAL.get(),
        //                QuantumCrafterWirelessTermScreen::new,
        //                "/screens/wireless_quantum_crafter_terminal.json");

        InitScreens.<StockExportBusMenu, StockExportBusScreen<StockExportBusMenu>>register(
                event, AAEMenus.STOCK_EXPORT_BUS.get(), StockExportBusScreen::new, "/screens/stock_export_bus.json");
        InitScreens.register(
                event, AAEMenus.IMPORT_EXPORT_BUS.get(), ImportExportBusScreen::new, "/screens/import_export_bus.json");
        InitScreens.<AdvancedIOBusMenu, StockExportBusScreen<AdvancedIOBusMenu>>register(
                event, AAEMenus.ADVANCED_IO_BUS.get(), AdvancedIOBusScreen::new, "/screens/advanced_io_bus.json");

        InitScreens.register(
                event,
                AAEMenus.CRAFTER_PATTERN_CONFIG.get(),
                QuantumCrafterConfigPatternScreen::new,
                "/screens/quantum_crafter_pattern_config.json");

        InitScreens.register(
                event,
                AAEMenus.QUANTUM_ARMOR_CONFIG.get(),
                QuantumArmorConfigScreen::new,
                "/screens/quantum_armor_config.json");
        InitScreens.register(
                event,
                AAEMenus.QUANTUM_ARMOR_NUM_INPUT.get(),
                QuantumArmorNumInputConfigScreen::new,
                "/screens/quantum_armor_num_input_config.json");
        InitScreens.register(
                event,
                AAEMenus.QUANTUM_ARMOR_FILTER_CONFIG.get(),
                QuantumArmorFilterConfigScreen::new,
                "/screens/quantum_armor_filter_config.json");
        InitScreens.register(
                event,
                AAEMenus.QUANTUM_ARMOR_MAGNET.get(),
                QuantumArmorMagnetScreen::new,
                "/screens/quantum_armor_magnet.json");
        InitScreens.register(
                event,
                AAEMenus.QUANTUM_ARMOR_STYLE_CONFIG.get(),
                QuantumArmorStyleConfigScreen::new,
                "/screens/quantum_armor_style.json");
        InitScreens.register(
                event,
                AAEMenus.PORTABLE_WORKBENCH.get(),
                PortableWorkbenchScreen::new,
                "/screens/portable_workbench.json");
    }

    private void registerPartRenderers(RegisterPartRendererEvent event) {
        event.register(ThroughputMonitorPart.class, new ThroughputMonitorRenderer());
    }

    private static void initRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(AAEBlockEntities.REACTION_CHAMBER.get(), ReactionChamberRenderer::new);
    }

    private void registerBlockStateModels(RegisterBlockStateModels event) {
        event.registerModel(QuantumComputerModel.Unbaked.ID, QuantumComputerModel.Unbaked.MAP_CODEC);
    }

    private static void initClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(
                (IClientFluidTypeExtensions) AAEFluids.QUANTUM_INFUSION.fluidType(),
                AAEFluids.QUANTUM_INFUSION.fluidType());
    }

    private static void registerItemModels(RegisterItemModelsEvent event) {
        event.register(QuantumArmorItemModel.Unbaked.ID, QuantumArmorItemModel.Unbaked.MAP_CODEC);
    }

    private static void initFluidModels(RegisterFluidModelsEvent event) {
        for (var fluid : AAEFluids.INSTANCE.getFluids()) {
            if (fluid.fluidType() instanceof WaterBasedFluidType waterBasedFluidType) {
                event.register(waterBasedFluidType.getFluidModel(), fluid::source, fluid::flowing);
            }
        }
    }

    public static AAEClient instance() {
        return INSTANCE;
    }

    @Override
    @Nullable
    public Level getClientLevel() {
        return Minecraft.getInstance().level;
    }

    @Override
    public void registerHotkey(String id) {
        AAEHotkeys.INSTANCE.registerHotkey(id);
    }

    private void registerHotkeys(RegisterKeyMappingsEvent e) {
        AAEHotkeys.INSTANCE.finalizeRegistration(e);
    }

    private void receiveRecipes(RecipesReceivedEvent event) {
        recipeMap = event.getRecipeMap();
        knownRecipeTypes.clear();
        knownRecipeTypes.addAll(event.getRecipeTypes());
    }
}
