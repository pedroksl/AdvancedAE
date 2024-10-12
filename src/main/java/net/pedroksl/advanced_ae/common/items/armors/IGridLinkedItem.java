package net.pedroksl.advanced_ae.common.items.armors;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import appeng.api.features.IGridLinkableHandler;
import appeng.api.ids.AEComponents;
import appeng.api.implementations.blockentities.IWirelessAccessPoint;
import appeng.api.networking.IGrid;
import appeng.core.localization.PlayerMessages;
import appeng.util.Platform;

public interface IGridLinkedItem {
    IGridLinkableHandler LINKABLE_HANDLER = new LinkableHandler();

    default @Nullable GlobalPos getLinkedPosition(ItemStack item) {
        return item.get(AEComponents.WIRELESS_LINK_TARGET);
    }

    default @Nullable IGrid getLinkedGrid(ItemStack item, Level level, @Nullable Consumer<Component> errorConsumer) {
        if (level instanceof ServerLevel serverLevel) {
            GlobalPos linkedPos = this.getLinkedPosition(item);
            if (linkedPos == null) {
                if (errorConsumer != null) {
                    errorConsumer.accept(PlayerMessages.DeviceNotLinked.text());
                }

                return null;
            } else {
                ServerLevel linkedLevel = serverLevel.getServer().getLevel(linkedPos.dimension());
                if (linkedLevel == null) {
                    if (errorConsumer != null) {
                        errorConsumer.accept(PlayerMessages.LinkedNetworkNotFound.text());
                    }

                    return null;
                } else {
                    BlockEntity be = Platform.getTickingBlockEntity(linkedLevel, linkedPos.pos());
                    if (be instanceof IWirelessAccessPoint accessPoint) {
                        IGrid grid = accessPoint.getGrid();
                        if (grid == null && errorConsumer != null) {
                            errorConsumer.accept(PlayerMessages.LinkedNetworkNotFound.text());
                        }

                        return grid;
                    } else {
                        if (errorConsumer != null) {
                            errorConsumer.accept(PlayerMessages.LinkedNetworkNotFound.text());
                        }

                        return null;
                    }
                }
            }
        } else {
            return null;
        }
    }

    class LinkableHandler implements IGridLinkableHandler {
        LinkableHandler() {}

        public boolean canLink(ItemStack stack) {
            return stack.getItem() instanceof IGridLinkedItem;
        }

        public void link(ItemStack itemStack, GlobalPos pos) {
            itemStack.set(AEComponents.WIRELESS_LINK_TARGET, pos);
        }

        public void unlink(ItemStack itemStack) {
            itemStack.remove(AEComponents.WIRELESS_LINK_TARGET);
        }
    }
}
