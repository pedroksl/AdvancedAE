package net.pedroksl.advanced_ae.common.definitions;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.ae2addonlib.registry.ConfigRegistry;

@Mod.EventBusSubscriber(modid = AdvancedAE.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AAEConfig extends ConfigRegistry {

    private final ClientConfig client = new ClientConfig();
    private final CommonConfig common = new CommonConfig();

    private static AAEConfig INSTANCE;

    AAEConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, client.spec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, common.spec);
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

    public static void register(String mod_id) {
        if (!mod_id.equals(AdvancedAE.MOD_ID)) {
            throw new IllegalArgumentException();
        }
        INSTANCE = new AAEConfig();
    }

    public static AAEConfig instance() {
        return INSTANCE;
    }

    private static class ClientConfig {
        private final ForgeConfigSpec spec;

        public final ForgeConfigSpec.BooleanValue enableEffects;

        public ClientConfig() {
            var builder = new ForgeConfigSpec.Builder();

            builder.push("reaction chamber");
            enableEffects = define(
                    builder,
                    "enableEffects",
                    true,
                    "Enable/disable reaction chamber's item rendering. Saves on performance if there are a considerable amount of them running at the same time.");
            this.spec = builder.build();
        }
    }

    private static class CommonConfig {
        private final ForgeConfigSpec spec;

        public final ForgeConfigSpec.IntValue quantumComputerMaxSize;
        public final ForgeConfigSpec.IntValue quantumComputerAcceleratorThreads;
        public final ForgeConfigSpec.IntValue quantumComputerMaxMultiThreaders;
        public final ForgeConfigSpec.IntValue quantumComputerMaxDataEntanglers;
        public final ForgeConfigSpec.IntValue quantumComputerMultiThreaderMultiplication;
        public final ForgeConfigSpec.IntValue quantumComputerDataEntanglerMultiplication;

        public final ForgeConfigSpec.IntValue maxWalkSpeed;
        public final ForgeConfigSpec.IntValue maxSprintSpeed;
        public final ForgeConfigSpec.IntValue maxStepHeight;
        public final ForgeConfigSpec.IntValue maxJumpHeight;
        public final ForgeConfigSpec.IntValue hpBufferHearts;
        public final ForgeConfigSpec.IntValue maxFlightSpeed;
        public final ForgeConfigSpec.IntValue evasionChance;
        public final ForgeConfigSpec.IntValue maxMagnetRange;
        public final ForgeConfigSpec.IntValue strengthBoost;
        public final ForgeConfigSpec.IntValue attackSpeedBoost;
        public final ForgeConfigSpec.IntValue luckBoost;
        public final ForgeConfigSpec.IntValue maxReachBoost;
        public final ForgeConfigSpec.IntValue swimSpeedBoost;
        public final ForgeConfigSpec.IntValue regenerationPerTick;
        public final ForgeConfigSpec.IntValue percentageDamageAbsorption;

        public final ForgeConfigSpec.IntValue throughputMonitorCacheSize;

        public CommonConfig() {
            var builder = new ForgeConfigSpec.Builder();

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
}
