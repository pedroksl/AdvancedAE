package net.pedroksl.advanced_ae.common.helpers;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

import appeng.api.AECapabilities;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.parts.automation.HandlerStrategy;

public class SimulatedStorageImportStrategy<T, S> {

    private final BlockCapabilityCache<T, Direction> cache;
    private final BlockCapabilityCache<MEStorage, Direction> meCache;
    private final HandlerStrategy<T, S> conversion;

    public SimulatedStorageImportStrategy(
            BlockCapability<T, Direction> capability,
            HandlerStrategy<T, S> conversion,
            ServerLevel level,
            BlockPos fromPos,
            Direction fromSide) {
        this.cache = BlockCapabilityCache.create(capability, level, fromPos, fromSide);
        this.meCache = BlockCapabilityCache.create(AECapabilities.ME_STORAGE, level, fromPos, fromSide);
        this.conversion = conversion;
    }

    public long simulateTransfer(AEKey what, long toImport, IActionSource src) {
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
        var amount = adjacentStorage.extract(what, toImport, Actionable.SIMULATE, src);

        // Check if slots are locked for extraction
        if (amount == 0 && adjacentStorage.containsAnyFuzzy(Set.of(what))) {
            var stacks = adjacentStorage.getAvailableStacks();
            amount = stacks.get(what);
        }

        return Math.max(0, amount);
    }
}
