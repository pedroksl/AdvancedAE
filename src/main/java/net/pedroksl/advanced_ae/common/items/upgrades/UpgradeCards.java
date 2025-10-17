package net.pedroksl.advanced_ae.common.items.upgrades;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableObject;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.definitions.AAEConfig;
import net.pedroksl.advanced_ae.common.helpers.MagnetHelpers;
import net.pedroksl.advanced_ae.common.items.armors.*;
import net.pedroksl.advanced_ae.xmod.Addons;
import net.pedroksl.advanced_ae.xmod.appflux.AppliedFluxPlugin;
import net.pedroksl.advanced_ae.xmod.curios.CuriosPlugin;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;

public class UpgradeCards {
    public static boolean walkSpeed(Level level, Player player, ItemStack stack) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        boolean canFly = chest.getItem() instanceof QuantumArmorBase armor
                && armor.isUpgradeEnabledAndPowered(chest, UpgradeType.FLIGHT);
        boolean isNotFlying = !player.getAbilities().flying;
        if (!player.isSprinting() && (canFly || isNotFlying) && !player.isInWaterOrBubble()) {
            var upgrade = UpgradeType.WALK_SPEED;
            if (stack.getItem() instanceof QuantumLeggings legs && legs.isUpgradeEnabledAndPowered(stack, upgrade)) {
                return processMovementSpeed(upgrade, player, canFly, stack, chest);
            } else if (canFly && player.getAbilities().flying) {
                return processFlightSpeed(player, chest);
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
                return processMovementSpeed(upgrade, player, canFly, stack, chest);
            } else if (canFly && player.getAbilities().flying) {
                return processFlightSpeed(player, chest);
            }
        }
        return false;
    }

    public static boolean swimSpeed(Level level, Player player, ItemStack stack) {
        if (player.isInWaterOrBubble()) {
            var upgrade = UpgradeType.SWIM_SPEED;
            if (stack.getItem() instanceof QuantumLeggings legs && legs.isUpgradeEnabledAndPowered(stack, upgrade)) {
                return processMovementSpeed(upgrade, player, false, stack, null);
            }
        }
        return false;
    }

    private static boolean processMovementSpeed(
            UpgradeType upgrade, Player player, boolean canFly, ItemStack stack, ItemStack chest) {
        boolean slowDown = true;
        var value = upgrade.getSettings().multiplier * stack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(upgrade), 0);

        if (!(value > 0 && value < 1)) {
            slowDown = false;
            value /= 25f;
        }

        if (canFly && player.getAbilities().flying && chest != null) {
            if (!slowDown) {
                value += chest.getOrDefault(AAEComponents.UPGRADE_VALUE.get(UpgradeType.FLIGHT), 0) / 25f;
            } else {
                value = chest.getOrDefault(AAEComponents.UPGRADE_VALUE.get(UpgradeType.FLIGHT), 0) / 25f;
            }
            slowDown = false;
        }

        if (slowDown && player.onGround()) {
            var motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.multiply(value, 1, value));
            return true;
        } else if (!slowDown && value > 0) {
            if (!player.onGround()) value /= 4;
            if (player.zza < 0F) value /= 2;
            player.moveRelative(
                    value, new Vec3(Math.signum(player.xxa), Math.signum(player.yya), Math.signum(player.zza)));
            return true;
        }
        return false;
    }

    private static boolean processFlightSpeed(Player player, ItemStack chest) {
        var value = chest.getOrDefault(AAEComponents.UPGRADE_VALUE.get(UpgradeType.FLIGHT), 0) / 25f;
        if (value > 0) {
            if (!player.onGround()) value /= 4;
            if (player.zza < 0F) value /= 2;
            player.moveRelative(
                    value, new Vec3(Math.signum(player.xxa), Math.signum(player.yya), Math.signum(player.zza)));
            return true;
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
                                    foodStack = foodStack.finishUsingItem(level, player);
                                    if (!foodStack.isEmpty()) {
                                        storage.getInventory()
                                                .insert(
                                                        AEItemKey.of(foodStack),
                                                        foodStack.getCount(),
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
            if (player.containerMenu != null) {
                if (!player.containerMenu.getCarried().isEmpty()) {
                    return false;
                }
            }

            MutableObject<Component> errorHolder = new MutableObject<>();
            var grid = helmet.getLinkedGrid(stack, level, errorHolder::setValue);
            if (grid != null) {
                var storage = grid.getStorageService();
                var filter = stack.getOrDefault(
                        AAEComponents.UPGRADE_FILTER.get(UpgradeType.AUTO_STOCK), new ArrayList<GenericStack>());
                boolean didSomething = false;
                var inventory = storage.getInventory().getAvailableStacks();
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
                        if (amountDelta > 0 && inventory.get(itemKey) > 0) {
                            long extracted = storage.getInventory()
                                    .extract(
                                            genStack.what(),
                                            amountDelta,
                                            Actionable.MODULATE,
                                            IActionSource.ofPlayer(player));
                            ItemStack stackToInsert = itemKey.toStack((int) extracted);
                            player.addItem(stackToInsert);
                            storage.getInventory()
                                    .insert(
                                            genStack.what(),
                                            stackToInsert.getCount(),
                                            Actionable.MODULATE,
                                            IActionSource.ofPlayer(player));

                            didSomething |= extracted > 0;
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
                                var item = player.getInventory().getItem(slot).copy();
                                var amountToSet = Math.max(0, Math.min(item.getCount(), amountToLeave));
                                item.setCount(amountToSet);
                                player.getInventory().setItem(slot, item);
                                amountToLeave = Math.max(0, amountToLeave - amountToSet);
                            }
                            didSomething |= inserted > 0;
                        }
                    }
                }
                return didSomething;
            }
        }
        return false;
    }

    public static boolean magnet(Level level, Player player, ItemStack stack) {
        if (!player.isSpectator() && stack.getItem() instanceof QuantumHelmet helmet) {
            if (helmet.isUpgradeEnabledAndPowered(stack, UpgradeType.MAGNET)) {
                var range = stack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(UpgradeType.MAGNET), 5);
                var pos = player.position();

                AABB area = MagnetHelpers.getBoundingBox(pos, range);

                // Pick up items
                var filter = stack.getOrDefault(
                        AAEComponents.UPGRADE_FILTER.get(UpgradeType.MAGNET), new ArrayList<GenericStack>());
                var blacklist = stack.getOrDefault(AAEComponents.UPGRADE_EXTRA.get(UpgradeType.MAGNET), true);
                List<ItemEntity> items = level.getEntities(
                        EntityType.ITEM, area, obj -> MagnetHelpers.validEntities(obj, player, filter, blacklist));
                items.forEach(itemEntity -> {
                    if (!level.isClientSide()
                            && player.getInventory().getSlotWithRemainingSpace(itemEntity.getItem()) != -1) {
                        itemEntity.playerTouch(player);
                    }
                    // Still move the items close to the player in case inventory is full
                    itemEntity.setPos(pos);
                });

                // Pick up experience
                if (!level.isClientSide()) {
                    List<ExperienceOrb> xps = level.getEntitiesOfClass(ExperienceOrb.class, area);
                    xps.forEach(xp -> {
                        xp.invulnerableTime = 0;
                        player.takeXpDelay = 0;
                        xp.playerTouch(player);
                    });
                }
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

    public static boolean recharging(Level level, Player player, ItemStack stack) {
        if (stack.getItem() instanceof QuantumArmorBase armor
                && armor.isUpgradeEnabledAndPowered(stack, UpgradeType.CHARGING)
                && armor.getLinkedPosition(stack) != null) {
            MutableObject<Component> errorHolder = new MutableObject<>();
            var grid = armor.getLinkedGrid(stack, level, errorHolder::setValue);
            if (grid != null) {
                var energy = grid.getEnergyService();

                var currentPower = armor.getAECurrentPower(stack);
                var rate = armor.getChargeRate(stack);
                var afRate = Integer.MAX_VALUE;
                var maxPower = armor.getAEMaxPower(stack);
                var neededPower = Math.min(rate, maxPower - currentPower);

                if (neededPower > 0 && Addons.APPFLUX.isLoaded()) {
                    neededPower = Math.min(afRate, maxPower - currentPower);
                    neededPower = AppliedFluxPlugin.rechargeAeStorageItem(grid, neededPower, player, stack, armor);
                }

                if (neededPower > 0 && energy.getStoredPower() > 0) {
                    var extracted = energy.extractAEPower(rate, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    var remainder = armor.injectAEPower(stack, extracted, Actionable.MODULATE);
                    energy.injectPower(remainder, Actionable.MODULATE);
                }

                if (stack.getItem() instanceof QuantumChestplate) {
                    // Recharge Inventory
                    for (var i = 0; i < Inventory.INVENTORY_SIZE; i++) {
                        var item = player.getInventory().getItem(i);
                        if (item.isEmpty()) continue;

                        rechargeItem(player, item, grid, rate, energy);
                    }

                    if (!player.getOffhandItem().isEmpty()) {
                        rechargeItem(player, player.getOffhandItem(), grid, rate, energy);
                    }

                    var optionalInv = CuriosPlugin.getCuriosInventory(player);
                    if (optionalInv.isPresent()) {
                        var curiosInventory = optionalInv.get();
                        var handler = curiosInventory.getEquippedCurios();
                        for (var i = 0; i < handler.getSlots(); i++) {
                            var item = handler.getStackInSlot(i);
                            if (item.isEmpty()) continue;
                            rechargeItem(player, item, grid, rate, energy);
                        }
                    }
                }
            }
        }

        return false;
    }

    private static void rechargeItem(
            Player player, ItemStack stack, IGrid grid, double rate, IEnergyService energyService) {
        var cap = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        var afRate = Integer.MAX_VALUE;
        if (cap != null && cap.canReceive() && cap.getEnergyStored() < cap.getMaxEnergyStored()) {
            if (Addons.APPFLUX.isLoaded()) {
                AppliedFluxPlugin.rechargeEnergyStorage(grid, afRate, IActionSource.ofPlayer(player), cap);
            }

            if (energyService.getStoredPower() > 0) {
                var extracted = energyService.extractAEPower(rate, Actionable.MODULATE, PowerMultiplier.CONFIG);
                var inserted = cap.receiveEnergy((int) extracted, false);
                energyService.injectPower(extracted - inserted, Actionable.MODULATE);
            }
        }
    }
}
