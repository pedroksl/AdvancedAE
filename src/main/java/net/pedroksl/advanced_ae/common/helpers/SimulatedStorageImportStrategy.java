package net.pedroksl.advanced_ae.common.helpers;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.capabilities.Capabilities;
import appeng.parts.automation.HandlerStrategy;
import appeng.util.BlockApiCache;

public class SimulatedStorageImportStrategy<T, S> {

    private final BlockApiCache<T> cache;
    private final BlockApiCache<MEStorage> meCache;
    private final HandlerStrategy<T, S> conversion;
    private final Direction fromSide;

    public SimulatedStorageImportStrategy(
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

    public long simulateTransfer(AEKey what, long toImport, IActionSource src) {
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
        var amount = adjacentStorage.extract(what, toImport, Actionable.SIMULATE, src);

        // Check if slots are locked for extraction
        if (amount == 0 && adjacentStorage.containsAnyFuzzy(Set.of(what))) {
            var stacks = adjacentStorage.getAvailableStacks();
            amount = stacks.get(what);
        }

        return Math.max(0, amount);
    }
}
