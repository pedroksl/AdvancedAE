package net.pedroksl.advanced_ae.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.pedroksl.advanced_ae.AdvancedAE;

@EventBusSubscriber(modid = AdvancedAE.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class AAEDataGen {

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent dataEvent) {
		var pack = dataEvent.getGenerator().getVanillaPack(true);
		var fileHelper = dataEvent.getExistingFileHelper();
		var lookup = dataEvent.getLookupProvider();
		pack.addProvider(p -> new AAEBlockStateProvider(p, fileHelper));
		var blockTagsProvider = pack.addProvider(p -> new AAEBlockTagProvider(p, lookup, fileHelper));
		pack.addProvider(p -> new AAEItemModelProvider(p, fileHelper));
		pack.addProvider(p -> new AAEItemTagProvider(p, lookup, blockTagsProvider.contentsGetter(), fileHelper));
		pack.addProvider(p -> new AAELootTableProvider(p, lookup));
		pack.addProvider(p -> new AAERecipeProvider(p, lookup));
	}
}
