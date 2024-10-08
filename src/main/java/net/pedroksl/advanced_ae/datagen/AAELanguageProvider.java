package net.pedroksl.advanced_ae.datagen;

import org.jetbrains.annotations.NotNull;

import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;
import net.pedroksl.advanced_ae.common.definitions.AAEText;

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

    public void add(ArmorMaterial key, String engishName) {
        add(Util.makeDescriptionId("material", BuiltInRegistries.ARMOR_MATERIAL.getKey(key)), engishName);
    }

    @NotNull
    @Override
    public String getName() {
        return "Language";
    }
}
