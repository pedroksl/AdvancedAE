package net.pedroksl.advanced_ae.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.pedroksl.advanced_ae.common.items.armors.QuantumBoots;
import net.pedroksl.advanced_ae.common.items.armors.QuantumChestplate;
import net.pedroksl.advanced_ae.common.items.armors.QuantumHelmet;
import net.pedroksl.advanced_ae.common.items.armors.QuantumLeggings;

@OnlyIn(Dist.CLIENT)
public class AAEClientPlayerEvents {

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(AAEClientPlayerEvents::playerRender);
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
}
