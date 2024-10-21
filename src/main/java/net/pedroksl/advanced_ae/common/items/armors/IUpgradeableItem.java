package net.pedroksl.advanced_ae.common.items.armors;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;

public interface IUpgradeableItem extends IGridLinkedItem {
    List<UpgradeType> getPossibleUpgrades();

    List<UpgradeType> getAppliedUpgrades(ItemStack stack);

    default List<UpgradeType> getPassiveUpgrades(ItemStack itemStack) {
        List<UpgradeType> abilityList = new ArrayList<>();
        getAppliedUpgrades(itemStack).forEach(up -> {
            if (up.applicationType == UpgradeType.ApplicationType.PASSIVE
                    || up.applicationType == UpgradeType.ApplicationType.BUFF) abilityList.add(up);
        });
        return abilityList;
    }

    default boolean isUpgradeEnabled(ItemStack stack, UpgradeType upgrade) {
        return stack.getOrDefault(AAEComponents.UPGRADE_TOGGLE.get(upgrade), false);
    }

    default boolean isUpgradePowered(ItemStack stack, UpgradeType upgrade) {
        return isUpgradePowered(stack, upgrade, null);
    }

    default boolean isUpgradePowered(ItemStack stack, UpgradeType upgrade, Level level) {
        // Use internal buffer
        var energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy != null && energy.getEnergyStored() > upgrade.getCost()) return true;

        // If that failed, try to pull from the grid
        if (level != null && getLinkedPosition(stack) != null) {
            MutableObject<Component> errorHolder = new MutableObject<>();
            var grid = getLinkedGrid(stack, level, errorHolder::setValue);
            if (grid != null) {
                var energyService = grid.getEnergyService();
                var extracted =
                        energyService.extractAEPower(upgrade.getCost(), Actionable.SIMULATE, PowerMultiplier.CONFIG);
                return extracted >= upgrade.getCost() - 0.01;
            }
        }
        return false;
    }

    default boolean isUpgradeEnabledAndPowered(ItemStack stack, UpgradeType upgrade) {
        return isUpgradeEnabled(stack, upgrade) && isUpgradePowered(stack, upgrade);
    }

    default boolean isUpgradeEnabledAndPowered(ItemStack stack, UpgradeType upgrade, @Nullable Level level) {
        return isUpgradeEnabled(stack, upgrade) && isUpgradePowered(stack, upgrade, level);
    }

    default boolean isUpgradeAllowed(UpgradeType type) {
        return getPossibleUpgrades().contains(type);
    }

    default boolean hasUpgrade(ItemStack stack, UpgradeType type) {
        return stack.has(AAEComponents.UPGRADE_TOGGLE.get(type));
    }

    default boolean applyUpgrade(ItemStack stack, UpgradeType type) {
        if (!isUpgradeAllowed(type) || hasUpgrade(stack, type)) {
            return false;
        }

        getAppliedUpgrades(stack).add(type);
        stack.set(AAEComponents.UPGRADE_TOGGLE.get(type), true);
        stack.set(AAEComponents.UPGRADE_VALUE.get(type), type.getSettings().maxValue);
        stack.set(AAEComponents.UPGRADE_FILTER.get(type), new ArrayList<>());
        if (type.getExtraSettings() != UpgradeType.ExtraSettings.NONE) {
            stack.set(AAEComponents.UPGRADE_EXTRA.get(type), true);
        }
        return true;
    }

    default boolean removeUpgrade(ItemStack stack, UpgradeType type) {
        if (getAppliedUpgrades(stack).contains(type)) {
            stack.remove(AAEComponents.UPGRADE_TOGGLE.get(type));
            stack.remove(AAEComponents.UPGRADE_VALUE.get(type));
            stack.remove(AAEComponents.UPGRADE_FILTER.get(type));
            getAppliedUpgrades(stack).remove(type);
            return true;
        }
        return false;
    }

    default void tickUpgrades(Level level, Player player, ItemStack stack) {
        for (var upgrade : getAppliedUpgrades(stack)) {
            if (upgrade.applicationType == UpgradeType.ApplicationType.PASSIVE
                    && isUpgradeEnabled(stack, upgrade)
                    && upgrade.ability != null) {
                if (upgrade.ability.execute(level, player, stack)) {
                    consumeEnergy(stack, upgrade);
                }
            } else if (upgrade.applicationType == UpgradeType.ApplicationType.BUFF
                    && isUpgradeEnabled(stack, upgrade)) {
                consumeEnergy(stack, upgrade);
            }
            if (upgrade == UpgradeType.FLIGHT && player.getAbilities().flying) {
                consumeEnergy(stack, upgrade);
            }
        }
    }

    default void consumeEnergy(ItemStack stack, UpgradeType upgrade) {
        consumeEnergy(stack, upgrade.getCost());
    }

    default void consumeEnergy(ItemStack stack, int amount) {
        if (stack.getItem() instanceof PoweredItem item) {
            var multi = PowerMultiplier.CONFIG;
            item.extractAEPower(stack, multi.multiply(amount), Actionable.MODULATE);
        } else {
            var energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energy != null) {
                energy.extractEnergy(amount, false);
            }
        }
    }
}
