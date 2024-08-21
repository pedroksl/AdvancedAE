package net.pedroksl.advanced_ae;

import com.mojang.logging.LogUtils;

import net.pedroksl.advanced_ae.common.definitions.AAEBlockEntities;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import org.slf4j.Logger;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.pedroksl.advanced_ae.client.AAEClientRegistryHandler;
import net.pedroksl.advanced_ae.common.AAERegistryHandler;
import net.pedroksl.advanced_ae.common.AAESingletons;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(AdvancedAE.MOD_ID)
public class AdvancedAE {
	public static final String MOD_ID = "advanced_ae";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static AdvancedAE INSTANCE;

	public AdvancedAE(IEventBus eventBus, ModContainer container) {
		assert INSTANCE == null;
		INSTANCE = this;

		if (!container.getModId().equals(MOD_ID)) {
			throw new IllegalArgumentException("Invalid ID: " + MOD_ID);
		}

		AAEItems.DR.register(eventBus);
		AAEBlocks.DR.register(eventBus);
		AAEBlockEntities.DR.register(eventBus);

		container.registerConfig(ModConfig.Type.COMMON, AAEConfig.SPEC);

		eventBus.addListener((RegisterEvent e) -> {
			if (e.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
				AAERegistryHandler.INSTANCE.registerTab(e.getRegistry(Registries.CREATIVE_MODE_TAB));
				return;
			}
			if (e.getRegistryKey().equals(Registries.BLOCK)) {
				AAESingletons.init(AAERegistryHandler.INSTANCE);
				AAERegistryHandler.INSTANCE.runRegister();
			}
		});
		if (FMLEnvironment.dist.isClient()) {
			eventBus.register(AAEClientRegistryHandler.INSTANCE);
		}
		eventBus.addListener(this::commonSetup);
		eventBus.addListener(this::clientSetup);
		eventBus.addListener(AAENetworkHandler.INSTANCE::onRegister);
		eventBus.register(AAERegistryHandler.INSTANCE);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		AAERegistryHandler.INSTANCE.onInit();
	}

	public void clientSetup(FMLClientSetupEvent event) {
	}

	public static ResourceLocation id(String id) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
	}
}
