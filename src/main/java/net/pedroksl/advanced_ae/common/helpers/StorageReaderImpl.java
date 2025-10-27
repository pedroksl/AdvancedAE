package net.pedroksl.advanced_ae.common.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.capabilities.Capabilities;
import appeng.parts.automation.HandlerStrategy;
import appeng.util.BlockApiCache;

public class StorageReaderImpl<T, S> implements StorageReader {

    private final BlockApiCache<T> cache;
    private final BlockApiCache<MEStorage> meCache;
    private final HandlerStrategy<T, S> conversion;
    private final Direction fromSide;

    public StorageReaderImpl(
            Capability<T> capability,
            HandlerStrategy<T, S> conversion,
            ServerLevel level,
            BlockPos fromPos,
            Direction fromSide) {
        this.cache = BlockApiCache.create(capability, level, fromPos);
        this.meCache = BlockApiCache.create(Capabilities.STORAGE, level, fromPos);
        this.conversion = conversion;
        this.fromSide = fromSide;
    }

    public long getCurrentStock(AEKey what) {
        if (what.getType() != conversion.getKeyType()) {
            return 0;
        }

        // Try internal capability first
        var meHandler = meCache.find(fromSide);
        if (meHandler != null) {
            var keys = meHandler.getAvailableStacks();
            if (keys.get(what) > 0) {
                return keys.get(what);
            }
        }

        var adjacentHandler = cache.find(fromSide);
        if (adjacentHandler == null) {
            return 0;
        }

        var adjacentStorage = conversion.getFacade(adjacentHandler);
        var amount = adjacentStorage.getAvailableStacks().get(what);
        return Math.max(0, amount);
    }

    public static StorageReader item(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        return new StorageReaderImpl<>(ForgeCapabilities.ITEM_HANDLER, HandlerStrategy.ITEMS, level, fromPos, fromSide);
    }

    public static StorageReader fluid(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        return new StorageReaderImpl<>(
                ForgeCapabilities.FLUID_HANDLER, HandlerStrategy.FLUIDS, level, fromPos, fromSide);
    }
}
