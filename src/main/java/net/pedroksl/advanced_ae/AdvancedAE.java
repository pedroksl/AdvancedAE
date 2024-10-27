package net.pedroksl.advanced_ae;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.pedroksl.advanced_ae.common.definitions.*;
import net.pedroksl.advanced_ae.common.items.armors.IGridLinkedItem;
import net.pedroksl.advanced_ae.common.parts.AdvPatternProviderPart;
import net.pedroksl.advanced_ae.common.parts.SmallAdvPatternProviderPart;
import net.pedroksl.advanced_ae.events.AAELivingEntityEvents;
import net.pedroksl.advanced_ae.events.AAEPlayerEvents;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.recipes.InitRecipeSerializers;
import net.pedroksl.advanced_ae.recipes.InitRecipeTypes;
import net.pedroksl.advanced_ae.xmod.Addons;
import net.pedroksl.advanced_ae.xmod.appflux.AppliedFluxApi;
import net.pedroksl.advanced_ae.xmod.mekansim.MekCap;

import appeng.api.AECapabilities;
import appeng.api.features.GridLinkables;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.parts.RegisterPartCapabilitiesEvent;
import appeng.api.parts.RegisterPartCapabilitiesEventInternal;
import appeng.api.upgrades.Upgrades;
import appeng.blockentity.AEBaseInvBlockEntity;
import appeng.blockentity.powersink.AEBasePoweredBlockEntity;
import appeng.core.AELog;
import appeng.core.definitions.AEBlockEntities;
import appeng.core.definitions.AEItems;
import appeng.items.tools.powered.powersink.PoweredItemCapabilities;

@Mod(value = AdvancedAE.MOD_ID, dist = Dist.DEDICATED_SERVER)
public class AdvancedAE {
    public static final String MOD_ID = "advanced_ae";

    static AdvancedAE INSTANCE;

    public AdvancedAE(IEventBus eventBus, ModContainer container) {
        if (INSTANCE != null) {
            throw new IllegalStateException();
        }
        INSTANCE = this;

        AAEConfig.register(container);

        AAEBlocks.DR.register(eventBus);
        AAEItems.DR.register(eventBus);
        AAEBlockEntities.DR.register(eventBus);
        AAEFluids.init(eventBus);
        AAEMenus.DR.register(eventBus);
        AAEComponents.DR.register(eventBus);
        AAEMaterials.DR.register(eventBus);
        AAECreativeTab.DR.register(eventBus);

        AAEComponents.init();

        eventBus.addListener(AdvancedAE::initUpgrades);
        eventBus.addListener(AdvancedAE::initCapabilities);

        eventBus.addListener(AAENetworkHandler.INSTANCE::onRegister);
        eventBus.addListener((RegisterEvent event) -> {
            if (event.getRegistryKey() == Registries.RECIPE_TYPE) {
                InitRecipeTypes.init(event.getRegistry(Registries.RECIPE_TYPE));
            } else if (event.getRegistryKey() == Registries.RECIPE_SERIALIZER) {
                InitRecipeSerializers.init(event.getRegistry(Registries.RECIPE_SERIALIZER));
            }
        });

        eventBus.addListener(this::commonSetup);
        AAEHotkeys.init();
    }

    public static AdvancedAE instance() {
        return INSTANCE;
    }

    public void registerHotkey(String id) {}

    private void commonSetup(FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.register(AAELivingEntityEvents.class);
        NeoForge.EVENT_BUS.register(AAEPlayerEvents.class);

        event.enqueueWork(this::postRegistrationInitialization).whenComplete((res, err) -> {
            if (err != null) {
                AELog.warn(err);
            }
        });
    }

    public void postRegistrationInitialization() {
        GridLinkables.register(AAEItems.QUANTUM_HELMET, IGridLinkedItem.LINKABLE_HANDLER);
        GridLinkables.register(AAEItems.QUANTUM_CHESTPLATE, IGridLinkedItem.LINKABLE_HANDLER);
        GridLinkables.register(AAEItems.QUANTUM_LEGGINGS, IGridLinkedItem.LINKABLE_HANDLER);
        GridLinkables.register(AAEItems.QUANTUM_BOOTS, IGridLinkedItem.LINKABLE_HANDLER);
    }

    private static void initUpgrades(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Upgrades.add(AEItems.SPEED_CARD, AAEBlocks.REACTION_CHAMBER, 4);
            Upgrades.add(AEItems.SPEED_CARD, AAEBlocks.QUANTUM_CRAFTER, 4);
            Upgrades.add(AEItems.REDSTONE_CARD, AAEBlocks.QUANTUM_CRAFTER, 1);
            Upgrades.add(AEItems.SPEED_CARD, AAEItems.STOCK_EXPORT_BUS, 4);
            Upgrades.add(AEItems.CAPACITY_CARD, AAEItems.STOCK_EXPORT_BUS, 5);
            Upgrades.add(AEItems.REDSTONE_CARD, AAEItems.STOCK_EXPORT_BUS, 1);
            Upgrades.add(AEItems.CRAFTING_CARD, AAEItems.STOCK_EXPORT_BUS, 1);

            if (Addons.APPFLUX.isLoaded()) {
                AppliedFluxApi.init();
            }
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void initCapabilities(RegisterCapabilitiesEvent event) {
        for (var type : AAEBlockEntities.DR.getEntries()) {
            event.registerBlockEntity(
                    AECapabilities.IN_WORLD_GRID_NODE_HOST, type.get(), (be, context) -> (IInWorldGridNodeHost) be);
        }
        for (var type : AAEItems.getItems()) {
            if (type.get() instanceof IAEItemPowerStorage powerStorage) {
                event.registerItem(
                        Capabilities.EnergyStorage.ITEM,
                        (object, context) -> new PoweredItemCapabilities(object, powerStorage),
                        type);
            }
        }

        event.registerBlockEntity(
                AECapabilities.GENERIC_INTERNAL_INV,
                AAEBlockEntities.ADV_PATTERN_PROVIDER.get(),
                (be, context) -> be.getLogic().getReturnInv());
        event.registerBlockEntity(
                AECapabilities.GENERIC_INTERNAL_INV,
                AAEBlockEntities.SMALL_ADV_PATTERN_PROVIDER.get(),
                (be, context) -> be.getLogic().getReturnInv());

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                AAEBlockEntities.REACTION_CHAMBER.get(),
                AEBaseInvBlockEntity::getExposedItemHandler);
        event.registerBlockEntity(
                AECapabilities.GENERIC_INTERNAL_INV,
                AAEBlockEntities.REACTION_CHAMBER.get(),
                (be, context) -> be.getTank());
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                AAEBlockEntities.REACTION_CHAMBER.get(),
                AEBasePoweredBlockEntity::getEnergyStorage);

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

        if (Addons.MEKANISM.isLoaded()) {
            MekCap.initCap(event);
        }
    }

    public static ResourceLocation makeId(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }
}
