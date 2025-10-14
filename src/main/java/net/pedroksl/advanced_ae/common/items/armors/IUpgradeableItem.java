package net.pedroksl.advanced_ae.common.items.armors;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.ae2addonlib.api.IGridLinkedItem;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.core.localization.Tooltips;

public interface IUpgradeableItem extends IGridLinkedItem {
    List<UpgradeType> getPossibleUpgrades();

    default List<UpgradeType> getAppliedUpgrades(ItemStack stack) {
        var appliedUpgrades = new ArrayList<UpgradeType>();
        for (var upgrade : getPossibleUpgrades()) {
            if (hasUpgrade(stack, upgrade)) {
                appliedUpgrades.add(upgrade);
            }
        }
        return appliedUpgrades;
    }

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
        var energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energy != null && energy.getEnergyStored() >= upgrade.getCost();
    }

    default boolean isUpgradeEnabledAndPowered(ItemStack stack, UpgradeType upgrade) {
        return isUpgradeEnabled(stack, upgrade) && isUpgradePowered(stack, upgrade);
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

    default boolean toggleUpgrade(ItemStack stack, UpgradeType type) {
        return toggleUpgrade(stack, type, null);
    }

    default boolean toggleUpgrade(ItemStack stack, UpgradeType type, Player player) {
        if (hasUpgrade(stack, type)) {
            var component = AAEComponents.UPGRADE_TOGGLE.get(type);
            var value = stack.get(component);
            if (value != null) {
                stack.set(AAEComponents.UPGRADE_TOGGLE.get(type), !value);

                if (player != null) {
                    var id = Component.translatable(type.item().asItem().getDescriptionId());
                    var msg = id.withStyle(Tooltips.NORMAL_TOOLTIP_TEXT);
                    if (!value) {
                        msg.append(Component.literal(" ON").withStyle(Tooltips.GREEN));
                    } else {
                        msg.append(Component.literal(" OFF").withStyle(Tooltips.RED));
                    }
                    player.displayClientMessage(msg, true);
                }
                return true;
            }
        }
        if (player != null) {
            var id = Component.translatable(type.item().asItem().getDescriptionId());
            player.displayClientMessage(AAEText.UpgradeNotInstalledMessage.text(id), true);
        }
        return false;
    }

    default void tickUpgrades(Level level, Player player, ItemStack stack) {
        for (var upgrade : getAppliedUpgrades(stack)) {
            if (upgrade.applicationType == UpgradeType.ApplicationType.PASSIVE
                    && isUpgradeEnabled(stack, upgrade)
                    && upgrade.ability != null) {
                if (upgrade.ability.execute(level, player, stack)) {
                    consumeEnergy(player, stack, upgrade);
                }
            } else if (upgrade.applicationType == UpgradeType.ApplicationType.BUFF
                    && isUpgradeEnabled(stack, upgrade)) {
                consumeEnergy(player, stack, upgrade);
            }
            if (upgrade == UpgradeType.FLIGHT && player.getAbilities().flying) {
                consumeEnergy(player, stack, upgrade);
            }
        }
    }

    default void consumeEnergy(Player player, ItemStack stack, UpgradeType upgrade) {
        consumeEnergy(player, stack, upgrade.getCost());
    }

    default void consumeEnergy(Player player, ItemStack stack, int amount) {
        if (player.isCreative()) return;

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
