package net.pedroksl.advanced_ae.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.AAESingletons;

import java.util.Objects;

public class AAEItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider {
	public AAEItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, AdvancedAE.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		flatSingleLayer(AdvancedAE.id("adv_processing_pattern"));
		flatSingleLayer(Objects.requireNonNull(AAESingletons.ADV_PATTERN_ENCODER.getRegistryName()));
	}

	private void flatSingleLayer(ResourceLocation item) {
		String id = item.getPath();
		singleTexture(
				id,
				mcLoc("item/generated"),
				"layer0",
				AdvancedAE.id(item.getPath()));
	}
}
