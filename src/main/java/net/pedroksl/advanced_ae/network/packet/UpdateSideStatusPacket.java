package net.pedroksl.advanced_ae.network.packet;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.gui.OutputDirectionMenu;

import appeng.api.orientation.RelativeSide;

public class UpdateSideStatusPacket implements IMessage<UpdateSideStatusPacket> {

    private RelativeSide side;

    public UpdateSideStatusPacket(RelativeSide side) {
        this.side = side;
    }

    public UpdateSideStatusPacket() {}

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeEnum(side);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        this.side = buf.readEnum(RelativeSide.class);
    }

    @Override
    public void onMessage(Player player) {
        if (player.containerMenu instanceof OutputDirectionMenu menu) {
            menu.updateSideStatus(side);
        }
    }

    @Override
    public Class<UpdateSideStatusPacket> getPacketClass() {
        return UpdateSideStatusPacket.class;
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
