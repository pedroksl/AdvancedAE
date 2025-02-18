package net.pedroksl.advanced_ae.common.helpers;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.items.armors.IGridLinkedItem;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;

import appeng.api.implementations.blockentities.IWirelessAccessPoint;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.storage.ISubMenuHost;
import appeng.blockentity.networking.WirelessAccessPointBlockEntity;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;

public class PickCraftMenuHost<T extends QuantumArmorBase> extends ItemMenuHost implements ISubMenuHost, IActionHost {

    @Nullable
    private IWirelessAccessPoint currentAccessPoint;

    public PickCraftMenuHost(Player player, int inventorySlot, ItemStack stack) {
        super(player, inventorySlot, stack);

        updateConnectedAccessPoint();
    }

    private IGrid getLinkedGrid(ItemStack stack) {
        if (stack.getItem() instanceof IGridLinkedItem item) {
            return item.getLinkedGrid(stack, getPlayer().level());
        }
        return null;
    }

    public void tick() {
        updateConnectedAccessPoint();
        drainPower();
    }

    protected void updateConnectedAccessPoint() {
        this.currentAccessPoint = null;

        var targetGrid = getLinkedGrid(getItemStack());
        if (targetGrid != null) {
            for (var wap : targetGrid.getMachines(WirelessAccessPointBlockEntity.class)) {
                if (wap.isActive()) {
                    this.currentAccessPoint = wap;
                    break;
                }
            }
        }
    }

    @Override
    public @Nullable IGridNode getActionableNode() {
        if (this.currentAccessPoint != null) {
            return this.currentAccessPoint.getActionableNode();
        }
        return null;
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        ((AEBaseMenu) getPlayer().containerMenu).setValidMenu(false);
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return getItemStack();
    }
}
