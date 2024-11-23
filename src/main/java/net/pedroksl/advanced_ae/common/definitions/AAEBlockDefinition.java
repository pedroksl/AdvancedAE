package net.pedroksl.advanced_ae.common.definitions;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class AAEBlockDefinition<T extends Block> extends BlockDefinition<T> implements ItemLike {

	private RegistryObject<T> block;
	private AAEItemDefinition<BlockItem> item;

	public AAEBlockDefinition(String englishName, RegistryObject<T> block, AAEItemDefinition<BlockItem> item) {
		super(englishName, block.getId(), block.get(), item.get());
	}

	@Override
	public ResourceLocation id() {
		return block.getId();
	}

	public ItemStack stack() {
		return item.stack();
	}

	public GenericStack genericStack(long stackSize) {
		return item.genericStack(stackSize);
	}

	public boolean is(ItemStack comparableStack) {
		return item.is(comparableStack);
	}

	public boolean is(AEKey key) {
		return item.is(key);
	}

	public ItemDefinition<BlockItem> item() {
		return item;
	}

	@Override
	public @NotNull BlockItem asItem() {
		return item.asItem();
	}
}
