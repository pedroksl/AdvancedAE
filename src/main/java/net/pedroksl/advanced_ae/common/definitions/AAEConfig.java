package net.pedroksl.advanced_ae.common.definitions;

import appeng.core.AELog;
import appeng.core.config.ConfigFileManager;
import appeng.core.config.ConfigSection;
import appeng.core.config.ConfigValidationException;
import appeng.core.config.IntegerOption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AAEConfig {

    //public static final String CLIENT_CONFIG_PATH = "client.json";
    public static final String COMMON_CONFIG_PATH = "advanced_ae-common.json";

    //private final ClientConfig client;
    private final CommonConfig common;
    public final ConfigFileManager commonConfigManager;

    private static AAEConfig INSTANCE;

    AAEConfig(Path configDir) {
        //ConfigSection clientRoot = ConfigSection.createRoot();
        //client = new AAEConfig.ClientConfig(clientRoot);

        ConfigSection commonRoot = ConfigSection.createRoot();

        if (configDir != null) {
            commonConfigManager = createConfigFileManager(commonRoot, configDir, COMMON_CONFIG_PATH);
        } else {
            commonConfigManager = null;
        }

        common = new AAEConfig.CommonConfig(commonRoot);
    }

    private static ConfigFileManager createConfigFileManager(ConfigSection commonRoot, Path configDir,
                                                             String filename) {
        var configFile = configDir.resolve(filename);
        ConfigFileManager result = new ConfigFileManager(commonRoot, configFile);
        if (!Files.exists(configFile)) {
            result.save(); // Save a default file
        } else {
            try {
                result.load();
            } catch (ConfigValidationException e) {
                AELog.error("Failed to load AAE Config. Making backup", e);

                // Backup and delete config files to reset them
                makeBackupAndReset(configDir, filename);
            }

            // Re-save immediately to write-out new defaults
            try {
                result.save();
            } catch (Exception e) {
                AELog.warn(e);
            }
        }
        return result;
    }

    private static void makeBackupAndReset(Path configFolder, String configFile) {
        var backupFile = configFolder.resolve(configFile + ".bak");
        var originalFile = configFolder.resolve(configFile);
        try {
            Files.move(originalFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            AELog.warn("Failed to backup config file %s: %s!", originalFile, e);
        }
    }

    public void save() {
    }

    public void reload() {
        //clientConfigManager.load();
        commonConfigManager.load();
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

    public static void load(Path configFolder) {
        if (INSTANCE != null) {
            throw new IllegalStateException("Config is already loaded");
        }
        INSTANCE = new AAEConfig(configFolder);
    }

    public static AAEConfig instance() {
        return INSTANCE;
    }

    private static class ClientConfig {

        public ClientConfig(ConfigSection root) {
        }
    }

    public static class CommonConfig {
        public final IntegerOption quantumComputerMaxSize;
        public final IntegerOption quantumComputerAcceleratorThreads;
        public final IntegerOption quantumComputerMaxMultiThreaders;
        public final IntegerOption quantumComputerMaxDataEntanglers;
        public final IntegerOption quantumComputerMultiThreaderMultiplication;
        public final IntegerOption quantumComputerDataEntanglerMultiplication;

        public final IntegerOption maxWalkSpeed;
        public final IntegerOption maxSprintSpeed;
        public final IntegerOption maxStepHeight;
        public final IntegerOption maxJumpHeight;
        public final IntegerOption hpBufferHearts;
        public final IntegerOption maxFlightSpeed;
        public final IntegerOption evasionChance;
        public final IntegerOption maxMagnetRange;
        public final IntegerOption strengthBoost;
        public final IntegerOption attackSpeedBoost;
        public final IntegerOption luckBoost;
        public final IntegerOption maxReachBoost;
        public final IntegerOption swimSpeedBoost;
        public final IntegerOption regenerationPerTick;
        public final IntegerOption percentageDamageAbsorption;

        public CommonConfig(ConfigSection root) {

            ConfigSection qc = root.subsection("quantum computer");
            quantumComputerMaxSize = define(
                    qc,
                    "quantumComputerMaxSize",
                    5,
                    5,
                    12,
                    "Define the maximum dimensions of the Quantum Computer Multiblock.");
            quantumComputerAcceleratorThreads = define(
                    qc,
                    "quantumComputerAcceleratorThreads",
                    8,
                    4,
                    16,
                    "Define the maximum amount of multi threaders per Quantum Computer Multiblock.");
            quantumComputerMaxMultiThreaders = define(
                    qc,
                    "quantumComputerMaxMultiThreaders",
                    1,
                    1,
                    2,
                    "Define the maximum amount of multi threaders per Quantum Computer Multiblock.");
            quantumComputerMaxDataEntanglers = define(
                    qc,
                    "quantumComputermaxDataEntanglers",
                    1,
                    1,
                    2,
                    "Define the maximum amount of Data Entanglers per Quantum Computer Multiblock.");
            quantumComputerMultiThreaderMultiplication = define(
                    qc,
                    "quantumComputerMultiThreaderMultiplication",
                    4,
                    2,
                    8,
                    "Define the multiplication factor of the multi threaders.");
            quantumComputerDataEntanglerMultiplication = define(
                    qc,
                    "quantumComputerDataEntanglerMultiplication",
                    4,
                    2,
                    8,
                    "Define the multiplication factor of the data entanglers.");

            ConfigSection qa = root.subsection("quantum armor");
            maxWalkSpeed = define(
                    qa,
                    "quantumArmorMaxWalkSpeed",
                    60,
                    10,
                    100,
                    "Define the maximum walk speed increase. Values are divided by 10 before use.");
            maxSprintSpeed = define(
                    qa,
                    "quantumArmorMaxSprintSpeed",
                    80,
                    10,
                    150,
                    "Define the maximum sprint speed increase. Values are divided by 10 before use.");
            maxStepHeight = define(
                    qa, "quantumArmorMaxStepHeight", 3, 1, 5, "Define the maximum increase in step height.");
            maxJumpHeight = define(
                    qa, "quantumArmorMaxJumpHeight", 3, 1, 5, "Define the maximum increase in jump height.");
            swimSpeedBoost = define(
                    qa,
                    "quantumArmorSwimSpeedBoost",
                    80,
                    10,
                    150,
                    "Define the maximum swim speed increase. Values are divided by 10 before use.");
            hpBufferHearts = define(
                    qa, "quantumArmorHpBuffer", 20, 5, 50, "Define the HP increased of the HP Buffer card.");
            maxFlightSpeed = define(
                    qa,
                    "quantumArmorMaxFlightSpeed",
                    10,
                    1,
                    15,
                    "Define the maximum speed boost of the Flight Card.");
            evasionChance = define(
                    qa,
                    "quantumArmorEvasionChance",
                    30,
                    0,
                    100,
                    "Define the evasion % chance of the evasion card.");
            maxMagnetRange =
                    define(qa, "quantumArmorMagnetRange", 12, 5, 15, "Define the max range of the magnet card.");
            strengthBoost = define(
                    qa,
                    "quantumArmorStrengthBoost",
                    10,
                    5,
                    50,
                    "Define the Attack Damage boost of the Strength Card.");
            attackSpeedBoost = define(
                    qa,
                    "quantumArmorAttackSpeedBoost",
                    5,
                    1,
                    10,
                    "Define the Attack Speed Damage boost of the Attack Speed Card.");
            luckBoost = define(qa, "quantumArmorLuckBoost", 2, 1, 5, "Define the luck boost of the Luck Card.");
            maxReachBoost = define(
                    qa,
                    "quantumArmorMaxReachBoost",
                    5,
                    1,
                    8,
                    "Define the max additional reach of the " + "Reach Card.");
            regenerationPerTick = define(
                    qa,
                    "quantumArmorRenegerationPerTick",
                    10,
                    1,
                    20,
                    "Define the amount of hearts regenerated per tick with the Regeneration Card. Value will be "
                            + "divided by 10 before use.");
            percentageDamageAbsorption = define(
                    qa,
                    "quantumArmorPercentageDamageAbsorption",
                    30,
                    5,
                    100,
                    "Define the maximum percentage of incoming damage absorbed by the Quantum Armor. This value is still limited by the energy buffer in the equipment.");
        }

        private static IntegerOption define(
                ConfigSection builder, String name, int defaultValue, String comment) {
            return builder.addInt(name, defaultValue, comment);
        }

        private static IntegerOption define(
                ConfigSection builder, String name, int defaultValue, int min, int max, String comment) {
            return builder.addInt(name, defaultValue, min, max, comment);
        }
    }
}
