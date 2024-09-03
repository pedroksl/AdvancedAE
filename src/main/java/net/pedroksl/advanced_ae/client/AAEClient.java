package net.pedroksl.advanced_ae.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.client.gui.AdvPatternEncoderScreen;
import net.pedroksl.advanced_ae.client.gui.AdvPatternProviderScreen;
import net.pedroksl.advanced_ae.client.gui.ReactionChamberScreen;
import net.pedroksl.advanced_ae.client.gui.SmallAdvPatternProviderScreen;
import net.pedroksl.advanced_ae.client.renderer.AAECraftingUnitModelProvider;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
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
    }

    private static void initScreens(RegisterMenuScreensEvent event) {
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
}
