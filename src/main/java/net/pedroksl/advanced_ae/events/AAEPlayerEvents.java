package net.pedroksl.advanced_ae.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.items.armors.*;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.network.packet.ItemTrackingPacket;
import net.pedroksl.advanced_ae.xmod.Addons;
import net.pedroksl.advanced_ae.xmod.apoth.ApoEnchPlugin;

public class AAEPlayerEvents {
    public static final AttributeModifier flight =
            new AttributeModifier(AdvancedAE.makeId("flight"), 1.0, AttributeModifier.Operation.ADD_VALUE);

    public static final String NO_KEY_DATA = "aae$nokey";
    public static final String UP_KEY_DATA = "aae$upkey";
    public static final String DOWN_KEY_DATA = "aae$downkey";

    @SubscribeEvent
    public static void itemAttributes(ItemAttributeModifierEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() instanceof QuantumArmorBase armor) {
            if (armor.isUpgradeEnabledAndPowered(itemStack, UpgradeType.STEP_ASSIST)) {
                int value = itemStack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(UpgradeType.STEP_ASSIST), 0);
                event.addModifier(Attributes.STEP_HEIGHT, getStepAssist(value), EquipmentSlotGroup.FEET);
            }
            if (armor.isUpgradeEnabledAndPowered(itemStack, UpgradeType.FLIGHT))
                event.addModifier(NeoForgeMod.CREATIVE_FLIGHT, flight, EquipmentSlotGroup.CHEST);
            if (armor.isUpgradeEnabledAndPowered(itemStack, UpgradeType.HP_BUFFER)) {
                int value = itemStack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(UpgradeType.HP_BUFFER), 0);
                event.addModifier(Attributes.MAX_HEALTH, getHpBuffer(value), EquipmentSlotGroup.CHEST);
            }
            if (armor.isUpgradeEnabledAndPowered(itemStack, UpgradeType.STRENGTH)) {
                int value = itemStack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(UpgradeType.STRENGTH), 0);
                event.addModifier(Attributes.ATTACK_DAMAGE, getStrengthBoost(value), EquipmentSlotGroup.CHEST);
            }
            if (armor.isUpgradeEnabledAndPowered(itemStack, UpgradeType.ATTACK_SPEED)) {
                int value = itemStack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(UpgradeType.ATTACK_SPEED), 0);
                event.addModifier(Attributes.ATTACK_SPEED, getAttackSpeedBoost(value), EquipmentSlotGroup.CHEST);
            }
            if (armor.isUpgradeEnabledAndPowered(itemStack, UpgradeType.LUCK)) {
                int value = itemStack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(UpgradeType.LUCK), 0);
                event.addModifier(Attributes.LUCK, getLuckBoost(value), EquipmentSlotGroup.HEAD);
            }
            if (armor.isUpgradeEnabledAndPowered(itemStack, UpgradeType.REACH)) {
                int value = itemStack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(UpgradeType.REACH), 0);
                var attValue = getReachBoost(value);
                event.addModifier(Attributes.BLOCK_INTERACTION_RANGE, attValue, EquipmentSlotGroup.LEGS);
                event.addModifier(Attributes.ENTITY_INTERACTION_RANGE, attValue, EquipmentSlotGroup.LEGS);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void breakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (!player.onGround()) {
            if (!Addons.APOTHIC_ENCHANTING.isLoaded()
                    || !ApoEnchPlugin.checkForEnchant(player, ApoEnchPlugin.Enchantment.STABLE_FOOTING)) {
                ItemStack armor = player.getItemBySlot(EquipmentSlot.CHEST);
                if (armor.getItem() instanceof QuantumChestplate) {
                    var newValue = Math.min(Float.MAX_VALUE, event.getOriginalSpeed() * 5);
                    event.setNewSpeed(newValue);
                }
            }
        } else if (player.isEyeInFluid(FluidTags.WATER)) {
            ItemStack armor = player.getItemBySlot(EquipmentSlot.CHEST);
            if (armor.getItem() instanceof QuantumChestplate) {
                var att = player.getAttribute(Attributes.SUBMERGED_MINING_SPEED);
                if (att != null) {
                    var value = (float) att.getValue();
                    if (value < 1) {
                        event.setNewSpeed(event.getOriginalSpeed() / value);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();

        var nv = player.getEffect(MobEffects.NIGHT_VISION);

        ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof QuantumHelmet helmet) {
                if (helmet.isUpgradeEnabledAndPowered(stack, UpgradeType.NIGHT_VISION)) {
                    if (nv == null || nv.getDuration() < 210) {
                        stack.set(AAEComponents.NIGHT_VISION_ACTIVATED, true);
                        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 210, 0, false, false, false));
                        helmet.consumeEnergy(player, stack, UpgradeType.NIGHT_VISION);
                    }
                }
            }
            if (stack.getOrDefault(AAEComponents.NIGHT_VISION_ACTIVATED, false)) {
                if (nv != null && nv.getDuration() < 210) {
                    player.removeEffect(MobEffects.NIGHT_VISION);
                    stack.remove(AAEComponents.NIGHT_VISION_ACTIVATED);
                }
            }
        }

        ItemStack bootStack = player.getItemBySlot(EquipmentSlot.FEET);
        if (bootStack.getItem() instanceof QuantumBoots boots) {
            if (player.getAbilities().flying && boots.isUpgradeEnabledAndPowered(bootStack, UpgradeType.FLIGHT_DRIFT)) {
                if (player.getPersistentData().getBoolean(NO_KEY_DATA)) {
                    var motion = player.getDeltaMovement();
                    if (motion.x != 0 || motion.z != 0) {
                        var value =
                                bootStack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(UpgradeType.FLIGHT_DRIFT), 100)
                                        / 100f;
                        player.setDeltaMovement(motion.x * value, motion.y, motion.z * value);
                        boots.consumeEnergy(player, bootStack, UpgradeType.FLIGHT_DRIFT);
                    }
                }
            }
        }
        var upKey = player.getPersistentData().getBoolean(UP_KEY_DATA);
        var downKey = player.getPersistentData().getBoolean(DOWN_KEY_DATA);
        if (upKey != downKey) {
            ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chestStack.getItem() instanceof QuantumChestplate chest) {
                var upgrade = UpgradeType.FLIGHT;
                if (player.getAbilities().flying && chest.isUpgradeEnabledAndPowered(chestStack, upgrade)) {
                    var value = upgrade.getSettings().multiplier
                            * chestStack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(upgrade), 0)
                            / 25f;
                    var direction = upKey ? 1 : -1;
                    player.moveRelative(value, new Vec3(0, direction, 0));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer
                && event.getTarget() instanceof ItemEntity item
                && ((ItemEntity) event.getTarget()).thrower != null) {
            PacketDistributor.sendToPlayer(
                    serverPlayer, new ItemTrackingPacket(item.thrower, item.getId(), item.pickupDelay));
        }
    }

    private static AttributeModifier getStepAssist(int value) {
        return new AttributeModifier(AdvancedAE.makeId("step_assist"), value, AttributeModifier.Operation.ADD_VALUE);
    }

    private static AttributeModifier getHpBuffer(int value) {
        return new AttributeModifier(AdvancedAE.makeId("hp_buffer"), value, AttributeModifier.Operation.ADD_VALUE);
    }

    private static AttributeModifier getStrengthBoost(int value) {
        return new AttributeModifier(AdvancedAE.makeId("strength_boost"), value, AttributeModifier.Operation.ADD_VALUE);
    }

    private static AttributeModifier getAttackSpeedBoost(int value) {
        return new AttributeModifier(AdvancedAE.makeId("attack_speed"), value, AttributeModifier.Operation.ADD_VALUE);
    }

    private static AttributeModifier getLuckBoost(int value) {
        return new AttributeModifier(AdvancedAE.makeId("luck"), value, AttributeModifier.Operation.ADD_VALUE);
    }

    private static AttributeModifier getReachBoost(int value) {
        return new AttributeModifier(AdvancedAE.makeId("reach_boost"), value, AttributeModifier.Operation.ADD_VALUE);
    }
}
