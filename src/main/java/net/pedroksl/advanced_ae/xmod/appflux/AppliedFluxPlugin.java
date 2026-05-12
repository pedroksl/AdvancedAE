package net.pedroksl.advanced_ae.xmod.appflux;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import appeng.api.config.PowerMultiplier;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionSource;

public class AppliedFluxPlugin {

    public static void init() {
        try {
            //            Upgrades.add(
            //                    AFSingletons.INDUCTION_CARD,
            //                    AAEBlocks.ADV_PATTERN_PROVIDER,
            //                    1,
            //                    AAEText.AdvPatternProvider.getTranslationKey());
            //            Upgrades.add(
            //                    AFSingletons.INDUCTION_CARD,
            //                    AAEItems.ADV_PATTERN_PROVIDER,
            //                    1,
            //                    AAEText.AdvPatternProvider.getTranslationKey());
            //            Upgrades.add(
            //                    AFSingletons.INDUCTION_CARD,
            //                    AAEBlocks.SMALL_ADV_PATTERN_PROVIDER,
            //                    1,
            //                    AAEText.AdvPatternProvider.getTranslationKey());
            //            Upgrades.add(
            //                    AFSingletons.INDUCTION_CARD,
            //                    AAEItems.SMALL_ADV_PATTERN_PROVIDER,
            //                    1,
            //                    AAEText.AdvPatternProvider.getTranslationKey());
        } catch (Throwable ignored) {
            // NO-OP
        }
    }

    public static double rechargeAeStorageItem(
            IGrid grid, double neededPower, Player player, ItemStack stack, IAEItemPowerStorage aePowerStorage) {
        try {
            var storage = grid.getStorageService();

            var mult = PowerMultiplier.CONFIG;
            var neededFePower = mult.divide(neededPower);

            //            var extracted = mult.multiply(storage.getInventory()
            //                    .extract(
            //                            FluxKey.of(EnergyType.FE),
            //                            (long) neededFePower,
            //                            Actionable.MODULATE,
            //                            IActionSource.ofPlayer(player)));

            //            var remainder = aePowerStorage.injectAEPower(stack, extracted, Actionable.MODULATE);
            //            storage.getInventory()
            //                    .insert(
            //                            FluxKey.of(EnergyType.FE),
            //                            (long) mult.divide(remainder),
            //                            Actionable.MODULATE,
            //                            IActionSource.ofPlayer(player));

            //            neededPower -= extracted - remainder;
        } catch (Throwable ignored) {
            // NO_OP
        }
        return neededPower;
    }

    public static void rechargeEnergyStorage(
            IGrid grid, int afRate, IActionSource source, EnergyHandler cap, Transaction tx) {
        try {
            var storage = grid.getStorageService();

            //            var extracted =
            //                    storage.getInventory().extract(FluxKey.of(EnergyType.FE), afRate, Actionable.MODULATE,
            // source);
            //            var inserted = cap.receiveEnergy((int) extracted, false);
            //            storage.getInventory().insert(FluxKey.of(EnergyType.FE), extracted - inserted,
            // Actionable.MODULATE, source);
        } catch (Throwable ignored) {
            // NO_OP
        }
    }
}
