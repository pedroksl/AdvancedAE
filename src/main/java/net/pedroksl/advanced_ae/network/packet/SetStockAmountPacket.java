package net.pedroksl.advanced_ae.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.advanced_ae.gui.QuantumCrafterConfigPatternMenu;

import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

public record SetStockAmountPacket(int index, long amount) implements ServerboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, SetStockAmountPacket> STREAM_CODEC =
            StreamCodec.ofMember(SetStockAmountPacket::write, SetStockAmountPacket::decode);

    public static final Type<SetStockAmountPacket> TYPE = CustomAppEngPayload.createType("aae_set_stock_amount");

    @Override
    public Type<SetStockAmountPacket> type() {
        return TYPE;
    }

    public SetStockAmountPacket(int index, long amount) {
        this.index = index;
        this.amount = amount;
    }

    public static SetStockAmountPacket decode(RegistryFriendlyByteBuf stream) {
        var index = stream.readInt();
        var amount = stream.readLong();
        return new SetStockAmountPacket(index, amount);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeInt(this.index);
        data.writeLong(this.amount);
    }

    @Override
    public void handleOnServer(ServerPlayer serverPlayer) {
        if (serverPlayer.containerMenu instanceof QuantumCrafterConfigPatternMenu menu) {
            menu.setStockAmount(this.index, this.amount);
        }
    }
}
