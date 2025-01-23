package net.pedroksl.advanced_ae;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.pedroksl.advanced_ae.common.definitions.*;
import net.pedroksl.advanced_ae.common.items.armors.IGridLinkedItem;
import net.pedroksl.advanced_ae.common.patterns.AdvPatternDetailsDecoder;
import net.pedroksl.advanced_ae.events.AAELivingEntityEvents;
import net.pedroksl.advanced_ae.events.AAEPlayerEvents;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.recipes.InitRecipeSerializers;
import net.pedroksl.advanced_ae.recipes.InitRecipeTypes;
import net.pedroksl.advanced_ae.xmod.Addons;
import net.pedroksl.advanced_ae.xmod.appflux.AppliedFluxPlugin;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.features.GridLinkables;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;

public class AdvancedAE {
    public static final String MOD_ID = "advanced_ae";

    static AdvancedAE INSTANCE;

    public AdvancedAE() {
        if (INSTANCE != null) {
            throw new IllegalStateException();
        }
        INSTANCE = this;

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        AAEConfig.register(MOD_ID);

        AAEBlocks.DR.register(eventBus);
        AAEItems.DR.register(eventBus);
        AAEBlockEntities.DR.register(eventBus);
        AAEFluids.init(eventBus);
        AAEMenus.DR.register(eventBus);
        AAECreativeTab.DR.register(eventBus);

        eventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        eventBus.addListener(AdvancedAE::initUpgrades);
        eventBus.addListener((RegisterEvent event) -> {
            if (event.getRegistryKey() == Registries.RECIPE_TYPE) {
                InitRecipeTypes.init(ForgeRegistries.RECIPE_TYPES);
            } else if (event.getRegistryKey() == Registries.RECIPE_SERIALIZER) {
                InitRecipeSerializers.init(ForgeRegistries.RECIPE_SERIALIZERS);
            }
        });

        AAEHotkeys.init();
        AAENbt.init();
    }

    public static AdvancedAE instance() {
        return INSTANCE;
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        AAENetworkHandler.INSTANCE.init();
        PatternDetailsHelper.registerDecoder(AdvPatternDetailsDecoder.INSTANCE);
        initGridLinkables();
        AAEPlayerEvents.init();
        AAELivingEntityEvents.init();
    }

    public static void initGridLinkables() {
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
            Upgrades.add(AEItems.SPEED_CARD, AAEItems.IMPORT_EXPORT_BUS, 4);
            Upgrades.add(AEItems.CAPACITY_CARD, AAEItems.IMPORT_EXPORT_BUS, 5);
            Upgrades.add(AEItems.REDSTONE_CARD, AAEItems.IMPORT_EXPORT_BUS, 1);
            Upgrades.add(AEItems.CRAFTING_CARD, AAEItems.IMPORT_EXPORT_BUS, 1);
            Upgrades.add(AEItems.FUZZY_CARD, AAEItems.IMPORT_EXPORT_BUS, 1);

            if (Addons.APPFLUX.isLoaded()) {
                AppliedFluxPlugin.init();
            }
        });
    }

    public void registerHotkey(String id) {}

    public static ResourceLocation makeId(String id) {
        return new ResourceLocation(MOD_ID, id);
    }
}
