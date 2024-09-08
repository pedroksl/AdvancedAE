package net.pedroksl.advanced_ae.gui.config;

import java.util.EnumSet;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.entities.ReactionChamberEntity;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.network.packet.OutputDirectionClientUpdatePacket;
import net.pedroksl.advanced_ae.network.packet.UpdateSideStatusPacket;

import appeng.api.orientation.RelativeSide;
import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;

public class OutputDirectionMenu extends AEBaseMenu implements ISubMenu {

    public EnumSet<RelativeSide> allowedOutputs = EnumSet.allOf(RelativeSide.class);

    private final ISubMenuHost host;

    private static final String CLEAR = "clearSides";

    public OutputDirectionMenu(int id, Inventory ip, ISubMenuHost host) {
        this(AAEMenus.OUTPUT_DIRECTION, id, ip, host);

        registerClientAction(CLEAR, this::clearSides);
    }

    protected OutputDirectionMenu(
            MenuType<? extends OutputDirectionMenu> type, int id, Inventory ip, ISubMenuHost host) {
        super(type, id, ip, host);
        this.host = host;
    }

    @Override
    public ISubMenuHost getHost() {
        return this.host;
    }

    public static void open(ServerPlayer player, MenuHostLocator locator, EnumSet<RelativeSide> allowedOutputs) {
        MenuOpener.open(AAEMenus.OUTPUT_DIRECTION, player, locator);

        if (player.containerMenu instanceof OutputDirectionMenu cca) {
            cca.setAllowedOutputs(allowedOutputs);
            cca.broadcastChanges();
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (isServerSide()) {
            AAENetworkHandler.INSTANCE.sendTo(
                    new OutputDirectionClientUpdatePacket(this.allowedOutputs), ((ServerPlayer) getPlayer()));
        }
    }

    public Level getLevel() {
        return this.getPlayerInventory().player.level();
    }

    private void setAllowedOutputs(EnumSet<RelativeSide> allowedOutputs) {
        this.allowedOutputs = allowedOutputs.clone();
    }

    public void clearSides() {
        if (isClientSide()) {
            sendClientAction(CLEAR);
            return;
        }

        this.allowedOutputs.clear();
        ((ReactionChamberEntity) this.getHost()).updateOutputSides(this.allowedOutputs);
    }

    public void updateSideStatus(RelativeSide side) {
        if (isClientSide()) {
            AAENetworkHandler.INSTANCE.sendToServer(new UpdateSideStatusPacket(side));
            return;
        }

        if (this.allowedOutputs.contains(side)) {
            this.allowedOutputs.remove(side);
        } else {
            this.allowedOutputs.add(side);
        }

        ((ReactionChamberEntity) this.getHost()).updateOutputSides(allowedOutputs);
        AAENetworkHandler.INSTANCE.sendTo(
                new OutputDirectionClientUpdatePacket(this.allowedOutputs), ((ServerPlayer) getPlayer()));
    }
}
