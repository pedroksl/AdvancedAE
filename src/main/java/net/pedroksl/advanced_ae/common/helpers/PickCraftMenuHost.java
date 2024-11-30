package net.pedroksl.advanced_ae.common.helpers;

import appeng.api.config.Actionable;
import appeng.api.implementations.blockentities.IWirelessAccessPoint;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.storage.ISubMenuHost;
import appeng.blockentity.networking.WirelessAccessPointBlockEntity;
import appeng.core.localization.GuiText;
import appeng.core.localization.PlayerMessages;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.items.armors.IGridLinkedItem;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public class PickCraftMenuHost<T extends QuantumArmorBase> extends ItemMenuHost
        implements ISubMenuHost, IActionHost {

    @Nullable
    private IWirelessAccessPoint currentAccessPoint;
    /**
     * The distance to the currently connected access point in blocks.
     */
    protected double currentDistanceFromGrid = Double.MAX_VALUE;
    /**
     * How far away are we from losing signal.
     */
    protected double currentRemainingRange = Double.MIN_VALUE;

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
        this.currentDistanceFromGrid = Double.MAX_VALUE;
        this.currentRemainingRange = Double.MIN_VALUE;

        var targetGrid = getLinkedGrid(getItemStack());
        if (targetGrid != null) {
            @Nullable IWirelessAccessPoint bestWap = null;
            double bestSqDistance = Double.MAX_VALUE;
            double bestSqRemainingRange = Double.MIN_VALUE;

            // Find closest WAP
            for (var wap : targetGrid.getMachines(WirelessAccessPointBlockEntity.class)) {
                var signal = getAccessPointSignal(wap);

                // If the WAP is not suitable then MAX_VALUE will be returned and the check will fail
                if (signal.distanceSquared < bestSqDistance) {
                    bestSqDistance = signal.distanceSquared;
                    bestWap = wap;
                }
                // There may be access points with larger range that are farther away,
                // but those would have larger energy consumption
                if (signal.remainingRangeSquared > bestSqRemainingRange) {
                    bestSqRemainingRange = signal.remainingRangeSquared;
                }
            }

            // If no WAP is found this will work too
            this.currentAccessPoint = bestWap;
            this.currentDistanceFromGrid = Math.sqrt(bestSqDistance);
            this.currentRemainingRange = Math.sqrt(bestSqRemainingRange);
        }
    }

    protected AccessPointSignal getAccessPointSignal(IWirelessAccessPoint wap) {
        double rangeLimit = wap.getRange();
        rangeLimit *= rangeLimit;

        var dc = wap.getLocation();

        if (dc.getLevel() == this.getPlayer().level()) {
            var offX = dc.getPos().getX() - this.getPlayer().getX();
            var offY = dc.getPos().getY() - this.getPlayer().getY();
            var offZ = dc.getPos().getZ() - this.getPlayer().getZ();

            double r = offX * offX + offY * offY + offZ * offZ;
            if (wap.isActive()) {
                return new AccessPointSignal(r, rangeLimit - r);
            }
        }

        return new AccessPointSignal(Double.MAX_VALUE, Double.MIN_VALUE);
    }

    public record AccessPointSignal(double distanceSquared, double remainingRangeSquared) {}

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
