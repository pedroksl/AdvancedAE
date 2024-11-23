package net.pedroksl.advanced_ae.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.AAEItemAndBlock;
import net.pedroksl.advanced_ae.common.blocks.QuantumCrafterBlock;

import java.util.Objects;

public class AAEItemModelProvider extends ItemModelProvider {
	public AAEItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, AdvancedAE.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		simpleItem(Objects.requireNonNull(AAEItemAndBlock.ADV_PROCESSING_PATTERN.getRegistryName()));
		simpleItem(Objects.requireNonNull(AAEItemAndBlock.ADV_PATTERN_ENCODER.getRegistryName()));
		simpleItem(AAEItemAndBlock.SHATTERED_SINGULARITY);
	}

	private void simpleItem(Item item) {
		withExistingParent(item.toString(),
		new ResourceLocation("item/generated")).texture("layer0",
				new ResourceLocation(AdvancedAE.MOD_ID, "item/" + item));
	}

	private void simpleItem(ResourceLocation item) {
		withExistingParent(item.getPath(),
				new ResourceLocation("item/generated")).texture("layer0",
				new ResourceLocation(AdvancedAE.MOD_ID, "item/" + item.getPath()));
	}
}
