package net.pedroksl.advanced_ae.xmod.appmek;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.pedroksl.advanced_ae.common.helpers.StorageReader;

import appeng.api.AECapabilities;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;

import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import me.ramidzkh.mekae2.ae2.stack.MekanismExternalStorageStrategy;

public class AppMekPlugin {

    public static StorageReader chemicalStorageReader(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        return new ChemicalStorageReaderImpl(level, fromPos, fromSide);
    }

    private static class ChemicalStorageReaderImpl implements StorageReader {

        private final MekanismExternalStorageStrategy strategy;
        private final BlockCapabilityCache<MEStorage, Direction> meCache;

        ChemicalStorageReaderImpl(ServerLevel level, BlockPos fromPos, Direction fromSide) {
            this.strategy = new MekanismExternalStorageStrategy(level, fromPos, fromSide);
            this.meCache = BlockCapabilityCache.create(AECapabilities.ME_STORAGE, level, fromPos, fromSide);
        }

        @Override
        public long getCurrentStock(AEKey what) {
            if (what.getType() != MekanismKeyType.TYPE) {
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

            var wrapper = strategy.createWrapper(false, () -> {});
            if (wrapper == null) {
                return 0;
            }

            return Math.max(0, wrapper.getAvailableStacks().get(what));
        }
    }
}
