package net.pedroksl.advanced_ae.datagen;

import org.jetbrains.annotations.NotNull;

import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.*;

public class AAELanguageProvider extends LanguageProvider {
    public AAELanguageProvider(PackOutput output) {
        super(output, AdvancedAE.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        for (var item : AAEItems.getItems()) {
            add(item.asItem(), item.getEnglishName());
        }

        for (var block : AAEBlocks.getBlocks()) {
            add(block.block(), block.getEnglishName());
        }

        for (var fluid : AAEFluids.getFluids()) {
            add(fluid.fluidType(), fluid.getEnglishName());
            add(fluid.flowing(), fluid.getEnglishName());
            add(fluid.source(), fluid.getEnglishName());
            add(fluid.block(), fluid.getEnglishName());
            add(fluid.bucketItem(), fluid.bucketItemId().getEnglishName());
        }

        for (var translation : AAEText.values()) {
            add(translation.getTranslationKey(), translation.getEnglishText());
        }

        for (var material : AAEMaterials.getMaterials()) {
            add(material.get(), material.getEnglishName());
        }

        generateLocalizations();
    }

    private void generateLocalizations() {
        add("key.advanced_ae.category", "Advanced AE");
        add("key.advanced_ae.quantum_armor_config", "Open Quantum Armor Configuration");
    }

    public void add(FluidType key, String englishName) {
        add(Util.makeDescriptionId("fluid_type", NeoForgeRegistries.FLUID_TYPES.getKey(key)), englishName);
    }

    public void add(Fluid key, String englishName) {
        add(Util.makeDescriptionId("fluid", BuiltInRegistries.FLUID.getKey(key)), englishName);
    }

    public void add(ArmorMaterial key, String englishName) {
        add(Util.makeDescriptionId("material", BuiltInRegistries.ARMOR_MATERIAL.getKey(key)), englishName);
    }

    @NotNull
    @Override
    public String getName() {
        return "Language";
    }
}
