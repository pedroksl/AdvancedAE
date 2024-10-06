package net.pedroksl.advanced_ae.common.definitions;

import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MaterialDefinition<T extends ArmorMaterial> implements Supplier<T> {
    private final String englishName;
    private final DeferredHolder<ArmorMaterial, T> material;

    public MaterialDefinition(String englishName, DeferredHolder<ArmorMaterial, T> material) {
        this.englishName = englishName;
        this.material = material;
    }

    public String getEnglishName() {
        return this.englishName;
    }

    public ResourceLocation id() {
        return this.material.getId();
    }

    public Holder<ArmorMaterial> holder() {
        return this.material;
    }

    public T get() {
        return this.material.get();
    }

    public T asItem() {
        return this.material.get();
    }
}
