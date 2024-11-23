package net.pedroksl.advanced_ae.gui;

import appeng.api.orientation.RelativeSide;
import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.locator.MenuLocator;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.api.IDirectionalOutputHost;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.network.packet.OutputDirectionClientUpdatePacket;
import net.pedroksl.advanced_ae.network.packet.UpdateSideStatusPacket;

import java.util.EnumSet;

public class OutputDirectionMenu extends AEBaseMenu implements ISubMenu {

    public static final MenuType<OutputDirectionMenu> TYPE = MenuTypeBuilder
            .create((id, ip, host) -> new OutputDirectionMenu(id, ip, host), IDirectionalOutputHost.class)
            .build("aae_output_direction");

    public EnumSet<RelativeSide> allowedOutputs = EnumSet.allOf(RelativeSide.class);

    private final IDirectionalOutputHost host;

    private static final String CLEAR = "clearSides";

    public OutputDirectionMenu(int id, Inventory ip, IDirectionalOutputHost host) {
        this(TYPE, id, ip, host);
    }

    protected OutputDirectionMenu(
            MenuType<? extends OutputDirectionMenu> type, int id, Inventory ip, IDirectionalOutputHost host) {
        super(type, id, ip, host);
        this.host = host;

        registerClientAction(CLEAR, this::clearSides);
    }

    @Override
    public IDirectionalOutputHost getHost() {
        return this.host;
    }

    public static void open(ServerPlayer player, MenuLocator locator, EnumSet<RelativeSide> allowedOutputs) {
        MenuOpener.open(TYPE, player, locator);

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
        this.getHost().updateOutputSides(this.allowedOutputs);
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

        this.getHost().updateOutputSides(allowedOutputs);
        AAENetworkHandler.INSTANCE.sendTo(
                new OutputDirectionClientUpdatePacket(this.allowedOutputs), ((ServerPlayer) getPlayer()));
    }
}
