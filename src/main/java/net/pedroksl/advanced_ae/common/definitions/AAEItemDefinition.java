package net.pedroksl.advanced_ae.common.definitions;

import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.core.definitions.ItemDefinition;
import appeng.util.helpers.ItemComparisonHelper;

public class AAEItemDefinition<T extends Item> implements ItemLike, Supplier<T> {

    private final String englishName;
    private final RegistryObject<T> item;

    public AAEItemDefinition(String englishName, RegistryObject<T> item) {
        this.englishName = englishName;
        this.item = item;
    }

    public String getEnglishName() {
        return englishName;
    }

    public ResourceLocation id() {
        return this.item.getId();
    }

    public ItemStack stack() {
        return stack(1);
    }

    public ItemStack stack(int stackSize) {
        return new ItemStack(item.get(), stackSize);
    }

    public GenericStack genericStack(long stackSize) {
        return new GenericStack(AEItemKey.of(item.get()), stackSize);
    }

    public Holder<T> holder() {
        return item.getHolder().orElseThrow();
    }

    @Deprecated(forRemoval = true, since = "1.21")
    public final boolean isSameAs(ItemStack comparableStack) {
        return is(comparableStack);
    }

    public final boolean is(ItemStack comparableStack) {
        return ItemComparisonHelper.isEqualItemType(comparableStack, this.stack());
    }

    public final boolean is(AEKey key) {
        if (key instanceof AEItemKey itemKey) {
            return asItem() == itemKey.getItem();
        }
        return false;
    }

    public final boolean isSameAs(AEKey key) {
        return is(key);
    }

    @Override
    public T get() {
        return item.get();
    }

    @Override
    public T asItem() {
        return item.get();
    }

    public ItemDefinition<T> getItemDefinition() {
        return new ItemDefinition<>(this.englishName, id(), asItem());
    }
}
