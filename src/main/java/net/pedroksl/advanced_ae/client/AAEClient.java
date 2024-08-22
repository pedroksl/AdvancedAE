package net.pedroksl.advanced_ae.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderGui;
import net.pedroksl.advanced_ae.gui.advpatternprovider.SmallAdvPatternProviderGui;
import net.pedroksl.advanced_ae.gui.patternencoder.AdvPatternEncoderGui;

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
                event, AAEMenus.ADV_PATTERN_PROVIDER, AdvPatternProviderGui::new, "/screens/adv_pattern_provider.json");
        InitScreens.register(
                event,
                AAEMenus.SMALL_ADV_PATTERN_PROVIDER,
                SmallAdvPatternProviderGui::new,
                "/screens/small_adv_pattern_provider.json");
        InitScreens.register(
                event, AAEMenus.ADV_PATTERN_ENCODER, AdvPatternEncoderGui::new, "/screens/adv_pattern_encoder.json");
    }

    @SuppressWarnings("deprecation")
    private static void initCraftingUnitModels(FMLClientSetupEvent event) {
        //		event.enqueueWork(() -> {
        //			for (var type : AAECraftingUnitType.values()) {
        //				BuiltInModelHooks.addBuiltInModel(
        //						AdvancedAE.makeId("block/crafting/" + type.getAffix() + "_formed"),
        //						new CraftingCubeModel(new AAECraftingUnitModelProvider(type)));
        //
        //				ItemBlockRenderTypes.setRenderLayer(type.getDefinition().block(), RenderType.cutout());
        //			}
        //		});
    }

    private static void initItemColours(RegisterColorHandlersEvent.Item event) {}
}
