package net.pedroksl.advanced_ae.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.helpers.KeysPressed;
import net.pedroksl.advanced_ae.common.items.armors.*;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.network.packet.KeysPressedPacket;

public class AAEClientPlayerEvents {
    public static final AttributeModifier flight =
            new AttributeModifier(AdvancedAE.makeId("flight"), 1.0, AttributeModifier.Operation.ADD_VALUE);

    @SubscribeEvent
    public static void playerRender(RenderPlayerEvent.Pre event) {
        // TODO Hide player bones
        //        var renderer = event.getRenderer();
        //        var model = renderer.getModel();
        //
        //        ItemStack helmetStack = player.getItemBySlot(EquipmentSlot.HEAD);
        //        if (helmetStack.getItem() instanceof QuantumHelmet item && item.isVisible(helmetStack)) {
        //            model.hat.visible = false;
        //        }
        //
        //        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        //        if (chestStack.getItem() instanceof QuantumChestplate item && item.isVisible(chestStack)) {
        //            model.leftSleeve.visible = false;
        //            model.rightSleeve.visible = false;
        //            model.jacket.visible = false;
        //
        //            model.leftArm.visible = false;
        //            model.rightArm.visible = false;
        //        }
        //
        //        ItemStack leggingsStack = player.getItemBySlot(EquipmentSlot.LEGS);
        //        ItemStack bootsStack = player.getItemBySlot(EquipmentSlot.FEET);
        //        if (leggingsStack.getItem() instanceof QuantumLeggings item && item.isVisible(leggingsStack)) {
        //            model.leftPants.visible = false;
        //            model.rightPants.visible = false;
        //
        //            if (bootsStack.getItem() instanceof QuantumBoots item2 && item2.isVisible(bootsStack)) {
        //                model.leftLeg.visible = false;
        //                model.rightLeg.visible = false;
        //            }
        //        }
    }

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        boolean shouldUpdateServer = false;
        var keys = new KeysPressed(player.getPersistentData().getByteOr(KeysPressed.KEYS_PRESSED, (byte) 0));
        ItemStack bootStack = player.getItemBySlot(EquipmentSlot.FEET);
        if (bootStack.getItem() instanceof QuantumBoots boots) {
            if (boots.isUpgradeEnabledAndPowered(bootStack, UpgradeType.FLIGHT_DRIFT)) {
                var options = Minecraft.getInstance().options;
                var noKey = !options.keyUp.isDown()
                        && !options.keyRight.isDown()
                        && !options.keyDown.isDown()
                        && !options.keyLeft.isDown();

                if (keys.noKey != noKey) {
                    // Send packet to server if data on player is different
                    keys.noKey = noKey;
                    shouldUpdateServer = true;
                    player.getPersistentData().putByte(KeysPressed.KEYS_PRESSED, keys.toByte());
                }
            }
        }

        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestStack.getItem() instanceof QuantumChestplate) {
            var options = Minecraft.getInstance().options;
            var downKey = options.keyShift.isDown();
            var upKey = options.keyJump.isDown();

            if (keys.downKey != downKey || keys.upKey != upKey) {
                // Send packet to server if data on player is different
                keys.downKey = downKey;
                keys.upKey = upKey;
                player.getPersistentData().putByte(KeysPressed.KEYS_PRESSED, keys.toByte());
            }
        }

        if (shouldUpdateServer) {
            ClientPacketDistributor.sendToServer(new KeysPressedPacket(keys));
        }
    }
}
