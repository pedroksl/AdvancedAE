package net.pedroksl.advanced_ae.network.packet;

import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.ae2addonlib.network.AddonPacket;

public class ItemTrackingPacket extends AddonPacket {

    private final UUID thrower;
    private final int entityId;
    private final int pickupDelay;

    public ItemTrackingPacket(FriendlyByteBuf stream) {
        this.thrower = stream.readUUID();
        this.entityId = stream.readInt();
        this.pickupDelay = stream.readInt();
    }

    public ItemTrackingPacket(ItemEntity item) {
        this.thrower = item.thrower;
        this.entityId = item.getId();
        this.pickupDelay = item.pickupDelay;
    }

    @Override
    public void write(FriendlyByteBuf stream) {
        stream.writeUUID(this.thrower);
        stream.writeInt(this.entityId);
        stream.writeInt(this.pickupDelay);
    }

    @Override
    public void clientPacketData(Player player) {
        if (player != null && player.level() != null) {
            Entity entity = player.level().getEntity(this.entityId);
            if (entity instanceof ItemEntity) {
                ((ItemEntity) entity).thrower = this.thrower;
                ((ItemEntity) entity).setPickUpDelay(this.pickupDelay);
            }
        }
    }
}
