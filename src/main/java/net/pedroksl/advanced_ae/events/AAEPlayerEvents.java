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
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.items.armors.*;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.network.packet.KeysPressedPacket;

public class AAEPlayerEvents {
    public static final AttributeModifier flight =
            new AttributeModifier(AdvancedAE.makeId("flight"), 1.0, AttributeModifier.Operation.ADD_VALUE);

    public static final String NO_KEY_DATA = "aae$nokey";
    public static final String UP_KEY_DATA = "aae$upkey";
    public static final String DOWN_KEY_DATA = "aae$downkey";

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
                var attValue = getReachBoost(value);
                event.addModifier(Attributes.BLOCK_INTERACTION_RANGE, attValue, EquipmentSlotGroup.LEGS);
                event.addModifier(Attributes.ENTITY_INTERACTION_RANGE, attValue, EquipmentSlotGroup.LEGS);
            }
        }
    }

    @SubscribeEvent
    public static void BreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (!player.onGround()) {
            ItemStack armor = player.getItemBySlot(EquipmentSlot.CHEST);
            if (armor.getItem() instanceof QuantumChestplate) {
                event.setNewSpeed(event.getOriginalSpeed() * 5);
            }
        } else if (player.isInWaterOrBubble()) {
            event.setNewSpeed(event.getOriginalSpeed() * 5);
        }
    }

    @SubscribeEvent
    public static void playerRender(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer)) {
            var renderer = event.getRenderer();
            var model = renderer.getModel();

            ItemStack helmetStack = player.getItemBySlot(EquipmentSlot.HEAD);
            if (helmetStack.getItem() instanceof QuantumHelmet) {
                model.hat.visible = false;
            }

            ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chestStack.getItem() instanceof QuantumChestplate) {
                model.leftSleeve.visible = false;
                model.rightSleeve.visible = false;
                model.jacket.visible = false;

                model.leftArm.visible = false;
                model.rightArm.visible = false;
            }

            ItemStack leggingsStack = player.getItemBySlot(EquipmentSlot.LEGS);
            ItemStack bootsStack = player.getItemBySlot(EquipmentSlot.FEET);
            if (leggingsStack.getItem() instanceof QuantumLeggings) {
                model.leftPants.visible = false;
                model.rightPants.visible = false;

                if (bootsStack.getItem() instanceof QuantumBoots) {
                    model.leftLeg.visible = false;
                    model.rightLeg.visible = false;
                }
            }
        }
    }

    //    @SubscribeEvent
    //    public static void playerArmRender(RenderArmEvent event) {
    //        Player player = event.getPlayer();
    //
    //        ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
    //        if (stack.getItem() instanceof QuantumChestplate chest) {
    //            var renderProvider = chest.getAnimatableInstanceCache().getRenderProvider();
    //            if (renderProvider instanceof GeoRenderProvider provider) {
    //                var renderer = provider.getGeoArmorRenderer(player, stack, EquipmentSlot.CHEST, null);
    //                if (renderer instanceof QuantumArmorRenderer quantumRenderer) {
    //                    var boneName = event.getArm() == HumanoidArm.RIGHT ? QuantumArmorRenderer.RIGHT_ARM :
    //                            QuantumArmorRenderer.LEFT_ARM;
    //                    var model = quantumRenderer.getGeoModel();
    //                    var renderType = RenderType.armorCutoutNoCull(model.getTextureResource(chest));
    //
    //                    var poseStack = event.getPoseStack();
    //                    poseStack.pushPose();
    //                    poseStack.last().pose().rotate((float) Math.PI,0, 0, 1);
    //                    poseStack.translate(-1, 0.5f, 0);
    //                    poseStack.translate(-0.3125F, -0.125F, 0);
    //                    var source = event.getMultiBufferSource();
    //                    var buffer = source.getBuffer(renderType);
    //                    quantumRenderer.renderChildBones(boneName, poseStack, chest, renderType,
    //                            source, buffer, false, 0, event.getPackedLight(), OverlayTexture.NO_OVERLAY, 0);
    //                    poseStack.popPose();
    //
    //                    event.setCanceled(true);
    //                }
    //            }
    //        }
    //    }

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
            ItemStack bootStack = player.getItemBySlot(EquipmentSlot.FEET);
            if (bootStack.getItem() instanceof QuantumBoots boots) {
                if (player.getAbilities().flying
                        && boots.isUpgradeEnabledAndPowered(bootStack, UpgradeType.FLIGHT_DRIFT)) {
                    if (player.getPersistentData().getBoolean(NO_KEY_DATA)) {
                        var motion = player.getDeltaMovement();
                        if (motion.x != 0 || motion.z != 0) {
                            var value = bootStack.getOrDefault(
                                            AAEComponents.UPGRADE_VALUE.get(UpgradeType.FLIGHT_DRIFT), 100)
                                    / 100f;
                            player.setDeltaMovement(motion.x * value, motion.y, motion.z * value);
                            boots.consumeEnergy(bootStack, UpgradeType.FLIGHT_DRIFT);
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
    }

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer)) {
            ItemStack bootStack = player.getItemBySlot(EquipmentSlot.FEET);
            if (bootStack.getItem() instanceof QuantumBoots boots) {
                if (boots.isUpgradeEnabledAndPowered(bootStack, UpgradeType.FLIGHT_DRIFT)) {
                    var options = Minecraft.getInstance().options;
                    var noKey = !options.keyUp.isDown()
                            && !options.keyRight.isDown()
                            && !options.keyDown.isDown()
                            && !options.keyLeft.isDown();
                    if (player.getPersistentData().getBoolean(NO_KEY_DATA) != noKey) {
                        // Send packet to server if data on player is different
                        PacketDistributor.sendToServer(new KeysPressedPacket(NO_KEY_DATA, noKey));
                        player.getPersistentData().putBoolean(NO_KEY_DATA, noKey);
                    }
                }
            }

            ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chestStack.getItem() instanceof QuantumChestplate) {
                var options = Minecraft.getInstance().options;
                var downKey = options.keyShift.isDown();
                if (player.getPersistentData().getBoolean(DOWN_KEY_DATA) != downKey) {
                    // Send packet to server if data on player is different
                    PacketDistributor.sendToServer(new KeysPressedPacket(DOWN_KEY_DATA, downKey));
                    player.getPersistentData().putBoolean(DOWN_KEY_DATA, downKey);
                }

                var upKey = options.keyJump.isDown();
                if (player.getPersistentData().getBoolean(UP_KEY_DATA) != upKey) {
                    // Send packet to server if data on player is different
                    PacketDistributor.sendToServer(new KeysPressedPacket(UP_KEY_DATA, upKey));
                    player.getPersistentData().putBoolean(UP_KEY_DATA, upKey);
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
