package net.pedroksl.advanced_ae.gui;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.pedroksl.advanced_ae.api.IDirectionalOutputHost;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.network.packet.OutputDirectionClientUpdatePacket;
import net.pedroksl.advanced_ae.network.packet.UpdateSideStatusPacket;

import appeng.api.orientation.RelativeSide;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;

public class OutputDirectionMenu extends AEBaseMenu implements ISubMenu {

    public EnumSet<RelativeSide> allowedOutputs = EnumSet.allOf(RelativeSide.class);

    private final IDirectionalOutputHost host;

    private static final String CLEAR = "clearSides";

    public OutputDirectionMenu(int id, Inventory ip, IDirectionalOutputHost host) {
        this(AAEMenus.OUTPUT_DIRECTION.get(), id, ip, host);
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
        MenuOpener.open(AAEMenus.OUTPUT_DIRECTION.get(), player, locator);

        if (player.containerMenu instanceof OutputDirectionMenu cca) {
            cca.setAllowedOutputs(allowedOutputs);
            cca.broadcastChanges();
        }
    }

    public ItemStack getAdjacentBlock(RelativeSide side) {
        var dir = host.getOrientation().getSide(side);
        BlockPos blockPos = host.getBlockPosition().relative(dir);

        Level level = getLevel();
        if (level == null) {
            return null;
        }

        BlockState blockState = level.getBlockState(blockPos);
        if (!blockState.isAir()) {
            return blockState.getCloneItemStack(
                    new BlockHitResult(
                            blockPos.getCenter().relative(dir.getOpposite(), 0.5), dir.getOpposite(), blockPos, false),
                    level,
                    blockPos,
                    this.getPlayerInventory().player);
        } else {
            return ItemStack.EMPTY;
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
