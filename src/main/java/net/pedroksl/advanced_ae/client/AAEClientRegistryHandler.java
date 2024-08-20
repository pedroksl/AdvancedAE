package net.pedroksl.advanced_ae.client;

import appeng.init.client.InitScreens;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderContainer;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderGui;
import net.pedroksl.advanced_ae.gui.advpatternprovider.SmallAdvPatternProviderContainer;
import net.pedroksl.advanced_ae.gui.advpatternprovider.SmallAdvPatternProviderGui;
import net.pedroksl.advanced_ae.gui.patternencoder.AdvPatternEncoderContainer;
import net.pedroksl.advanced_ae.gui.patternencoder.AdvPatternEncoderGui;

public class AAEClientRegistryHandler {
	public static final AAEClientRegistryHandler INSTANCE = new AAEClientRegistryHandler();

	@SubscribeEvent
	public void registerGui(RegisterMenuScreensEvent event) {
		InitScreens.register(event, AdvPatternProviderContainer.TYPE, AdvPatternProviderGui::new, "/screens/adv_pattern_provider.json");
		InitScreens.register(event, SmallAdvPatternProviderContainer.TYPE, SmallAdvPatternProviderGui::new, "/screens/small_adv_pattern_provider.json");
		InitScreens.register(event, AdvPatternEncoderContainer.TYPE, AdvPatternEncoderGui::new, "/screens/adv_pattern_encoder.json");
	}

	@SubscribeEvent
	public void registerColorHandler(RegisterColorHandlersEvent.Item event) {
	}

	@SubscribeEvent
	public void registerModels(ModelEvent.RegisterGeometryLoaders event) {

	}

	@SubscribeEvent
	public void registerHotKey(RegisterKeyMappingsEvent e) {
	}
}
