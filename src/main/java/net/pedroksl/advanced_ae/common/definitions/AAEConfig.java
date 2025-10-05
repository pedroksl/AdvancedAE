package net.pedroksl.advanced_ae.common.definitions;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.pedroksl.advanced_ae.AdvancedAE;

public class AAEConfig {

    private final AAEConfig.ClientConfig client = new AAEConfig.ClientConfig();
    private final AAEConfig.CommonConfig common = new AAEConfig.CommonConfig();

    private static AAEConfig INSTANCE;

    AAEConfig(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, client.spec);
        container.registerConfig(ModConfig.Type.COMMON, common.spec);
    }

    public boolean getEnableEffects() {
        return client.enableEffects.get();
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

    public int getMaxWalkSpeed() {
        return common.maxWalkSpeed.get();
    }

    public int getMaxSprintSpeed() {
        return common.maxSprintSpeed.get();
    }

    public int getMaxStepHeight() {
        return common.maxStepHeight.get();
    }

    public int getMaxJumpHeight() {
        return common.maxJumpHeight.get();
    }

    public int getmaxHpBuffer() {
        return common.hpBufferHearts.get();
    }

    public int getMaxFlightSpeed() {
        return common.maxFlightSpeed.get();
    }

    public int getEvasionChance() {
        return common.evasionChance.get();
    }

    public int getMaxMagnetRange() {
        return common.maxMagnetRange.get();
    }

    public int getStrengthBoost() {
        return common.strengthBoost.get();
    }

    public int getAttackSpeedBoost() {
        return common.attackSpeedBoost.get();
    }

    public int getLuckBoost() {
        return common.luckBoost.get();
    }

    public int getMaxReachBoost() {
        return common.maxReachBoost.get();
    }

    public int getMaxSwimSpeedBoost() {
        return common.swimSpeedBoost.get();
    }

    public int getRenegerationPerTick() {
        return common.regenerationPerTick.get();
    }

    public int getPercentageDamageAbsorption() {
        return common.percentageDamageAbsorption.get();
    }

    public int getThroughputMonitorCacheSize() {
        return common.throughputMonitorCacheSize.get();
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

        public final BooleanValue enableEffects;

        public ClientConfig() {
            var builder = new ModConfigSpec.Builder();

            builder.push("quantum computer");
            enableEffects = define(builder, "enableEffects", true);
            this.spec = builder.build();
        }
    }

    private static class CommonConfig {
        private final ModConfigSpec spec;

        public final IntValue quantumComputerMaxSize;
        public final IntValue quantumComputerAcceleratorThreads;
        public final IntValue quantumComputerMaxMultiThreaders;
        public final IntValue quantumComputerMaxDataEntanglers;
        public final IntValue quantumComputerMultiThreaderMultiplication;
        public final IntValue quantumComputerDataEntanglerMultiplication;

        public final IntValue maxWalkSpeed;
        public final IntValue maxSprintSpeed;
        public final IntValue maxStepHeight;
        public final IntValue maxJumpHeight;
        public final IntValue hpBufferHearts;
        public final IntValue maxFlightSpeed;
        public final IntValue evasionChance;
        public final IntValue maxMagnetRange;
        public final IntValue strengthBoost;
        public final IntValue attackSpeedBoost;
        public final IntValue luckBoost;
        public final IntValue maxReachBoost;
        public final IntValue swimSpeedBoost;
        public final IntValue regenerationPerTick;
        public final IntValue percentageDamageAbsorption;

        public final IntValue throughputMonitorCacheSize;

        public CommonConfig() {
            var builder = new ModConfigSpec.Builder();

            builder.push("quantum computer");
            quantumComputerMaxSize = define(
                    builder,
                    "quantumComputerMaxSize",
                    7,
                    5,
                    12,
                    "Define the maximum dimensions of the Quantum Computer Multiblock.");
            quantumComputerAcceleratorThreads = define(
                    builder,
                    "quantumComputerAcceleratorThreads",
                    8,
                    4,
                    16,
                    "Define the amount of Threads per Quantum Computer Accelerator.");
            quantumComputerMaxMultiThreaders = define(
                    builder,
                    "quantumComputerMaxMultiThreaders",
                    1,
                    1,
                    2,
                    "Define the maximum amount of Multi Threaders per Quantum Computer Multiblock.");
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

            builder.push("quantum armor");
            maxWalkSpeed = define(
                    builder,
                    "quantumArmorMaxWalkSpeed",
                    60,
                    10,
                    100,
                    "Define the maximum walk speed increase. Values are divided by 10 before use.");
            maxSprintSpeed = define(
                    builder,
                    "quantumArmorMaxSprintSpeed",
                    80,
                    10,
                    150,
                    "Define the maximum sprint speed increase. Values are divided by 10 before use.");
            maxStepHeight = define(
                    builder, "quantumArmorMaxStepHeight", 3, 1, 5, "Define the maximum increase in step height.");
            maxJumpHeight = define(
                    builder, "quantumArmorMaxJumpHeight", 3, 1, 5, "Define the maximum increase in jump height.");
            swimSpeedBoost = define(
                    builder,
                    "quantumArmorSwimSpeedBoost",
                    80,
                    10,
                    150,
                    "Define the maximum swim speed increase. Values are divided by 10 before use.");
            hpBufferHearts = define(
                    builder, "quantumArmorHpBuffer", 20, 5, 50, "Define the HP increased of the HP Buffer card.");
            maxFlightSpeed = define(
                    builder,
                    "quantumArmorMaxFlightSpeed",
                    10,
                    1,
                    15,
                    "Define the maximum speed boost of the Flight Card.");
            evasionChance = define(
                    builder,
                    "quantumArmorEvasionChance",
                    30,
                    0,
                    100,
                    "Define the evasion % chance of the evasion card.");
            maxMagnetRange =
                    define(builder, "quantumArmorMagnetRange", 12, 5, 15, "Define the max range of the magnet card.");
            strengthBoost = define(
                    builder,
                    "quantumArmorStrengthBoost",
                    10,
                    5,
                    50,
                    "Define the Attack Damage boost of the Strength Card.");
            attackSpeedBoost = define(
                    builder,
                    "quantumArmorAttackSpeedBoost",
                    5,
                    1,
                    10,
                    "Define the Attack Speed Damage boost of the Attack Speed Card.");
            luckBoost = define(builder, "quantumArmorLuckBoost", 2, 1, 5, "Define the luck boost of the Luck Card.");
            maxReachBoost = define(
                    builder,
                    "quantumArmorMaxReachBoost",
                    5,
                    1,
                    8,
                    "Define the max additional reach of the " + "Reach Card.");
            regenerationPerTick = define(
                    builder,
                    "quantumArmorRenegerationPerTick",
                    10,
                    1,
                    20,
                    "Define the amount of hearts regenerated per tick with the Regeneration Card. Value will be "
                            + "divided by 10 before use.");
            percentageDamageAbsorption = define(
                    builder,
                    "quantumArmorPercentageDamageAbsorption",
                    30,
                    5,
                    100,
                    "Define the maximum percentage of incoming damage absorbed by the Quantum Armor. This value is still limited by the energy buffer in the equipment.");
            builder.pop();

            builder.push("Miscellaneous");
            throughputMonitorCacheSize = define(
                    builder,
                    "throughputMonitorCacheSize",
                    80,
                    40,
                    400,
                    "Define the size of the cache for the Throughput Monitor. Only affects \"Per minute\" and \"Per 10 minutes\" configurations. Higher values will increase precison, but will make the monitor react slower to changes.");
            builder.pop();

            this.spec = builder.build();
        }
    }

    private static IntValue define(ModConfigSpec.Builder builder, String name, int defaultValue, String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue);
    }

    private static IntValue define(
            ModConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue, min, max);
    }

    private static IntValue define(ModConfigSpec.Builder builder, String name, int defaultValue, int min, int max) {
        return builder.defineInRange(name, defaultValue, min, max);
    }

    private static IntValue define(ModConfigSpec.Builder builder, String name, int defaultValue) {
        return define(builder, name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private static BooleanValue define(ModConfigSpec.Builder builder, String name, boolean defaultValue) {
        return builder.define(name, defaultValue);
    }
}
