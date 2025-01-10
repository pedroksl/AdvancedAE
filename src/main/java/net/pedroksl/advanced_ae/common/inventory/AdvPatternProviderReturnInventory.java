package net.pedroksl.advanced_ae.common.inventory;

import appeng.api.config.YesNo;
import appeng.api.stacks.AEKey;
import appeng.helpers.patternprovider.PatternProviderReturnInventory;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;

import java.util.HashSet;
import java.util.Set;

public class AdvPatternProviderReturnInventory extends PatternProviderReturnInventory {
    public AdvPatternProviderReturnInventory(Runnable listener, AdvPatternProviderLogic logic) {
        super(listener);

        this.setFilter((slot, what) -> {
            var filter = logic.getConfigManager().getSetting(AAESettings.FILTERED_IMPORT);
            if (filter != YesNo.YES) return true;

            Set<AEKey> tracked = logic.getTrackedCrafts();
            if (!tracked.isEmpty()) {
                for (AEKey craft : tracked) {
                    if (what.equals(craft)) {
                        return true;
                    }
                }
            }

            HashSet<AEKey> cached = logic.getOutputCache();
            return cached.stream().anyMatch(what::equals);
        });
    }
}
