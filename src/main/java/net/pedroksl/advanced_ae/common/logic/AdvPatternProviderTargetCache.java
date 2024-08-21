package net.pedroksl.advanced_ae.common.logic;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

import appeng.api.AECapabilities;
import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.MEStorage;
import appeng.helpers.patternprovider.PatternProviderTarget;
import appeng.me.storage.CompositeStorage;
import appeng.parts.automation.StackWorldBehaviors;

class AdvPatternProviderTargetCache {
    private final BlockCapabilityCache<MEStorage, Direction> cache;
    private final Direction direction;
    private final IActionSource src;
    private final HashMap<Direction, Map<AEKeyType, ExternalStorageStrategy>> strategiesMap = new HashMap<>();

    AdvPatternProviderTargetCache(ServerLevel l, BlockPos pos, Direction direction, IActionSource src) {
        this.cache = BlockCapabilityCache.create(AECapabilities.ME_STORAGE, l, pos, direction);
        this.direction = direction;
        this.src = src;
        for (Direction dir : Direction.values()) {
            this.strategiesMap.put(dir, StackWorldBehaviors.createExternalStorageStrategies(l, pos, dir));
        }
    }

    @Nullable
    PatternProviderTarget find(Direction fromSide) {
        // our capability first: allows any storage channel
        Direction side = fromSide == null ? direction : fromSide;
        var meStorage = cache.getCapability();
        if (meStorage != null) {
            return wrapMeStorage(meStorage);
        }

        // otherwise fall back to the platform capability
        var externalStorages = new IdentityHashMap<AEKeyType, MEStorage>(2);
        for (var entry : strategiesMap.get(side).entrySet()) {
            var wrapper = entry.getValue().createWrapper(false, () -> {});
            if (wrapper != null) {
                externalStorages.put(entry.getKey(), wrapper);
            }
        }

        if (!externalStorages.isEmpty()) {
            return wrapMeStorage(new CompositeStorage(externalStorages));
        }

        return null;
    }

    private PatternProviderTarget wrapMeStorage(MEStorage storage) {
        return new PatternProviderTarget() {
            @Override
            public long insert(AEKey what, long amount, Actionable type) {
                return storage.insert(what, amount, type, src);
            }

            @Override
            public boolean containsPatternInput(Set<AEKey> patternInputs) {
                for (var stack : storage.getAvailableStacks()) {
                    if (patternInputs.contains(stack.getKey().dropSecondary())) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
