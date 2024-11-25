package net.pedroksl.advanced_ae.datagen;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pedroksl.advanced_ae.AdvancedAE;

@Mod.EventBusSubscriber(modid = AdvancedAE.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AAEDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var out = gen.getPackOutput();
        var fileHelper = event.getExistingFileHelper();
        var lookup = event.getLookupProvider();
        var languageProvider = new AAELanguageProvider(out);

        gen.addProvider(event.includeClient(), new AAEModelProvider(out, fileHelper));
        gen.addProvider(event.includeServer(), new AAERecipeProvider(out, lookup));
        gen.addProvider(event.includeServer(), new AAELootTableProvider(out));

        var blockTags = new AAETagProvider.AAEBlockTagProvider(out, lookup, fileHelper);
        var itemTags = new AAETagProvider.AAEItemTagProvider(out, lookup, blockTags.contentsGetter(), fileHelper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), itemTags);

        gen.addProvider(event.includeClient(), languageProvider);
    }
}
