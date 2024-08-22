package net.pedroksl.advanced_ae;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.pedroksl.advanced_ae.common.definitions.*;
import net.pedroksl.advanced_ae.common.parts.AdvPatternProviderPart;
import net.pedroksl.advanced_ae.common.parts.SmallAdvPatternProviderPart;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.xmod.appflux.AFCommonLoad;

import appeng.api.AECapabilities;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.parts.RegisterPartCapabilitiesEvent;
import appeng.api.parts.RegisterPartCapabilitiesEventInternal;
import appeng.core.definitions.AEBlockEntities;

@Mod(AdvancedAE.MOD_ID)
public class AdvancedAE {
    public static final String MOD_ID = "advanced_ae";

    public AdvancedAE(IEventBus eventBus, ModContainer container) {
        AAEBlocks.DR.register(eventBus);
        AAEItems.DR.register(eventBus);
        AAEBlockEntities.DR.register(eventBus);
        AAEMenus.DR.register(eventBus);
        AAEComponents.DR.register(eventBus);
        AAECreativeTab.DR.register(eventBus);

        eventBus.addListener(AdvancedAE::initUpgrades);
        eventBus.addListener(AdvancedAE::initCapabilities);

        container.registerConfig(ModConfig.Type.COMMON, AAEConfig.SPEC);

        eventBus.addListener(AAENetworkHandler.INSTANCE::onRegister);
        // eventBus.register(AAERegistryHandler.INSTANCE);
    }

    private static void initUpgrades(RegisterCapabilitiesEvent event) {
        if (ModList.get().isLoaded("appflux")) {
            AFCommonLoad.init();
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void initCapabilities(RegisterCapabilitiesEvent event) {
        for (var type : AAEBlockEntities.DR.getEntries()) {
            event.registerBlockEntity(
                    AECapabilities.IN_WORLD_GRID_NODE_HOST, type.get(), (be, context) -> (IInWorldGridNodeHost) be);
        }

        event.registerBlockEntity(
                AECapabilities.GENERIC_INTERNAL_INV,
                AAEBlockEntities.ADV_PATTERN_PROVIDER.get(),
                (be, context) -> be.getLogic().getReturnInv());
        event.registerBlockEntity(
                AECapabilities.GENERIC_INTERNAL_INV,
                AAEBlockEntities.SMALL_ADV_PATTERN_PROVIDER.get(),
                (be, context) -> be.getLogic().getReturnInv());

        var partEvent = new RegisterPartCapabilitiesEvent();
        partEvent.addHostType(AEBlockEntities.CABLE_BUS.get());
        partEvent.register(
                AECapabilities.GENERIC_INTERNAL_INV,
                (part, context) -> part.getLogic().getReturnInv(),
                AdvPatternProviderPart.class);
        partEvent.register(
                AECapabilities.GENERIC_INTERNAL_INV,
                (part, context) -> part.getLogic().getReturnInv(),
                SmallAdvPatternProviderPart.class);
        ModLoader.postEvent(partEvent);
        RegisterPartCapabilitiesEventInternal.register(partEvent, event);
    }

    public static ResourceLocation makeId(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }
}
