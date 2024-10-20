package net.pedroksl.advanced_ae.events;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.advanced_ae.common.items.armors.QuantumBoots;
import net.pedroksl.advanced_ae.common.items.armors.QuantumChestplate;
import net.pedroksl.advanced_ae.common.items.armors.QuantumHelmet;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.network.packet.NoKeyPressedPacket;

public class AAEPlayerEvents {
    public static final AttributeModifier flight =
            new AttributeModifier(AdvancedAE.makeId("flight"), 1.0, AttributeModifier.Operation.ADD_VALUE);

    public static final String NO_KEY_DATA = "aae$nokey";

    @SubscribeEvent
    public static void ItemAttributes(ItemAttributeModifierEvent event) {
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
                event.addModifier(Attributes.BLOCK_INTERACTION_RANGE, getReachBoost(value), EquipmentSlotGroup.LEGS);
            }
        }
    }

    @SubscribeEvent
    public static void BreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player.getAbilities().flying) {
            ItemStack armor = player.getItemBySlot(EquipmentSlot.CHEST);
            if (armor.getItem() instanceof QuantumChestplate chestplate) {
                if (chestplate.isUpgradeEnabledAndPowered(armor, UpgradeType.FLIGHT)) {
                    event.setNewSpeed(event.getOriginalSpeed() * 5);
                    chestplate.consumeEnergy(armor, UpgradeType.FLIGHT);
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            var nv = serverPlayer.getEffect(MobEffects.NIGHT_VISION);

            ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
            if (stack.getItem() instanceof QuantumHelmet helmet) {
                if (helmet.isUpgradeEnabledAndPowered(stack, UpgradeType.NIGHT_VISION, serverPlayer.level())) {
                    if (nv == null || nv.getDuration() < 210) {
                        stack.set(AAEComponents.NIGHT_VISION_ACTIVATED, true);
                        serverPlayer.addEffect(
                                new MobEffectInstance(MobEffects.NIGHT_VISION, 210, 0, false, false, false));
                        helmet.consumeEnergy(stack, UpgradeType.NIGHT_VISION);
                    }
                }
            }
            if (stack.getOrDefault(AAEComponents.NIGHT_VISION_ACTIVATED, false)) {
                if (nv != null && nv.getDuration() < 210) {
                    serverPlayer.removeEffect(MobEffects.NIGHT_VISION);
                    stack.remove(AAEComponents.NIGHT_VISION_ACTIVATED);
                }
            }
        } else {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.FEET);
            if (stack.getItem() instanceof QuantumBoots boots) {
                if (player.getAbilities().flying && boots.isUpgradeEnabledAndPowered(stack, UpgradeType.FLIGHT_DRIFT)) {
                    if (player.getPersistentData().getBoolean(NO_KEY_DATA)) {
                        var motion = player.getDeltaMovement();
                        if (motion.x != 0 || motion.z != 0) {
                            var value =
                                    stack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(UpgradeType.FLIGHT_DRIFT), 100)
                                            / 100f;
                            player.setDeltaMovement(motion.x * value, motion.y, motion.z * value);
                            boots.consumeEnergy(stack, UpgradeType.FLIGHT_DRIFT);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer)) {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.FEET);
            if (stack.getItem() instanceof QuantumBoots boots) {
                if (boots.isUpgradeEnabledAndPowered(stack, UpgradeType.FLIGHT_DRIFT)) {
                    var options = Minecraft.getInstance().options;
                    var noKey = !options.keyUp.isDown()
                            && !options.keyRight.isDown()
                            && !options.keyDown.isDown()
                            && !options.keyLeft.isDown();
                    if (player.getPersistentData().getBoolean(NO_KEY_DATA) != noKey) {
                        // Send packet to server if data on player is different
                        PacketDistributor.sendToServer(new NoKeyPressedPacket(noKey));
                        player.getPersistentData().putBoolean(NO_KEY_DATA, noKey);
                    }
                }
            }
        }
    }

    private static AttributeModifier getStepAssist(int value) {
        return new AttributeModifier(AdvancedAE.makeId("step_assist"), value, AttributeModifier.Operation.ADD_VALUE);
    }

    private static AttributeModifier getHpBuffer(int value) {
        return new AttributeModifier(AdvancedAE.makeId("hp_buffer"), value, AttributeModifier.Operation.ADD_VALUE);
    }

    private static AttributeModifier getStrengthBoost(int value) {
        return new AttributeModifier(AdvancedAE.makeId("hp_buffer"), value, AttributeModifier.Operation.ADD_VALUE);
    }

    private static AttributeModifier getAttackSpeedBoost(int value) {
        return new AttributeModifier(AdvancedAE.makeId("hp_buffer"), value, AttributeModifier.Operation.ADD_VALUE);
    }

    private static AttributeModifier getLuckBoost(int value) {
        return new AttributeModifier(AdvancedAE.makeId("luck"), value, AttributeModifier.Operation.ADD_VALUE);
    }

    private static AttributeModifier getReachBoost(int value) {
        return new AttributeModifier(AdvancedAE.makeId("hp_buffer"), value, AttributeModifier.Operation.ADD_VALUE);
    }
}
