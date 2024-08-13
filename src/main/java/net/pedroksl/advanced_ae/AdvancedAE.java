package net.pedroksl.advanced_ae;

import appeng.api.crafting.PatternDetailsHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.pedroksl.advanced_ae.client.AAEClientRegistryHandler;
import net.pedroksl.advanced_ae.common.AAEItemAndBlock;
import net.pedroksl.advanced_ae.common.AAERegistryHandler;
import net.minecraftforge.registries.RegisterEvent;
import net.pedroksl.advanced_ae.common.patterns.AdvPatternDetailsDecoder;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AdvancedAE.MOD_ID)
public class AdvancedAE {
	public static final String MOD_ID = "advanced_ae";

	public AdvancedAE() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AAEConfig.SPEC);
		AAEItemAndBlock.init(AAERegistryHandler.INSTANCE);
		modEventBus.register(AAERegistryHandler.INSTANCE);
		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::clientSetup);
		MinecraftForge.EVENT_BUS.register(this);
		modEventBus.addListener((RegisterEvent e) -> {
			if (e.getRegistryKey() == Registries.CREATIVE_MODE_TAB) {
				AAERegistryHandler.INSTANCE.registerTab(e.getVanillaRegistry());
			}
		});
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		AAERegistryHandler.INSTANCE.onInit();
		AAENetworkHandler.INSTANCE.init();
		PatternDetailsHelper.registerDecoder(AdvPatternDetailsDecoder.INSTANCE);
	}

	public void clientSetup(FMLClientSetupEvent event) {
		AAEClientRegistryHandler.INSTANCE.init();
	}

	public static ResourceLocation id(String id) {
		return new ResourceLocation(MOD_ID, id);
	}
}
