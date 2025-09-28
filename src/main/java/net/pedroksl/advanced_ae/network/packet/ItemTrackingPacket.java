package net.pedroksl.advanced_ae.network.packet;

import java.util.UUID;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

public class ItemTrackingPacket implements IMessage<ItemTrackingPacket> {

    private UUID thrower;
    private int entityId;
    private int pickupDelay;

    public ItemTrackingPacket() {}

    public ItemTrackingPacket(ItemEntity item) {
        this.thrower = item.thrower;
        this.entityId = item.getId();
        this.pickupDelay = item.pickupDelay;
    }

    @Override
    public void fromBytes(FriendlyByteBuf friendlyByteBuf) {
        this.thrower = friendlyByteBuf.readUUID();
        this.entityId = friendlyByteBuf.readInt();
        this.pickupDelay = friendlyByteBuf.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(this.thrower);
        friendlyByteBuf.writeInt(this.entityId);
        friendlyByteBuf.writeInt(this.pickupDelay);
    }

    @Override
    public void onMessage(Player player) {
        if (player != null && player.level() != null) {
            Entity entity = player.level().getEntity(this.entityId);
            if (entity instanceof ItemEntity) {
                ((ItemEntity) entity).thrower = this.thrower;
                ((ItemEntity) entity).setPickUpDelay(this.pickupDelay);
            }
        }
    }

    @Override
    public Class<ItemTrackingPacket> getPacketClass() {
        return ItemTrackingPacket.class;
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
