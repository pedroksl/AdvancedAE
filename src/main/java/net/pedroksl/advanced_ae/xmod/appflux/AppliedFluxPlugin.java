package net.pedroksl.advanced_ae.xmod.appflux;

import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEText;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.upgrades.Upgrades;

public class AppliedFluxPlugin {

    public static void init() {
        try {
            Upgrades.add(
                    AFSingletons.INDUCTION_CARD,
                    AAEBlocks.ADV_PATTERN_PROVIDER,
                    1,
                    AAEText.AdvPatternProvider.getTranslationKey());
            Upgrades.add(
                    AFSingletons.INDUCTION_CARD,
                    AAEItems.ADV_PATTERN_PROVIDER,
                    1,
                    AAEText.AdvPatternProvider.getTranslationKey());
            Upgrades.add(
                    AFSingletons.INDUCTION_CARD,
                    AAEBlocks.SMALL_ADV_PATTERN_PROVIDER,
                    1,
                    AAEText.AdvPatternProvider.getTranslationKey());
            Upgrades.add(
                    AFSingletons.INDUCTION_CARD,
                    AAEItems.SMALL_ADV_PATTERN_PROVIDER,
                    1,
                    AAEText.AdvPatternProvider.getTranslationKey());
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

            var extracted = mult.multiply(storage.getInventory()
                    .extract(
                            FluxKey.of(EnergyType.FE),
                            (long) neededFePower,
                            Actionable.MODULATE,
                            IActionSource.ofPlayer(player)));

            var remainder = aePowerStorage.injectAEPower(stack, extracted, Actionable.MODULATE);
            storage.getInventory().insert(FluxKey.of(EnergyType.FE), (long) mult.divide(remainder),
                    Actionable.MODULATE, IActionSource.ofPlayer(player));

            neededPower -= extracted - remainder;
        } catch (Throwable ignored) {
            // NO_OP
        }
        return neededPower;
    }

    public static void rechargeEnergyStorage(IGrid grid, int afRate, IActionSource source, IEnergyStorage cap) {
        try {
            var storage = grid.getStorageService();

            var extracted =
                    storage.getInventory().extract(FluxKey.of(EnergyType.FE), afRate, Actionable.MODULATE, source);
            var inserted = cap.receiveEnergy((int) extracted, false);
            storage.getInventory().insert(FluxKey.of(EnergyType.FE), extracted - inserted, Actionable.MODULATE, source);
        } catch (Throwable ignored) {
            // NO_OP
        }
    }
}
