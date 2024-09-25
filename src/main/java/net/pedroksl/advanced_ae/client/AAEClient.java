package net.pedroksl.advanced_ae.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.client.gui.*;
import net.pedroksl.advanced_ae.client.gui.OutputDirectionScreen;
import net.pedroksl.advanced_ae.client.renderer.AAECraftingUnitModelProvider;
import net.pedroksl.advanced_ae.client.renderer.ReactionChamberTESR;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
import net.pedroksl.advanced_ae.common.definitions.AAEBlockEntities;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;

import appeng.client.render.crafting.CraftingCubeModel;
import appeng.hooks.BuiltInModelHooks;
import appeng.init.client.InitScreens;

@SuppressWarnings("unused")
@Mod(value = AdvancedAE.MOD_ID, dist = Dist.CLIENT)
public class AAEClient {
    public AAEClient(IEventBus eventBus) {
        eventBus.addListener(AAEClient::initScreens);
        eventBus.addListener(AAEClient::initCraftingUnitModels);
        eventBus.addListener(AAEClient::initItemColours);
        eventBus.addListener(AAEClient::initRenderers);
    }

    private static void initScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(
                event, AAEMenus.QUANTUM_COMPUTER, QuantumComputerScreen::new, "/screens/quantum_computer.json");

        InitScreens.register(
                event,
                AAEMenus.ADV_PATTERN_PROVIDER,
                AdvPatternProviderScreen::new,
                "/screens/adv_pattern_provider.json");
        InitScreens.register(
                event,
                AAEMenus.SMALL_ADV_PATTERN_PROVIDER,
                SmallAdvPatternProviderScreen::new,
                "/screens/small_adv_pattern_provider.json");
        InitScreens.register(
                event, AAEMenus.ADV_PATTERN_ENCODER, AdvPatternEncoderScreen::new, "/screens/adv_pattern_encoder.json");
        InitScreens.register(
                event, AAEMenus.REACTION_CHAMBER, ReactionChamberScreen::new, "/screens/reaction_chamber.json");
        InitScreens.register(
                event, AAEMenus.QUANTUM_CRAFTER, QuantumCrafterScreen::new, "/screens/quantum_crafter.json");

        InitScreens.register(
                event, AAEMenus.OUTPUT_DIRECTION, OutputDirectionScreen::new, "/screens/output_direction.json");
        InitScreens.register(
                event,
                AAEMenus.CRAFTER_PATTERN_CONFIG,
                QuantumCrafterConfigPatternScreen::new,
                "/screens/quantum_crafter_pattern_config.json");
    }

    @SuppressWarnings("deprecation")
    private static void initCraftingUnitModels(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            var type = AAECraftingUnitType.STRUCTURE;
            BuiltInModelHooks.addBuiltInModel(
                    AdvancedAE.makeId("block/crafting/" + type.getAffix() + "_formed"),
                    new CraftingCubeModel(new AAECraftingUnitModelProvider(type)));

            ItemBlockRenderTypes.setRenderLayer(type.getDefinition().block(), RenderType.cutout());
        });
    }

    private static void initItemColours(RegisterColorHandlersEvent.Item event) {}

    private static void initRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(AAEBlockEntities.REACTION_CHAMBER.get(), ReactionChamberTESR::new);
    }
}
