package net.pedroksl.advanced_ae.common.items.armors;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.client.renderer.QuantumArmorRenderer;
import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.helpers.PortableCellWorkbenchMenuHost;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.network.packet.MenuSelectionPacket;
import net.pedroksl.advanced_ae.xmod.iris.IrisPlugin;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.MenuOpener;
import appeng.menu.locator.ItemMenuHostLocator;

import software.bernie.geckolib.animatable.client.GeoRenderProvider;

public class QuantumHelmet extends QuantumArmorBase {
    private static final double MAX_POWER_STORAGE = 200000000;

    private static final String MENU_TYPE = "aae$menutype";
    private static final int STANDARD_MENU = 0;
    private static final int WORKBENCH_MENU = 1;

    public QuantumHelmet(Properties properties) {
        super(AAEMaterials.QUANTUM_ALLOY.holder(), Type.HELMET, properties, () -> MAX_POWER_STORAGE);

        registerUpgrades(
                UpgradeType.WATER_BREATHING,
                UpgradeType.AUTO_FEED,
                UpgradeType.AUTO_STOCK,
                UpgradeType.MAGNET,
                UpgradeType.LUCK,
                UpgradeType.NIGHT_VISION,
                UpgradeType.CHARGING,
                UpgradeType.WORKBENCH);
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
                var visible = IrisPlugin.isShaderPackInUse();
                quantumRenderer.setBoneVisible(QuantumArmorRenderer.HUD_BONE, visible);
            }
        }
    }

    @Override
    public boolean openFromEquipmentSlot(Player player, ItemMenuHostLocator locator, boolean returningFromSubmenu) {
        var is = locator.locateItem(player);
        if (!player.level().isClientSide() && checkPreconditions(is)) {
            player.getPersistentData().putInt(MENU_TYPE, STANDARD_MENU);
            PacketDistributor.sendToPlayer(((ServerPlayer) player), new MenuSelectionPacket(MENU_TYPE, STANDARD_MENU));
        }
        return super.openFromEquipmentSlot(player, locator, returningFromSubmenu);
    }

    public boolean openPortableWorkbench(Player player, ItemMenuHostLocator locator) {
        return openPortableWorkbench(player, locator, false);
    }

    public boolean openPortableWorkbench(Player player, ItemMenuHostLocator locator, boolean returningFromSubmenu) {
        var is = locator.locateItem(player);

        if (!player.level().isClientSide() && checkPreconditions(is)) {
            if (((QuantumHelmet) is.getItem()).isUpgradeEnabled(is, UpgradeType.WORKBENCH)) {
                player.getPersistentData().putInt(MENU_TYPE, WORKBENCH_MENU);
                PacketDistributor.sendToPlayer(
                        ((ServerPlayer) player), new MenuSelectionPacket(MENU_TYPE, WORKBENCH_MENU));
                return MenuOpener.open(AAEMenus.PORTABLE_WORKBENCH, player, locator, returningFromSubmenu);
            } else {
                var id = Component.translatable(
                        UpgradeType.WORKBENCH.item().asItem().getDescriptionId());
                player.displayClientMessage(AAEText.UpgradeNotInstalledMessage.text(id), true);
            }
        }
        return false;
    }

    @Override
    public ItemMenuHost<?> getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        if (player.getPersistentData().contains(MENU_TYPE)
                && player.getPersistentData().getInt(MENU_TYPE) == WORKBENCH_MENU) {
            player.getPersistentData().remove(MENU_TYPE);
            return new PortableCellWorkbenchMenuHost(this, player, locator);
        }
        player.getPersistentData().remove(MENU_TYPE);
        return super.getMenuHost(player, locator, hitResult);
    }
}
