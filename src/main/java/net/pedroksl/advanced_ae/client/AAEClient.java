package net.pedroksl.advanced_ae.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.BucketItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.client.gui.*;
import net.pedroksl.advanced_ae.client.renderer.AAECraftingUnitModelProvider;
import net.pedroksl.advanced_ae.client.renderer.ReactionChamberTESR;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
import net.pedroksl.advanced_ae.common.definitions.AAEBlockEntities;
import net.pedroksl.advanced_ae.common.definitions.AAEFluids;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.gui.AdvancedIOBusMenu;
import net.pedroksl.advanced_ae.gui.QuantumCrafterTermMenu;
import net.pedroksl.advanced_ae.gui.QuantumCrafterWirelessTermMenu;
import net.pedroksl.advanced_ae.gui.StockExportBusMenu;

import appeng.api.util.AEColor;
import appeng.client.gui.me.common.PinnedKeys;
import appeng.client.render.StaticItemColor;
import appeng.client.render.crafting.CraftingCubeModel;
import appeng.hooks.BuiltInModelHooks;
import appeng.init.client.InitScreens;

@SuppressWarnings("unused")
@Mod(value = AdvancedAE.MOD_ID, dist = Dist.CLIENT)
public class AAEClient extends AdvancedAE {

    private static AAEClient INSTANCE;

    public AAEClient(IEventBus eventBus, ModContainer container) {
        super(eventBus, container);

        initBuiltInModels();

        eventBus.addListener(AAEClient::initScreens);
        eventBus.addListener(AAEClient::initItemBlockRenderTypes);
        eventBus.addListener(AAEClient::initItemColours);
        eventBus.addListener(AAEClient::initRenderers);
        eventBus.addListener(AAEClient::initClientExtensions);
        eventBus.addListener(this::registerHotkeys);

        INSTANCE = this;

        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> {
            tickPinnedKeys(Minecraft.getInstance());
            Hotkeys.checkHotkeys();
        });
    }

    private static void initBuiltInModels() {
        for (AAECraftingUnitType type : AAECraftingUnitType.values()) {
            BuiltInModelHooks.addBuiltInModel(
                    AdvancedAE.makeId("block/crafting/" + type.getAffix() + "_formed"),
                    new CraftingCubeModel(new AAECraftingUnitModelProvider(type)));
        }
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
        InitScreens.<QuantumCrafterWirelessTermMenu, QuantumCrafterWirelessTermScreen>register(
                event,
                AAEMenus.QUANTUM_CRAFTER_WIRELESS_TERMINAL.get(),
                QuantumCrafterWirelessTermScreen::new,
                "/screens/wireless_quantum_crafter_terminal.json");

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
        InitScreens.register(event, AAEMenus.SET_AMOUNT.get(), SetAmountScreen::new, "/screens/aae_set_amount.json");

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

    @SuppressWarnings("deprecation")
    private static void initItemBlockRenderTypes(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(AAEFluids.QUANTUM_INFUSION.source(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(AAEFluids.QUANTUM_INFUSION.flowing(), RenderType.translucent());
        });
    }

    private void tickPinnedKeys(Minecraft minecraft) {
        // Only prune pinned keys when no screen is currently open
        if (minecraft.screen == null) {
            PinnedKeys.prune();
        }
    }

    @Override
    public void registerHotkey(String id) {
        Hotkeys.registerHotkey(id);
    }

    private void registerHotkeys(RegisterKeyMappingsEvent e) {
        Hotkeys.finalizeRegistration(e::register);
    }

    @SuppressWarnings("deprecation")
    private static void initItemColours(RegisterColorHandlersEvent.Item event) {
        event.register(makeOpaque(new StaticItemColor(AEColor.TRANSPARENT)), AAEItems.THROUGHPUT_MONITOR.asItem());
        event.register(
                makeOpaque(new StaticItemColor(AEColor.TRANSPARENT)), AAEItems.QUANTUM_CRAFTER_TERMINAL.asItem());

        for (var bucket : AAEFluids.INSTANCE.getFluids()) {
            event.getItemColors()
                    .register(
                            (stack, index) -> {
                                if (index == 1 && stack.getItem() instanceof BucketItem bucketItem) {
                                    return IClientFluidTypeExtensions.of(bucketItem.content)
                                            .getTintColor();
                                }
                                return 0xFFFFFFFF;
                            },
                            bucket.bucketItem());
        }
    }

    private static void initRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(AAEBlockEntities.REACTION_CHAMBER.get(), ReactionChamberTESR::new);
    }

    private static void initClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(
                (IClientFluidTypeExtensions) AAEFluids.QUANTUM_INFUSION.fluidType(),
                AAEFluids.QUANTUM_INFUSION.fluidType());
    }

    private static ItemColor makeOpaque(ItemColor itemColor) {
        return (stack, tintIndex) -> FastColor.ARGB32.opaque(itemColor.getColor(stack, tintIndex));
    }

    public static AAEClient instance() {
        return INSTANCE;
    }
}
