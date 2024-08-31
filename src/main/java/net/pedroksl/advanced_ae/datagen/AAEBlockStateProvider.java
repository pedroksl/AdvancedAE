package net.pedroksl.advanced_ae.datagen;

import appeng.datagen.providers.models.AE2BlockStateProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.pedroksl.advanced_ae.AdvancedAE;

public class AAEBlockStateProvider extends AE2BlockStateProvider {
	public AAEBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, AdvancedAE.MOD_ID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
	}
}
