package net.pedroksl.advanced_ae.events;

import java.util.Random;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.definitions.AAEConfig;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import appeng.api.config.Actionable;

public class AAELivingEntityEvents {

    @SubscribeEvent
    public static void invulnerability(EntityInvulnerabilityCheckEvent event) {
        Entity target = event.getEntity();
        if (target instanceof Player player) {
            ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chestStack.getItem() instanceof QuantumArmorBase item
                    && item.isUpgradeEnabledAndPowered(chestStack, UpgradeType.LAVA_IMMUNITY)) {
                if (event.getSource().is(DamageTypes.LAVA)
                        || event.getSource().is(DamageTypes.IN_FIRE)
                        || event.getSource().is(DamageTypes.ON_FIRE)) {
                    player.setRemainingFireTicks(0);
                    event.setInvulnerable(true);
                    item.consumeEnergy(chestStack, UpgradeType.LAVA_IMMUNITY);
                }
            }
            ItemStack bootStack = player.getItemBySlot(EquipmentSlot.FEET);
            if (bootStack.getItem() instanceof QuantumArmorBase item
                    && item.isUpgradeEnabledAndPowered(bootStack, UpgradeType.EVASION)) {
                Random randomGenerator = new Random();
                var chance = randomGenerator.nextDouble(100);
                if (chance < AAEConfig.instance().getEvasionChance()) {
                    event.setInvulnerable(true);
                    item.consumeEnergy(bootStack, UpgradeType.EVASION);
                }
            }
        }
    }

    @SubscribeEvent
    public static void breath(LivingBreatheEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
            Level level = player instanceof ServerPlayer serverPlayer ? serverPlayer.level() : null;
            if (stack.getItem() instanceof QuantumArmorBase item
                    && item.isUpgradeEnabledAndPowered(stack, UpgradeType.WATER_BREATHING, level)) {
                event.setCanBreathe(true);
                item.consumeEnergy(stack, UpgradeType.WATER_BREATHING);
            }
        }
    }

    @SubscribeEvent
    public static void jumpEvent(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.FEET);
            Level level = player instanceof ServerPlayer serverPlayer ? serverPlayer.level() : null;
            if (stack.getItem() instanceof QuantumArmorBase item
                    && item.isUpgradeEnabledAndPowered(stack, UpgradeType.JUMP_HEIGHT, level)) {
                UpgradeType.JUMP_HEIGHT.ability.execute(player.level(), player, stack);
                item.consumeEnergy(stack, UpgradeType.JUMP_HEIGHT);
            }
        }
    }

    @SubscribeEvent
    public static void LivingFallDamage(LivingFallEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.FEET);
            if (stack.getItem() instanceof QuantumArmorBase item) {
                if (item.extractAEPower(stack, 10, Actionable.SIMULATE) > 0) {
                    event.setDistance(0.0f);
                }
            }
        }
    }
}
