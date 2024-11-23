package net.pedroksl.advanced_ae.common.definitions;

import appeng.api.stacks.AEKey;
import appeng.core.definitions.ItemDefinition;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class AAEItemDefinition<T extends Item> extends ItemDefinition<T> implements ItemLike, Supplier<T> {

	private RegistryObject<T> item;

	public AAEItemDefinition(String englishName, RegistryObject<T> item) {
		super(englishName, item.getId(), item.get());
	}

	public Holder<T> holder() {
		return item.getHolder().orElseThrow();
	}

	public final boolean is(ItemStack comparableStack) {
		return isSameAs(comparableStack);
	}

	public final boolean is(AEKey key) {
		return this.isSameAs(key);
	}

	@Override
	public T get() {
		return item.get();
	}
}
