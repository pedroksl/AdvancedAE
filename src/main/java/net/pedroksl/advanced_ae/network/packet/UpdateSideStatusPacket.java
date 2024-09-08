package net.pedroksl.advanced_ae.network.packet;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.gui.config.OutputDirectionMenu;

import appeng.api.orientation.RelativeSide;

public class UpdateSideStatusPacket implements IMessage {

    private RelativeSide side;

    public UpdateSideStatusPacket(RelativeSide side) {
        this.side = side;
    }

    public UpdateSideStatusPacket() {}

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeEnum(side);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        this.side = buf.readEnum(RelativeSide.class);
    }

    @Override
    public void onMessage(Player player) {
        if (player.containerMenu instanceof OutputDirectionMenu menu) {
            menu.updateSideStatus(side);
        }
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public ResourceLocation id() {
        return AdvancedAE.makeId("update_side_status");
    }
}
