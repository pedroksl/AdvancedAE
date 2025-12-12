package net.pedroksl.advanced_ae.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.common.items.armors.QuantumBoots;
import net.pedroksl.advanced_ae.common.items.armors.QuantumChestplate;
import net.pedroksl.advanced_ae.common.items.armors.QuantumHelmet;
import net.pedroksl.advanced_ae.common.items.armors.QuantumLeggings;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.events.AAEPlayerEvents;
import net.pedroksl.advanced_ae.network.packet.KeysPressedPacket;

public class AAEPlayerClientEvents {

    @SubscribeEvent
    public static void playerRender(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        var renderer = event.getRenderer();
        var model = renderer.getModel();

        ItemStack helmetStack = player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmetStack.getItem() instanceof QuantumHelmet item && item.isVisible(helmetStack)) {
            model.hat.visible = false;
        }

        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestStack.getItem() instanceof QuantumChestplate item && item.isVisible(chestStack)) {
            model.leftSleeve.visible = false;
            model.rightSleeve.visible = false;
            model.jacket.visible = false;

            model.leftArm.visible = false;
            model.rightArm.visible = false;
        }

        ItemStack leggingsStack = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack bootsStack = player.getItemBySlot(EquipmentSlot.FEET);
        if (leggingsStack.getItem() instanceof QuantumLeggings item && item.isVisible(leggingsStack)) {
            model.leftPants.visible = false;
            model.rightPants.visible = false;

            if (bootsStack.getItem() instanceof QuantumBoots item2 && item2.isVisible(bootsStack)) {
                model.leftLeg.visible = false;
                model.rightLeg.visible = false;
            }
        }
    }

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            return;
        }

        var options = Minecraft.getInstance().options;

        ItemStack bootStack = player.getItemBySlot(EquipmentSlot.FEET);
        if (bootStack.getItem() instanceof QuantumBoots boots
                && boots.isUpgradeEnabledAndPowered(bootStack, UpgradeType.FLIGHT_DRIFT)) {
            var noKey = !options.keyUp.isDown()
                    && !options.keyRight.isDown()
                    && !options.keyDown.isDown()
                    && !options.keyLeft.isDown();
            if (player.getPersistentData().getBoolean(AAEPlayerEvents.NO_KEY_DATA) != noKey) {
                PacketDistributor.sendToServer(new KeysPressedPacket(AAEPlayerEvents.NO_KEY_DATA, noKey));
                player.getPersistentData().putBoolean(AAEPlayerEvents.NO_KEY_DATA, noKey);
            }
        }

        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestStack.getItem() instanceof QuantumChestplate) {
            var downKey = options.keyShift.isDown();
            if (player.getPersistentData().getBoolean(AAEPlayerEvents.DOWN_KEY_DATA) != downKey) {
                PacketDistributor.sendToServer(new KeysPressedPacket(AAEPlayerEvents.DOWN_KEY_DATA, downKey));
                player.getPersistentData().putBoolean(AAEPlayerEvents.DOWN_KEY_DATA, downKey);
            }

            var upKey = options.keyJump.isDown();
            if (player.getPersistentData().getBoolean(AAEPlayerEvents.UP_KEY_DATA) != upKey) {
                PacketDistributor.sendToServer(new KeysPressedPacket(AAEPlayerEvents.UP_KEY_DATA, upKey));
                player.getPersistentData().putBoolean(AAEPlayerEvents.UP_KEY_DATA, upKey);
            }
        }
    }
}
