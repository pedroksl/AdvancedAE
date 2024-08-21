package net.pedroksl.advanced_ae.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.AAESingletons;

public class AAEItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider {
    public AAEItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AdvancedAE.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        flatSingleLayer("adv_processing_pattern");
        flatSingleLayer(AAESingletons.ADV_PATTERN_ENCODER.getRegistryName().getPath());
        flatSingleLayer("adv_pattern_provider_upgrade");
        flatSingleLayer("adv_pattern_provider_capacity_upgrade");
    }

    private void flatSingleLayer(String item) {
        var id = AdvancedAE.id(item);
        singleTexture(id.getPath(), mcLoc("item/generated"), "layer0", AdvancedAE.id("item/" + item));
    }
}
