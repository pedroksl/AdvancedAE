package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

public record MenuSelectionPacket(String data, int menuType) implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, MenuSelectionPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            MenuSelectionPacket::data,
            ByteBufCodecs.INT,
            MenuSelectionPacket::menuType,
            MenuSelectionPacket::new);

    public static final Type<MenuSelectionPacket> TYPE = CustomAppEngPayload.createType("aae_menu_selection_packet");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnClient(Player player) {
        if (menuType == -1) {
            player.getPersistentData().remove(data);
        } else {
            player.getPersistentData().putInt(data, menuType);
        }
    }
}
