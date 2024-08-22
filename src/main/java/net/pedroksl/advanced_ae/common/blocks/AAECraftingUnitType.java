package net.pedroksl.advanced_ae.common.blocks;

import net.minecraft.world.item.Item;

import appeng.block.crafting.ICraftingUnitType;
import appeng.core.definitions.BlockDefinition;

public enum AAECraftingUnitType implements ICraftingUnitType {
	UNIT(0, "unit"),
	ACCELERATOR(0, "accelerator");

	private final int storageMb;
	private final String affix;

	AAECraftingUnitType(int storageMb, String affix) {
		this.storageMb = storageMb;
		this.affix = affix;
	}

	@Override
	public long getStorageBytes() {
		return 1024L * 1024 * storageMb;
	}

	@Override
	public int getAcceleratorThreads() {
		return this == ACCELERATOR ? 4 : 0;
	}

	public String getAffix() {
		return this.affix;
	}

	public BlockDefinition<?> getDefinition() {
		//        return switch (this) {
		//            case UNIT -> AAEBlocks.ADV_CRAFTING_UNIT;
		//            case ACCELERATOR -> AAEBlocks.ADV_CRAFTING_ACCELERATOR;
		//        };
		return null;
	}

	@Override
	public Item getItemFromType() {
		return getDefinition().asItem();
	}
}
