package net.pedroksl.advanced_ae.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.pedroksl.advanced_ae.AdvancedAE;

import appeng.datagen.providers.models.AE2ModelProvider;

@EventBusSubscriber(modid = AdvancedAE.MOD_ID)
public class AAEDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        var generator = event.getGenerator();

        var localization = new AAELanguageProvider(generator.getPackOutput());
        var pack = generator.getVanillaPack(true);

        var lookup = event.getLookupProvider();

        pack.addProvider(packOutput -> new AAELootTableProvider(packOutput, lookup));
        pack.addProvider(AE2ModelProvider.create(AdvancedAE.MOD_ID, AAEModelProvider::new));

        pack.addProvider(packOutput -> new AAERecipeProvider.Runner(packOutput, lookup));

        var blockTags = pack.addProvider(packOutput -> new AAETagProvider.AAEBlockTagProvider(packOutput, lookup));
        pack.addProvider(
                packOutput -> new AAETagProvider.AAEItemTagProvider(packOutput, lookup, blockTags.contentsGetter()));
        pack.addProvider(
                packOutput -> new AAETagProvider.AAEDataComponentTypeTagProvider(packOutput, lookup, localization));

        pack.addProvider(_ -> localization);
    }
}
