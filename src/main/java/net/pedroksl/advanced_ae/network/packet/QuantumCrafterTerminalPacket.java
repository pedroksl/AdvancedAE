package net.pedroksl.advanced_ae.network.packet;

import it.unimi.dsi.fastutil.ints.*;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.pedroksl.advanced_ae.client.gui.QuantumCrafterTermScreen;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

/**
 * Sends the content for a single {@link net.pedroksl.advanced_ae.common.helpers.AutoCraftingContainer} shown in the
 * quantum crafter terminal to the client.
 */
public record QuantumCrafterTerminalPacket(
        boolean fullUpdate,
        long inventoryId,
        int inventorySize, // Only valid if fullUpdate
        long sortBy, // Only valid if fullUpdate
        Int2ObjectMap<ItemStack> slots,
        Int2BooleanMap enabledArray,
        Int2BooleanMap invalidArray)
        implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, QuantumCrafterTerminalPacket> STREAM_CODEC =
            StreamCodec.ofMember(QuantumCrafterTerminalPacket::write, QuantumCrafterTerminalPacket::decode);

    private static final StreamCodec<RegistryFriendlyByteBuf, Int2ObjectMap<ItemStack>> SLOTS_STREAM_CODEC =
            ByteBufCodecs.map(
                    Int2ObjectOpenHashMap::new,
                    ByteBufCodecs.SHORT.map(Short::intValue, Integer::shortValue),
                    ItemStack.OPTIONAL_STREAM_CODEC,
                    128);
    private static final StreamCodec<RegistryFriendlyByteBuf, Int2BooleanMap> BOOLEAN_STREAM_CODEC = ByteBufCodecs.map(
            Int2BooleanOpenHashMap::new,
            ByteBufCodecs.SHORT.map(Short::intValue, Integer::shortValue),
            ByteBufCodecs.BOOL,
            128);

    public static final Type<QuantumCrafterTerminalPacket> TYPE =
            CustomAppEngPayload.createType("quantum_crafter_terminal");

    @Override
    public Type<QuantumCrafterTerminalPacket> type() {
        return TYPE;
    }

    public static QuantumCrafterTerminalPacket decode(RegistryFriendlyByteBuf stream) {
        var inventoryId = stream.readVarLong();
        var fullUpdate = stream.readBoolean();
        int inventorySize = 0;
        long sortBy = 0;
        if (fullUpdate) {
            inventorySize = stream.readVarInt();
            sortBy = stream.readVarLong();
        }

        var slots = SLOTS_STREAM_CODEC.decode(stream);
        var enabledArray = BOOLEAN_STREAM_CODEC.decode(stream);
        var invalidArray = BOOLEAN_STREAM_CODEC.decode(stream);
        return new QuantumCrafterTerminalPacket(
                fullUpdate, inventoryId, inventorySize, sortBy, slots, enabledArray, invalidArray);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeVarLong(inventoryId);
        data.writeBoolean(fullUpdate);
        if (fullUpdate) {
            data.writeVarInt(inventorySize);
            data.writeVarLong(sortBy);
        }
        SLOTS_STREAM_CODEC.encode(data, slots);
        BOOLEAN_STREAM_CODEC.encode(data, enabledArray);
        BOOLEAN_STREAM_CODEC.encode(data, invalidArray);
    }

    public static QuantumCrafterTerminalPacket fullUpdate(
            long inventoryId,
            int inventorySize,
            long sortBy,
            Int2ObjectMap<ItemStack> slots,
            Int2BooleanArrayMap enabledArray,
            Int2BooleanArrayMap invalidArray) {
        return new QuantumCrafterTerminalPacket(
                true, inventoryId, inventorySize, sortBy, slots, enabledArray, invalidArray);
    }

    public static QuantumCrafterTerminalPacket incrementalUpdate(
            long inventoryId,
            Int2ObjectMap<ItemStack> slots,
            Int2BooleanArrayMap enabledArray,
            Int2BooleanArrayMap invalidArray) {
        return new QuantumCrafterTerminalPacket(false, inventoryId, 0, 0, slots, enabledArray, invalidArray);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterTermScreen<?> screen) {
            if (fullUpdate) {
                screen.postFullUpdate(this.inventoryId, sortBy, inventorySize, slots, enabledArray, invalidArray);
            } else {
                screen.postIncrementalUpdate(this.inventoryId, slots, enabledArray, invalidArray);
            }
        }
    }
}
