package net.pedroksl.advanced_ae.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.AAEItemAndBlock;

import java.util.Objects;

public class AAEItemModelProvider extends ItemModelProvider {
	public AAEItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, AdvancedAE.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		simpleItem(Objects.requireNonNull(AAEItemAndBlock.ADV_BLANK_PATTERN.getRegistryName()));
		simpleItem(Objects.requireNonNull(AAEItemAndBlock.ADV_CRAFTING_PATTERN.getRegistryName()));
		simpleItem(Objects.requireNonNull(AAEItemAndBlock.ADV_PROCESSING_PATTERN.getRegistryName()));
		simpleItem(Objects.requireNonNull(AAEItemAndBlock.ADV_SMITHING_PATTERN.getRegistryName()));
		simpleItem(Objects.requireNonNull(AAEItemAndBlock.ADV_STONECUTTING_PATTERN.getRegistryName()));
	}

	private void simpleItem(ResourceLocation item) {
		withExistingParent(item.getPath(),
				new ResourceLocation("item/generated")).texture("layer0",
				new ResourceLocation(AdvancedAE.MOD_ID, "item/" + item.getPath()));
	}
}
