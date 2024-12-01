package net.pedroksl.advanced_ae.common.items.armors;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pedroksl.advanced_ae.client.renderer.QuantumArmorRenderer;
import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.helpers.PortableCellWorkbenchMenuHost;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.network.packet.MenuSelectionPacket;
import net.pedroksl.advanced_ae.xmod.iris.IrisPlugin;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;

public class QuantumHelmet extends QuantumArmorBase {
    private static final double MAX_POWER_STORAGE = 200000000;

    public QuantumHelmet(Properties properties) {
        super(AAEMaterials.QUANTUM_ALLOY, Type.HELMET, properties, () -> MAX_POWER_STORAGE);

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
        if (slotId == EquipmentSlot.HEAD.getIndex()) {
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
        var renderer = IClientItemExtensions.of(item).getHumanoidArmorModel(player, stack, EquipmentSlot.HEAD, null);
        if (renderer instanceof QuantumArmorRenderer quantumRenderer) {
            var visible = IrisPlugin.isShaderPackInUse();
            quantumRenderer.setBoneVisible(QuantumArmorRenderer.HUD_BONE, visible);
        }
    }

    @Override
    public boolean openFromEquipmentSlot(
            Player player, int inventorySlot, ItemStack stack, boolean returningFromSubmenu) {
        if (player instanceof ServerPlayer serverPlayer && checkPreconditions(stack)) {
            player.getPersistentData().putInt(MENU_TYPE, MenuId.STANDARD.id);
            AAENetworkHandler.INSTANCE.sendTo(new MenuSelectionPacket(MENU_TYPE, MenuId.STANDARD.id), serverPlayer);
        }
        return super.openFromEquipmentSlot(player, inventorySlot, stack, returningFromSubmenu);
    }

    public boolean openPortableWorkbench(Player player, int inventorySlot, ItemStack stack) {
        return openPortableWorkbench(player, inventorySlot, stack, false);
    }

    public boolean openPortableWorkbench(
            Player player, int inventorySlot, ItemStack stack, boolean returningFromSubmenu) {
        if (player instanceof ServerPlayer serverPlayer && checkPreconditions(stack)) {
            if (((QuantumHelmet) stack.getItem()).isUpgradeEnabled(stack, UpgradeType.WORKBENCH)) {
                player.getPersistentData().putInt(MENU_TYPE, MenuId.WORKBENCH.id);
                AAENetworkHandler.INSTANCE.sendTo(
                        new MenuSelectionPacket(MENU_TYPE, MenuId.WORKBENCH.id), serverPlayer);
                return MenuOpener.open(
                        AAEMenus.PORTABLE_WORKBENCH,
                        player,
                        MenuLocators.forInventorySlot(inventorySlot),
                        returningFromSubmenu);
            } else {
                var id = Component.translatable(
                        UpgradeType.WORKBENCH.item().asItem().getDescriptionId());
                player.displayClientMessage(AAEText.UpgradeNotInstalledMessage.text(id), true);
            }
        }
        return false;
    }

    @Override
    public ItemMenuHost getMenuHost(Player player, int inventorySlot, ItemStack stack, @Nullable BlockPos pos) {
        if (player.getPersistentData().contains(MENU_TYPE)
                && player.getPersistentData().getInt(MENU_TYPE) == MenuId.WORKBENCH.id) {
            player.getPersistentData().remove(MENU_TYPE);
            return new PortableCellWorkbenchMenuHost(player, inventorySlot, stack);
        }
        player.getPersistentData().remove(MENU_TYPE);
        return super.getMenuHost(player, inventorySlot, stack, pos);
    }
}
