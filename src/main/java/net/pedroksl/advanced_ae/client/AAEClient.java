package net.pedroksl.advanced_ae.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.client.gui.*;
import net.pedroksl.advanced_ae.client.renderer.AAECraftingUnitModelProvider;
import net.pedroksl.advanced_ae.client.renderer.ReactionChamberTESR;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
import net.pedroksl.advanced_ae.common.definitions.AAEBlockEntities;
import net.pedroksl.advanced_ae.common.definitions.AAEFluids;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.events.AAEClientPlayerEvents;

import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.client.render.crafting.CraftingCubeModel;
import appeng.hooks.BuiltInModelHooks;
import appeng.init.client.InitScreens;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class AAEClient extends AdvancedAE {
    private static final Logger LOGGER = LoggerFactory.getLogger(AAEClient.class);

    private static AAEClient INSTANCE;

    public AAEClient() {
        super();

        initBuiltInModels();

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        AAEClientPlayerEvents.init();

        eventBus.addListener(AAEClient::initItemBlockRenderTypes);
        eventBus.addListener(AAEClient::initItemColours);
        eventBus.addListener(AAEClient::initRenderers);
        eventBus.addListener(this::registerHotkeys);

        INSTANCE = this;

        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent e) -> {
            if (e.phase == TickEvent.Phase.END) {
                AAEHotkeys.INSTANCE.checkHotkeys();
            }
        });

        eventBus.addListener(this::clientSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            try {
                initScreens();
            } catch (Throwable e) {
                LOGGER.error("AAE failed postClientSetup", e);
                throw new RuntimeException(e);
            }
        });
    }

    private static void initBuiltInModels() {
        var type = AAECraftingUnitType.STRUCTURE;
        BuiltInModelHooks.addBuiltInModel(
                AdvancedAE.makeId("block/crafting/" + type.getAffix() + "_formed"),
                new CraftingCubeModel(new AAECraftingUnitModelProvider(type)));
    }

    private static void initScreens() {
        InitScreens.register(
                AAEMenus.QUANTUM_COMPUTER.get(), QuantumComputerScreen::new, "/screens/quantum_computer.json");
        InitScreens.register(
                AAEMenus.ADV_PATTERN_PROVIDER.get(),
                AdvPatternProviderScreen::new,
                "/screens/adv_pattern_provider.json");
        InitScreens.register(
                AAEMenus.SMALL_ADV_PATTERN_PROVIDER.get(),
                SmallAdvPatternProviderScreen::new,
                "/screens/small_adv_pattern_provider.json");
        InitScreens.register(
                AAEMenus.ADV_PATTERN_ENCODER.get(), AdvPatternEncoderScreen::new, "/screens/adv_pattern_encoder.json");
        InitScreens.register(
                AAEMenus.REACTION_CHAMBER.get(), ReactionChamberScreen::new, "/screens/reaction_chamber.json");
        InitScreens.register(
                AAEMenus.QUANTUM_CRAFTER.get(), QuantumCrafterScreen::new, "/screens/quantum_crafter.json");

        InitScreens.register(
                AAEMenus.STOCK_EXPORT_BUS.get(), StockExportBusScreen::new, "/screens/stock_export_bus.json");
        InitScreens.register(
                AAEMenus.IMPORT_EXPORT_BUS.get(), ImportExportBusScreen::new, "/screens/import_export_bus.json");

        InitScreens.register(
                AAEMenus.CRAFTER_PATTERN_CONFIG.get(),
                QuantumCrafterConfigPatternScreen::new,
                "/screens/quantum_crafter_pattern_config.json");

        InitScreens.register(
                AAEMenus.QUANTUM_ARMOR_CONFIG.get(),
                QuantumArmorConfigScreen::new,
                "/screens/quantum_armor_config.json");
        InitScreens.register(
                AAEMenus.QUANTUM_ARMOR_NUM_INPUT.get(),
                QuantumArmorNumInputConfigScreen::new,
                "/screens/quantum_armor_num_input_config.json");
        InitScreens.register(
                AAEMenus.QUANTUM_ARMOR_FILTER_CONFIG.get(),
                QuantumArmorFilterConfigScreen::new,
                "/screens/quantum_armor_filter_config.json");
        InitScreens.register(
                AAEMenus.QUANTUM_ARMOR_MAGNET.get(),
                QuantumArmorMagnetScreen::new,
                "/screens/quantum_armor_magnet.json");
        InitScreens.register(
                AAEMenus.QUANTUM_ARMOR_STYLE_CONFIG.get(),
                QuantumArmorStyleConfigScreen::new,
                "/screens/quantum_armor_style.json");
        InitScreens.register(
                AAEMenus.PORTABLE_WORKBENCH.get(), PortableWorkbenchScreen::new, "/screens/portable_workbench.json");
    }

    @SuppressWarnings("deprecation")
    private static void initItemBlockRenderTypes(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(AAEFluids.QUANTUM_INFUSION.source(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(AAEFluids.QUANTUM_INFUSION.flowing(), RenderType.translucent());
        });
    }

    private void registerHotkeys(RegisterKeyMappingsEvent e) {
        AAEHotkeys.INSTANCE.finalizeRegistration(e::register);
    }

    @SuppressWarnings("deprecation")
    private static void initItemColours(RegisterColorHandlersEvent.Item event) {
        event.register(makeOpaque(new StaticItemColor(AEColor.TRANSPARENT)), AAEItems.THROUGHPUT_MONITOR.asItem());

        for (var bucket : AAEFluids.INSTANCE.getFluids()) {
            event.getItemColors().register(AAEFluids::getFluidColor, bucket.bucketItem());
        }
    }

    private static void initRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(AAEBlockEntities.REACTION_CHAMBER.get(), ReactionChamberTESR::new);
    }

    private static ItemColor makeOpaque(ItemColor itemColor) {
        return (stack, tintIndex) -> itemColor.getColor(stack, tintIndex) | 0xFF000000;
    }

    public static AAEClient instance() {
        return INSTANCE;
    }
}
