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

        for (var material : AAEMaterials.INSTANCE.getMaterials()) {
            add(material.get(), material.englishName());
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

        config("quantum computer", "Quantum Computer");
        config("quantum armor", "Quantum Armor");
        config("Miscellaneous", "Miscellaneous");
        config("enableEffects", "Enable Effects");

        config("quantumComputerMaxSize", "Maximum Quantum Computer Size");
        config("quantumComputerAcceleratorThreads", "Quantum Computer Accelerator Threads");
        config("quantumComputerMaxMultiThreaders", "Maximum Multi-Threaders");
        config("quantumComputermaxDataEntanglers", "Maximum Data Entanglers");
        config("quantumComputerMultiThreaderMultiplication", "Multi-Threader Multiplication");
        config("quantumComputerDataEntanglerMultiplication", "Data Entangler Multiplication");

        config("quantumArmorMaxWalkSpeed", "Maximum Walk Speed");
        config("quantumArmorMaxSprintSpeed", "Maximum Sprint Speed");
        config("quantumArmorMaxStepHeight", "Maximum Step Height");
        config("quantumArmorMaxJumpHeight", "Maximum Jump Height");
        config("quantumArmorHpBufferHearts", "HP Buffer Hearts");
        config("quantumArmorMaxFlightSpeed", "Maximum Flight Speed");
        config("quantumArmorEvasionChance", "Evasion Chance");
        config("quantumArmorMaxMagnetRange", "Maximum Magnet Range");
        config("quantumArmorStrengthBoost", "Strength Boost");
        config("quantumArmorAttackSpeedBoost", "Attack Speed Boost");
        config("quantumArmorLuckBoost", "Luck Boost");
        config("quantumArmorMaxReachBoost", "Maximum Reach Boost");
        config("quantumArmorSwimSpeedBoost", "Swim Speed Boost");
        config("quantumArmorRegenerationPerTick", "Regeneration Per Tick");
        config("quantumArmorPercentageDamageAbsorption", "Percentage Damage Absorption");

        config("throughputMonitorCacheSize", "Throughput Monitor Cache Size");
    }

    private void config(String key, String englishName) {
        add(String.format("%s.configuration.%s", AdvancedAE.MOD_ID, key), englishName);
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
