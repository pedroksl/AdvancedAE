package net.pedroksl.advanced_ae.common.items.armors;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.helpers.PortableCellWorkbenchMenuHost;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.network.packet.MenuSelectionPacket;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.MenuOpener;
import appeng.menu.locator.ItemMenuHostLocator;

public class QuantumHelmet extends QuantumArmorBase {
    private static final double MAX_POWER_STORAGE = 200000000;

    public QuantumHelmet(Properties properties) {
        super(ArmorType.HELMET, properties, () -> MAX_POWER_STORAGE);

        registerUpgrades(
                UpgradeType.WATER_BREATHING,
                UpgradeType.AUTO_FEED,
                UpgradeType.AUTO_STOCK,
                UpgradeType.MAGNET,
                UpgradeType.LUCK,
                UpgradeType.NIGHT_VISION,
                UpgradeType.CHARGING,
                UpgradeType.WORKBENCH,
                UpgradeType.CAMO);
    }

    @Override
    public boolean openFromEquipmentSlot(Player player, ItemMenuHostLocator locator, boolean returningFromSubmenu) {
        var is = locator.locateItem(player);
        if (!player.level().isClientSide() && checkPreconditions(is)) {
            player.getPersistentData().putInt(MENU_TYPE, MenuId.STANDARD.id);
            PacketDistributor.sendToPlayer(
                    ((ServerPlayer) player), new MenuSelectionPacket(MENU_TYPE, MenuId.STANDARD.id));
        }
        return super.openFromEquipmentSlot(player, locator, returningFromSubmenu);
    }

    public boolean openPortableWorkbench(Player player, ItemMenuHostLocator locator) {
        return openPortableWorkbench(player, locator, false);
    }

    public boolean openPortableWorkbench(Player player, ItemMenuHostLocator locator, boolean returningFromSubmenu) {
        var is = locator.locateItem(player);

        if (player instanceof ServerPlayer serverPlayer && checkPreconditions(is)) {
            if (((QuantumHelmet) is.getItem()).isUpgradeEnabled(is, UpgradeType.WORKBENCH)) {
                player.getPersistentData().putInt(MENU_TYPE, MenuId.WORKBENCH.id);
                PacketDistributor.sendToPlayer(serverPlayer, new MenuSelectionPacket(MENU_TYPE, MenuId.WORKBENCH.id));
                return MenuOpener.open(AAEMenus.PORTABLE_WORKBENCH.get(), player, locator, returningFromSubmenu);
            } else {
                var id = Component.translatable(
                        UpgradeType.WORKBENCH.item().asItem().getDescriptionId());

                player.sendOverlayMessage(AAEText.UpgradeNotInstalledMessage.text(id));
            }
        }
        return false;
    }

    @Override
    public ItemMenuHost<?> getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        if (player.getPersistentData().contains(MENU_TYPE)
                && player.getPersistentData().getIntOr(MENU_TYPE, 0) == MenuId.WORKBENCH.id) {
            player.getPersistentData().remove(MENU_TYPE);
            return new PortableCellWorkbenchMenuHost(this, player, locator);
        }
        player.getPersistentData().remove(MENU_TYPE);
        return super.getMenuHost(player, locator, hitResult);
    }
}
