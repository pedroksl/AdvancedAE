package net.pedroksl.advanced_ae.events;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.pedroksl.advanced_ae.common.definitions.AAENbt;
import net.pedroksl.advanced_ae.common.items.armors.*;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.network.packet.KeysPressedPacket;
import net.pedroksl.advanced_ae.xmod.Addons;
import net.pedroksl.advanced_ae.xmod.apoth.ApoEnchPlugin;

public class AAEPlayerEvents {
    public static final String NO_KEY_DATA = "aae$nokey";
    public static final String UP_KEY_DATA = "aae$upkey";
    public static final String DOWN_KEY_DATA = "aae$downkey";

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(AAEPlayerEvents::breakSpeed);
        MinecraftForge.EVENT_BUS.addListener(AAEPlayerEvents::playerRender);
        MinecraftForge.EVENT_BUS.addListener(AAEPlayerEvents::playerTickStart);
        MinecraftForge.EVENT_BUS.addListener(AAEPlayerEvents::playerTickEnd);
    }

    @SuppressWarnings("deprecation")
    public static void breakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (!player.onGround()) {
            if (!Addons.APOTHIC_ENCHANTING.isLoaded()
                    || !ApoEnchPlugin.checkForEnchant(player, ApoEnchPlugin.Enchantment.STABLE_FOOTING)) {
                ItemStack armor = player.getItemBySlot(EquipmentSlot.CHEST);
                if (armor.getItem() instanceof QuantumChestplate) {
                    var newValue = Math.max(event.getOriginalSpeed(), event.getOriginalSpeed() * 5);
                    event.setNewSpeed(newValue);
                }
            }
        } else if (player.isEyeInFluid(FluidTags.WATER)) {
            ItemStack armor = player.getItemBySlot(EquipmentSlot.CHEST);
            if (armor.getItem() instanceof QuantumChestplate) {
                var att = player.getAttribute(ForgeMod.SWIM_SPEED.get());
                if (att != null) {
                    var value = (float) att.getValue();
                    if (value < 1) {
                        event.setNewSpeed(event.getOriginalSpeed() / value);
                    }
                }
            }
        }
    }

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

    public static void playerTickStart(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Player player = event.player;
        if (player instanceof ServerPlayer serverPlayer) {
            var nv = serverPlayer.getEffect(MobEffects.NIGHT_VISION);

            ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
            if (stack.getItem() instanceof QuantumHelmet helmet) {
                if (helmet.isUpgradeEnabledAndPowered(stack, UpgradeType.NIGHT_VISION, serverPlayer.level())) {
                    if (nv == null || nv.getDuration() < 210) {
                        stack.getOrCreateTag().putBoolean(AAENbt.NIGHT_VISION_ACTIVATED, true);
                        serverPlayer.addEffect(
                                new MobEffectInstance(MobEffects.NIGHT_VISION, 210, 0, false, false, false));
                        helmet.consumeEnergy(player, stack, UpgradeType.NIGHT_VISION);
                    }
                }
            }
            if (stack.getOrCreateTag().getBoolean(AAENbt.NIGHT_VISION_ACTIVATED)) {
                if (nv != null && nv.getDuration() < 210) {
                    serverPlayer.removeEffect(MobEffects.NIGHT_VISION);
                    stack.removeTagKey(AAENbt.NIGHT_VISION_ACTIVATED);
                }
            }
        } else {
            ItemStack bootStack = player.getItemBySlot(EquipmentSlot.FEET);
            if (bootStack.getItem() instanceof QuantumBoots boots) {
                var upgrade = UpgradeType.FLIGHT_DRIFT;
                if (player.getAbilities().flying && boots.isUpgradeEnabledAndPowered(bootStack, upgrade)) {
                    if (player.getPersistentData().getBoolean(NO_KEY_DATA)) {
                        var motion = player.getDeltaMovement();
                        if (motion.x != 0 || motion.z != 0) {
                            var value = boots.getUpgradeValue(bootStack, upgrade, 100) / 100f;
                            player.setDeltaMovement(motion.x * value, motion.y, motion.z * value);
                            boots.consumeEnergy(player, bootStack, upgrade);
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
                        var value =
                                upgrade.getSettings().multiplier * chest.getUpgradeValue(chestStack, upgrade, 0) / 50;
                        var direction = upKey ? 1 : -1;
                        player.moveRelative(value, new Vec3(0, direction, 0));
                    }
                }
            }
        }
    }

    public static void playerTickEnd(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
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
                        AAENetworkHandler.INSTANCE.sendToServer(new KeysPressedPacket(NO_KEY_DATA, noKey));
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
                    AAENetworkHandler.INSTANCE.sendToServer(new KeysPressedPacket(DOWN_KEY_DATA, downKey));
                    player.getPersistentData().putBoolean(DOWN_KEY_DATA, downKey);
                }

                var upKey = options.keyJump.isDown();
                if (player.getPersistentData().getBoolean(UP_KEY_DATA) != upKey) {
                    // Send packet to server if data on player is different
                    AAENetworkHandler.INSTANCE.sendToServer(new KeysPressedPacket(UP_KEY_DATA, upKey));
                    player.getPersistentData().putBoolean(UP_KEY_DATA, upKey);
                }
            }
        }
    }
}
