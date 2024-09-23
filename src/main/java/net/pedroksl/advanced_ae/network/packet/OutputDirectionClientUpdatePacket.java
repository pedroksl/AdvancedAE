package net.pedroksl.advanced_ae.network.packet;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.client.gui.OutputDirectionScreen;

import appeng.api.orientation.RelativeSide;

public class OutputDirectionClientUpdatePacket implements IMessage {
    private final Set<RelativeSide> sides;

    public OutputDirectionClientUpdatePacket(EnumSet<RelativeSide> sides) {
        this.sides = sides;
    }

    public OutputDirectionClientUpdatePacket() {
        this.sides = new HashSet<>();
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.sides.size());
        for (var side : this.sides) {
            buf.writeEnum(side);
        }
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        var size = buf.readInt();
        for (var x = 0; x < size; x++) {
            this.sides.add(buf.readEnum(RelativeSide.class));
        }
    }

    @Override
    public void onMessage(Player player) {
        if (Minecraft.getInstance().screen instanceof OutputDirectionScreen screen) {
            screen.update(this.sides);
        }
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public ResourceLocation id() {
        return AdvancedAE.makeId("output_direction_update_client");
    }
}
