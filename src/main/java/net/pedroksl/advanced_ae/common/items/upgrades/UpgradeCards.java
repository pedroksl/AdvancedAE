package net.pedroksl.advanced_ae.common.items.upgrades;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableObject;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.definitions.AAEConfig;
import net.pedroksl.advanced_ae.common.helpers.MagnetHelpers;
import net.pedroksl.advanced_ae.common.items.armors.*;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;

public class UpgradeCards {
    public static boolean walkSpeed(Level level, Player player, ItemStack stack) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        boolean canFly = chest.getItem() instanceof QuantumArmorBase armor
                && armor.isUpgradeEnabledAndPowered(chest, UpgradeType.FLIGHT);
        boolean isNotFlying = player.fallDistance <= 0 && !player.isFallFlying();
        if (!player.isSprinting() && (canFly || isNotFlying) && !player.isInWaterOrBubble()) {
            var upgrade = UpgradeType.WALK_SPEED;
            var value = upgrade.getSettings().multiplier
                    * stack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(upgrade), 0)
                    / 25f;
            if (canFly && player.getAbilities().flying) {
                value += chest.getOrDefault(AAEComponents.UPGRADE_VALUE.get(UpgradeType.FLIGHT), 0) / 25f;
            }
            if (value > 0) {
                if (!player.onGround()) value /= 4;
                if (player.zza < 0F) value /= 2;
                player.moveRelative(
                        value, new Vec3(Math.signum(player.xxa), Math.signum(player.yya), Math.signum(player.zza)));
                return true;
            }
        }
        return false;
    }

    public static boolean sprintSpeed(Level level, Player player, ItemStack stack) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        boolean canFly = chest.getItem() instanceof QuantumArmorBase armor
                && armor.isUpgradeEnabledAndPowered(chest, UpgradeType.FLIGHT);
        boolean isNotFlying = player.fallDistance <= 0 && !player.isFallFlying();
        if (player.isSprinting() && (canFly || isNotFlying) && !player.isInWaterOrBubble()) {
            var upgrade = UpgradeType.SPRINT_SPEED;
            if (stack.getItem() instanceof QuantumLeggings legs && legs.isUpgradeEnabledAndPowered(stack, upgrade)) {
                var value = upgrade.getSettings().multiplier
                        * stack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(upgrade), 0)
                        / 25f;
                if (value > 0) {
                    if (canFly && player.getAbilities().flying) {
                        value += chest.getOrDefault(AAEComponents.UPGRADE_VALUE.get(UpgradeType.FLIGHT), 0) / 25f;
                    }
                    if (!player.onGround()) value /= 4;
                    if (player.zza < 0F) value /= 2;
                    player.moveRelative(
                            value, new Vec3(Math.signum(player.xxa), Math.signum(player.yya), Math.signum(player.zza)));
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean jumpHeight(Level level, Player player, ItemStack stack) {
        if (!player.isInWaterOrBubble() && !player.isFallFlying()) {
            var upgrade = UpgradeType.JUMP_HEIGHT;
            if (stack.getItem() instanceof QuantumBoots boots && boots.isUpgradeEnabledAndPowered(stack, upgrade)) {
                var value = upgrade.getSettings().multiplier
                        * stack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(upgrade), -1)
                        / 8f;
                if (value > 0) {
                    if (player.isSprinting()) value *= 2;
                    player.moveRelative(value, new Vec3(0, 1, 0));
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean autoFeed(Level level, Player player, ItemStack stack) {
        if (player.getFoodData().needsFood()
                && stack.getItem() instanceof QuantumHelmet helmet
                && helmet.isUpgradeEnabledAndPowered(stack, UpgradeType.AUTO_FEED)
                && helmet.getLinkedPosition(stack) != null) {
            MutableObject<Component> errorHolder = new MutableObject<>();
            var grid = helmet.getLinkedGrid(stack, level, errorHolder::setValue);
            if (grid != null) {
                var storage = grid.getStorageService();
                var filter = stack.getOrDefault(
                        AAEComponents.UPGRADE_FILTER.get(UpgradeType.AUTO_FEED), new ArrayList<GenericStack>());
                for (var genStack : filter) {
                    if (storage.getInventory()
                                    .extract(genStack.what(), 1, Actionable.SIMULATE, IActionSource.ofPlayer(player))
                            > 0) {
                        if (genStack.what() instanceof AEItemKey itemKey) {
                            ItemStack foodStack = itemKey.toStack();
                            if (foodStack.has(DataComponents.FOOD)) {
                                var foodProperties = foodStack.get(DataComponents.FOOD);
                                if (foodProperties != null) {
                                    storage.getInventory()
                                            .extract(
                                                    genStack.what(),
                                                    1,
                                                    Actionable.MODULATE,
                                                    IActionSource.ofPlayer(player));
                                    ItemStack remainingStack = player.eat(level, foodStack, foodProperties);
                                    if (!remainingStack.isEmpty()) {
                                        storage.getInventory()
                                                .insert(
                                                        AEItemKey.of(remainingStack),
                                                        remainingStack.getCount(),
                                                        Actionable.MODULATE,
                                                        IActionSource.ofPlayer(player));
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public static boolean autoStock(Level level, Player player, ItemStack stack) {
        if (stack.getItem() instanceof QuantumHelmet helmet
                && helmet.isUpgradeEnabledAndPowered(stack, UpgradeType.AUTO_STOCK)
                && helmet.getLinkedPosition(stack) != null) {
            if (player.containerMenu != null
                    && !player.containerMenu.getCarried().isEmpty()) return false;
            MutableObject<Component> errorHolder = new MutableObject<>();
            var grid = helmet.getLinkedGrid(stack, level, errorHolder::setValue);
            if (grid != null) {
                var storage = grid.getStorageService();
                var filter = stack.getOrDefault(
                        AAEComponents.UPGRADE_FILTER.get(UpgradeType.AUTO_STOCK), new ArrayList<GenericStack>());
                for (var genStack : filter) {
                    if (genStack.what() instanceof AEItemKey itemKey) {
                        var desiredAmount = genStack.amount();
                        var currentAmount = 0;
                        var slots = new ArrayList<Integer>();
                        for (var x = 0; x < player.getInventory().getContainerSize(); x++) {
                            var currentStack = player.getInventory().getItem(x);
                            if (itemKey.is(currentStack.getItem())) {
                                currentAmount += currentStack.getCount();
                                slots.add(x);
                            }
                        }
                        var amountDelta = desiredAmount - currentAmount;
                        if (amountDelta > 0) {
                            long extracted = storage.getInventory()
                                    .extract(
                                            genStack.what(),
                                            amountDelta,
                                            Actionable.MODULATE,
                                            IActionSource.ofPlayer(player));
                            ItemStack stackToInsert = new ItemStack(itemKey.getItem(), (int) extracted);
                            player.addItem(stackToInsert);
                            storage.getInventory()
                                    .insert(
                                            genStack.what(),
                                            stackToInsert.getCount(),
                                            Actionable.MODULATE,
                                            IActionSource.ofPlayer(player));
                            return true;
                        } else if (amountDelta < 0) {
                            amountDelta = -amountDelta;
                            var inserted = storage.getInventory()
                                    .insert(
                                            genStack.what(),
                                            amountDelta,
                                            Actionable.MODULATE,
                                            IActionSource.ofPlayer(player));
                            var amountToLeave = (int) desiredAmount + (int) (amountDelta - inserted);
                            for (var slot : slots) {
                                var item = player.getInventory().getItem(slot);
                                var amountToSet = Math.max(0, Math.min(item.getCount(), amountToLeave));
                                player.getInventory().setItem(slot, new ItemStack(item.getItem(), amountToSet));
                                amountToLeave = Math.max(0, amountToLeave - amountToSet);
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean magnet(Level level, Player player, ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer && stack.getItem() instanceof QuantumHelmet helmet) {
            if (helmet.isUpgradeEnabledAndPowered(stack, UpgradeType.MAGNET, level)) {
                var range = stack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(UpgradeType.MAGNET), 5);
                var pos = serverPlayer.position();

                AABB area = MagnetHelpers.getBoundingBox(pos, range);

                // Pick up items
                var filter = stack.getOrDefault(
                        AAEComponents.UPGRADE_FILTER.get(UpgradeType.MAGNET), new ArrayList<GenericStack>());
                var blacklist = stack.getOrDefault(AAEComponents.UPGRADE_EXTRA.get(UpgradeType.MAGNET), true);
                List<ItemEntity> items = level.getEntities(
                        EntityType.ITEM, area, obj -> MagnetHelpers.validEntities(obj, player, filter, blacklist));
                items.forEach(itemEntity -> {
                    itemEntity.playerTouch(player);
                    // Still move the items close to the player in case inventory is full
                    itemEntity.setPos(pos);
                });

                // Pick up experience
                List<ExperienceOrb> xps = level.getEntitiesOfClass(ExperienceOrb.class, area);
                xps.forEach(xp -> {
                    xp.invulnerableTime = 0;
                    player.takeXpDelay = 0;
                    xp.playerTouch(player);
                });
                return true;
            }
        }
        return false;
    }

    public static boolean regeneration(Level level, Player player, ItemStack stack) {
        if (stack.getItem() instanceof QuantumChestplate chest) {
            if (chest.isUpgradeEnabledAndPowered(stack, UpgradeType.REGENERATION)) {
                player.heal((float) (0.1 * AAEConfig.instance().getRenegerationPerTick()));
                return true;
            }
        }
        return false;
    }
}