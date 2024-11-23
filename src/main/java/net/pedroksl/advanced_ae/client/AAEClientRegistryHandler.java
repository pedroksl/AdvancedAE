package net.pedroksl.advanced_ae.client;

import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.init.client.InitScreens;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.pedroksl.advanced_ae.common.AAEItemAndBlock;
import net.pedroksl.advanced_ae.gui.OutputDirectionMenu;
import net.pedroksl.advanced_ae.gui.QuantumCrafterConfigPatternMenu;
import net.pedroksl.advanced_ae.gui.QuantumCrafterMenu;
import net.pedroksl.advanced_ae.gui.SetAmountMenu;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderContainer;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderGui;
import net.pedroksl.advanced_ae.gui.patternencoder.AdvPatternEncoderContainer;
import net.pedroksl.advanced_ae.gui.patternencoder.AdvPatternEncoderGui;
import net.pedroksl.advanced_ae.gui.quantumcomputer.QuantumComputerMenu;

public class AAEClientRegistryHandler {
	public static final net.pedroksl.advanced_ae.client.AAEClientRegistryHandler INSTANCE = new net.pedroksl.advanced_ae.client.AAEClientRegistryHandler();

	public void init() {
		this.registerGui();
	}

	public void registerGui() {
		InitScreens.register(AdvPatternProviderContainer.TYPE, AdvPatternProviderGui::new, "/screens/adv_pattern_provider.json");
		InitScreens.register(AdvPatternEncoderContainer.TYPE, AdvPatternEncoderGui::new, "/screens" +
				"/adv_pattern_encoder.json");
		InitScreens.register(QuantumComputerMenu.TYPE, QuantumComputerScreen::new, "/screens/quantum_computer.json");
		InitScreens.register(QuantumCrafterMenu.TYPE, QuantumCrafterScreen::new, "/screens/quantum_crafter.json");
		InitScreens.register(QuantumCrafterConfigPatternMenu.TYPE, QuantumCrafterConfigPatternScreen::new, "/screens/quantum_crafter_pattern_config.json");
		InitScreens.register(SetAmountMenu.TYPE, SetAmountScreen::new, "/screens/aae_set_amount.json");
		InitScreens.register(OutputDirectionMenu.TYPE, OutputDirectionScreen::new, "/screens/output_direction.json");
	}

	@SubscribeEvent
	@SuppressWarnings("deprecation")
	public void registerColorHandler(RegisterColorHandlersEvent.Item event) {
		var color = event.getItemColors();
		color.register(new StaticItemColor(AEColor.TRANSPARENT), AAEItemAndBlock.ADV_PATTERN_PROVIDER);
	}
}
