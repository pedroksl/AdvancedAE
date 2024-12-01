package net.pedroksl.advanced_ae.common.items.armors;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.pedroksl.advanced_ae.common.definitions.AAENbt;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.stacks.GenericStack;
import appeng.core.localization.Tooltips;

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

    default boolean isUpgradeEnabled(@NotNull ItemStack stack, UpgradeType type) {
        var tag = stack.getTagElement(AAENbt.UPGRADE_TAG.get(type));
        if (tag != null) {
            return tag.getBoolean(AAENbt.UPGRADE_TOGGLE);
        }
        return false;
    }

    default boolean isUpgradePowered(ItemStack stack, UpgradeType upgrade) {
        return isUpgradePowered(stack, upgrade, null);
    }

    default boolean isUpgradePowered(@NotNull ItemStack stack, UpgradeType upgrade, Level level) {
        var energyOp = stack.getCapability(ForgeCapabilities.ENERGY);
        return energyOp.isPresent() && energyOp.resolve().get().getEnergyStored() >= upgrade.getCost();
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
        return stack.getTagElement(AAENbt.UPGRADE_TAG.get(type)) != null;
    }

    default boolean applyUpgrade(ItemStack stack, UpgradeType type) {
        if (!isUpgradeAllowed(type) || hasUpgrade(stack, type)) {
            return false;
        }

        getAppliedUpgrades(stack).add(type);
        var tag = new CompoundTag();
        tag.putBoolean(AAENbt.UPGRADE_TOGGLE, true);
        tag.putInt(AAENbt.UPGRADE_VALUE, type.getSettings().defaultValue);
        tag.put(AAENbt.UPGRADE_FILTER, new ListTag());
        if (type.getExtraSettings() != UpgradeType.ExtraSettings.NONE) {
            tag.putBoolean(AAENbt.UPGRADE_EXTRA, true);
        }
        stack.addTagElement(AAENbt.UPGRADE_TAG.get(type), tag);
        return true;
    }

    default boolean removeUpgrade(ItemStack stack, UpgradeType type) {
        if (getAppliedUpgrades(stack).contains(type)) {
            stack.removeTagKey(AAENbt.UPGRADE_TAG.get(type));
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
            var tag = stack.getTagElement(AAENbt.UPGRADE_TAG.get(type));
            if (tag != null) {
                tag.putBoolean(AAENbt.UPGRADE_TOGGLE, !tag.getBoolean(AAENbt.UPGRADE_TOGGLE));
                stack.addTagElement(AAENbt.UPGRADE_TAG.get(type), tag);

                if (player != null) {
                    var id = Component.translatable(type.item().asItem().getDescriptionId());
                    var msg = id.withStyle(Tooltips.NORMAL_TOOLTIP_TEXT);
                    if (tag.getBoolean(AAENbt.UPGRADE_TOGGLE)) {
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

    default int getUpgradeValue(@NotNull ItemStack stack, UpgradeType type, int defaultValue) {
        if (hasUpgrade(stack, type)) {
            var tag = stack.getTagElement(AAENbt.UPGRADE_TAG.get(type));
            if (tag != null) {
                if (tag.contains(AAENbt.UPGRADE_VALUE)) {
                    return tag.getInt(AAENbt.UPGRADE_VALUE);
                }
            }
        }
        return defaultValue;
    }

    default void setUpgradeValue(@NotNull ItemStack stack, UpgradeType type, int value) {
        if (hasUpgrade(stack, type)) {
            var tag = stack.getTagElement(AAENbt.UPGRADE_TAG.get(type));
            if (tag != null) {
                tag.putInt(AAENbt.UPGRADE_VALUE, value);
                stack.addTagElement(AAENbt.UPGRADE_TAG.get(type), tag);
            }
        }
    }

    default List<GenericStack> getFilter(@NotNull ItemStack stack, UpgradeType type) {
        List<GenericStack> list = new ArrayList<>();

        if (hasUpgrade(stack, type)) {
            var tag = stack.getTagElement(AAENbt.UPGRADE_TAG.get(type));
            if (tag != null) {
                if (tag.contains(AAENbt.UPGRADE_FILTER)) {
                    var listTag = tag.getList(AAENbt.UPGRADE_FILTER, CompoundTag.TAG_COMPOUND);
                    for (net.minecraft.nbt.Tag value : listTag) {
                        list.add(GenericStack.readTag(((CompoundTag) value)));
                    }
                }
            }
        }

        return list;
    }

    default void setFilter(@NotNull ItemStack stack, UpgradeType type, List<GenericStack> list) {
        if (hasUpgrade(stack, type)) {
            var tag = stack.getTagElement(AAENbt.UPGRADE_TAG.get(type));
            if (tag != null) {
                ListTag listTag = new ListTag();
                for (var genStack : list) {
                    listTag.add(GenericStack.writeTag(genStack));
                }
                tag.put(AAENbt.UPGRADE_FILTER, listTag);
                stack.addTagElement(AAENbt.UPGRADE_TAG.get(type), tag);
            }
        }
    }

    default boolean getUpgradeExtra(@NotNull ItemStack stack, UpgradeType type, boolean defaultValue) {
        if (hasUpgrade(stack, type)) {
            var tag = stack.getTagElement(AAENbt.UPGRADE_TAG.get(type));
            if (tag != null) {
                if (tag.contains(AAENbt.UPGRADE_EXTRA)) {
                    return tag.getBoolean(AAENbt.UPGRADE_EXTRA);
                }
            }
        }
        return defaultValue;
    }

    default void setUpgradeExtra(@NotNull ItemStack stack, UpgradeType type, boolean value) {
        if (hasUpgrade(stack, type)) {
            var tag = stack.getTagElement(AAENbt.UPGRADE_TAG.get(type));
            if (tag != null) {
                tag.putBoolean(AAENbt.UPGRADE_EXTRA, value);
                stack.addTagElement(AAENbt.UPGRADE_TAG.get(type), tag);
            }
        }
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
            if (upgrade == UpgradeType.FLIGHT && upgrade.ability != null) {
                upgrade.ability.execute(level, player, stack);
                if (player.getAbilities().flying) {
                    consumeEnergy(player, stack, upgrade);
                }
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
            var energyOp = stack.getCapability(ForgeCapabilities.ENERGY);
            energyOp.ifPresent(e -> e.extractEnergy(amount, false));
        }
    }
}
