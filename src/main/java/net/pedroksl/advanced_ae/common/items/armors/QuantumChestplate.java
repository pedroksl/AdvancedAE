package net.pedroksl.advanced_ae.common.items.armors;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.client.renderer.QuantumArmorRenderer;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

public class QuantumChestplate extends QuantumArmorBase implements GeoItem {

    private static final double MAX_POWER_STORAGE = 300000000;

    public QuantumChestplate(Properties properties) {
        super(AAEMaterials.QUANTUM_ALLOY.holder(), Type.CHESTPLATE, properties, () -> MAX_POWER_STORAGE);

        registerUpgrades(
                UpgradeType.FLIGHT,
                UpgradeType.HP_BUFFER,
                UpgradeType.LAVA_IMMUNITY,
                UpgradeType.REGENERATION,
                UpgradeType.STRENGTH,
                UpgradeType.ATTACK_SPEED);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (slotId == Inventory.INVENTORY_SIZE + EquipmentSlot.CHEST.getIndex()) {
            if (entity instanceof Player player) {
                if (!getPassiveUpgrades(stack).isEmpty()) {
                    tickUpgrades(level, player, stack);
                }

                toggleBoneVisibilities(stack, player);
            }
        }
    }

    private void toggleBoneVisibilities(ItemStack stack, Player player) {
        var item = (QuantumArmorBase) stack.getItem();
        var renderProvider = item.getRenderProvider();
        if (renderProvider instanceof GeoRenderProvider provider) {
            var renderer = provider.getGeoArmorRenderer(player, stack, EquipmentSlot.CHEST, null);
            if (renderer instanceof QuantumArmorRenderer quantumRenderer) {
                var visible = stack.has(AAEComponents.UPGRADE_TOGGLE.get(UpgradeType.STRENGTH));
                quantumRenderer.setBoneVisible(QuantumArmorRenderer.LEFT_BLADE_BONE, visible);
                quantumRenderer.setBoneVisible(QuantumArmorRenderer.RIGHT_BLADE_BONE, visible);
            }
        }
    }
}
