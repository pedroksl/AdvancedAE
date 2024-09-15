package net.pedroksl.advanced_ae.common.definitions;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.pedroksl.advanced_ae.AdvancedAE;

public class AAEConfig {

    private final AAEConfig.ClientConfig client = new AAEConfig.ClientConfig();
    private final AAEConfig.CommonConfig common = new AAEConfig.CommonConfig();

    private static AAEConfig INSTANCE;

    AAEConfig(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, client.spec);
        container.registerConfig(ModConfig.Type.COMMON, common.spec);
    }

    public int getQuantumComputerMaxSize() {
        return common.quantumComputerMaxSize.get();
    }

    public int getQuantumComputerAcceleratorThreads() {
        return common.quantumComputerAcceleratorThreads.get();
    }

    public int getQuantumComputerMaxMultiThreaders() {
        return common.quantumComputerMaxMultiThreaders.get();
    }

    public int getQuantumComputermaxDataEntanglers() {
        return common.quantumComputerMaxDataEntanglers.get();
    }

    public int getQuantumComputerMultiThreaderMultiplication() {
        return common.quantumComputerMultiThreaderMultiplication.get();
    }

    public int getQuantumComputerDataEntanglerMultiplication() {
        return common.quantumComputerDataEntanglerMultiplication.get();
    }

    public void save() {
        common.spec.save();
        client.spec.save();
    }

    public static void register(ModContainer container) {
        if (!container.getModId().equals(AdvancedAE.MOD_ID)) {
            throw new IllegalArgumentException();
        }
        INSTANCE = new AAEConfig(container);
    }

    public static AAEConfig instance() {
        return INSTANCE;
    }

    private static class ClientConfig {
        private final ModConfigSpec spec;

        public ClientConfig() {
            var builder = new ModConfigSpec.Builder();

            this.spec = builder.build();
        }
    }

    private static class CommonConfig {
        private final ModConfigSpec spec;

        public final ModConfigSpec.IntValue quantumComputerMaxSize;
        public final ModConfigSpec.IntValue quantumComputerAcceleratorThreads;
        public final ModConfigSpec.IntValue quantumComputerMaxMultiThreaders;
        public final ModConfigSpec.IntValue quantumComputerMaxDataEntanglers;
        public final ModConfigSpec.IntValue quantumComputerMultiThreaderMultiplication;
        public final ModConfigSpec.IntValue quantumComputerDataEntanglerMultiplication;

        public CommonConfig() {
            var builder = new ModConfigSpec.Builder();

            builder.push("quantum computer");
            quantumComputerMaxSize = define(
                    builder,
                    "quantumComputerMaxSize",
                    5,
                    5,
                    12,
                    "Define the maximum dimensions of the Quantum Computer Multiblock.");
            quantumComputerAcceleratorThreads = define(
                    builder,
                    "quantumComputerAcceleratorThreads",
                    8,
                    4,
                    16,
                    "Define the maximum amount of multi threaders per Quantum Computer Multiblock.");
            quantumComputerMaxMultiThreaders = define(
                    builder,
                    "quantumComputerMaxMultiThreaders",
                    1,
                    1,
                    2,
                    "Define the maximum amount of multi threaders per Quantum Computer Multiblock.");
            quantumComputerMaxDataEntanglers = define(
                    builder,
                    "quantumComputermaxDataEntanglers",
                    1,
                    1,
                    2,
                    "Define the maximum amount of Data Entanglers per Quantum Computer Multiblock.");
            quantumComputerMultiThreaderMultiplication = define(
                    builder,
                    "quantumComputerMultiThreaderMultiplication",
                    4,
                    2,
                    8,
                    "Define the multiplication factor of the multi threaders.");
            quantumComputerDataEntanglerMultiplication = define(
                    builder,
                    "quantumComputerDataEntanglerMultiplication",
                    4,
                    2,
                    8,
                    "Define the multiplication factor of the data entanglers.");
            builder.pop();

            this.spec = builder.build();
        }

        private static ModConfigSpec.IntValue define(
                ModConfigSpec.Builder builder, String name, int defaultValue, String comment) {
            builder.comment(comment);
            return define(builder, name, defaultValue);
        }

        private static ModConfigSpec.IntValue define(
                ModConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment) {
            builder.comment(comment);
            return define(builder, name, defaultValue, min, max);
        }

        private static ModConfigSpec.IntValue define(
                ModConfigSpec.Builder builder, String name, int defaultValue, int min, int max) {
            return builder.defineInRange(name, defaultValue, min, max);
        }

        private static ModConfigSpec.IntValue define(ModConfigSpec.Builder builder, String name, int defaultValue) {
            return define(builder, name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
    }
}
