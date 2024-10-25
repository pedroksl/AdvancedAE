package net.pedroksl.advanced_ae.common.items.armors;

import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.client.renderer.QuantumArmorRenderer;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.xmod.Addons;

import software.bernie.geckolib.animatable.client.GeoRenderProvider;

public class QuantumHelmet extends QuantumArmorBase {

    private static final double MAX_POWER_STORAGE = 200000000;

    public QuantumHelmet(Properties properties) {
        super(AAEMaterials.QUANTUM_ALLOY.holder(), Type.HELMET, properties, () -> MAX_POWER_STORAGE);

        registerUpgrades(
                UpgradeType.WATER_BREATHING,
                UpgradeType.AUTO_FEED,
                UpgradeType.AUTO_STOCK,
                UpgradeType.MAGNET,
                UpgradeType.LUCK,
                UpgradeType.NIGHT_VISION,
                UpgradeType.CHARGING);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (slotId == Inventory.INVENTORY_SIZE + EquipmentSlot.HEAD.getIndex()) {
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
            var renderer = provider.getGeoArmorRenderer(player, stack, EquipmentSlot.HEAD, null);
            if (renderer instanceof QuantumArmorRenderer quantumRenderer) {
                var visible = stack.has(AAEComponents.UPGRADE_TOGGLE.get(UpgradeType.AUTO_FEED));

                if (!Addons.IRIS.isLoaded()) {
                    visible = false;
                } else {
                    visible &= IrisApi.getInstance().isShaderPackInUse();
                }

                quantumRenderer.setBoneVisible(QuantumArmorRenderer.HUD_BONE, visible);
            }
        }
    }
}
