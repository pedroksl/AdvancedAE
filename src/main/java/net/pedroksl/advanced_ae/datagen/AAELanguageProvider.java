package net.pedroksl.advanced_ae.datagen;

import org.jetbrains.annotations.NotNull;

import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.*;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

public class AAELanguageProvider extends LanguageProvider {
    public AAELanguageProvider(PackOutput output) {
        super(output, AdvancedAE.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        for (var item : AAEItems.INSTANCE.getItems()) {
            add(item.asItem(), item.getEnglishName());
        }

        for (var block : AAEBlocks.INSTANCE.getBlocks()) {
            add(block.block(), block.getEnglishName());
        }

        for (var fluid : AAEFluids.INSTANCE.getFluids()) {
            add(fluid.fluidType(), fluid.englishName());
            add(fluid.flowing(), fluid.englishName());
            add(fluid.source(), fluid.englishName());
            add(fluid.block(), fluid.englishName());
            add(fluid.bucketItem(), fluid.bucketItemId().getEnglishName());
        }

        for (var translation : AAEText.values()) {
            add(translation.getTranslationKey(), translation.getEnglishText());
        }

        for (var key : AAEHotkeysRegistry.Keys.values()) {
            add("key." + AdvancedAE.MOD_ID + "." + key.getId(), key.getEnglishTranslation());
        }

        for (var upgrade : UpgradeType.values()) {
            add(upgrade.getTranslationKey(), upgrade.name);
        }

        generateLocalizations();
    }

    private void generateLocalizations() {
        add("key." + AdvancedAE.MOD_ID + ".category", "Advanced AE");
        add("key.ae2.wireless_quantum_crafter_terminal", "Open Wireless Quantum Crafter Terminal");
        add("curios.identifier.adv_pattern_encoder", "Pattern Encoder");
    }

    public void add(FluidType key, String englishName) {
        add(
                Util.makeDescriptionId(
                        "fluid_type", ForgeRegistries.FLUID_TYPES.get().getKey(key)),
                englishName);
    }

    @SuppressWarnings("deprecation")
    public void add(Fluid key, String englishName) {
        add(Util.makeDescriptionId("fluid", BuiltInRegistries.FLUID.getKey(key)), englishName);
    }

    @NotNull
    @Override
    public String getName() {
        return "Language";
    }
}
