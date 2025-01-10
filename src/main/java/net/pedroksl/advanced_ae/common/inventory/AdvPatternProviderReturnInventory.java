package net.pedroksl.advanced_ae.common.inventory;

import java.util.HashSet;
import java.util.Set;

import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;

import appeng.api.config.YesNo;
import appeng.api.stacks.AEKey;
import appeng.helpers.patternprovider.PatternProviderReturnInventory;

public class AdvPatternProviderReturnInventory extends PatternProviderReturnInventory {
    public AdvPatternProviderReturnInventory(Runnable listener, AdvPatternProviderLogic logic) {
        super(listener);

        this.setFilter(what -> {
            var filter = logic.getConfigManager().getSetting(AAESettings.FILTERED_IMPORT);
            if (filter != YesNo.YES) return true;

            Set<AEKey> tracked = logic.getTrackedCrafts();
            if (!tracked.isEmpty()) {
                for (AEKey craft : tracked) {
                    if (what == craft) {
                        return true;
                    }
                }
            }

            HashSet<AEKey> cached = logic.getOutputCache();
            return cached.stream().anyMatch(what::equals);
        });
    }
}
