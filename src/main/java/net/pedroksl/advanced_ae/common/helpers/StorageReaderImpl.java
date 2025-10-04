package net.pedroksl.advanced_ae.common.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;

import appeng.api.AECapabilities;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.parts.automation.HandlerStrategy;

public class StorageReaderImpl<T, S> implements StorageReader {

    private final BlockCapabilityCache<T, Direction> cache;
    private final BlockCapabilityCache<MEStorage, Direction> meCache;
    private final HandlerStrategy<T, S> conversion;

    public StorageReaderImpl(
            BlockCapability<T, Direction> capability,
            HandlerStrategy<T, S> conversion,
            ServerLevel level,
            BlockPos fromPos,
            Direction fromSide) {
        this.cache = BlockCapabilityCache.create(capability, level, fromPos, fromSide);
        this.meCache = BlockCapabilityCache.create(AECapabilities.ME_STORAGE, level, fromPos, fromSide);
        this.conversion = conversion;
    }

    public long getCurrentStock(AEKey what) {
        if (what.getType() != conversion.getKeyType()) {
            return 0;
        }

        // Try internal capability first
        var meHandler = meCache.getCapability();
        if (meHandler != null) {
            var keys = meHandler.getAvailableStacks();
            if (keys.get(what) > 0) {
                return keys.get(what);
            }
        }

        var adjacentHandler = cache.getCapability();
        if (adjacentHandler == null) {
            return 0;
        }

        var adjacentStorage = conversion.getFacade(adjacentHandler);
        var amount = adjacentStorage.getAvailableStacks().get(what);
        return Math.max(0, amount);
    }

    public static StorageReader item(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        return new StorageReaderImpl<>(Capabilities.ItemHandler.BLOCK, HandlerStrategy.ITEMS, level, fromPos, fromSide);
    }

    public static StorageReader fluid(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        return new StorageReaderImpl<>(
                Capabilities.FluidHandler.BLOCK, HandlerStrategy.FLUIDS, level, fromPos, fromSide);
    }
}
