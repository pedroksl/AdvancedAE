package net.pedroksl.advanced_ae.common.inventory;

import appeng.api.config.YesNo;
import appeng.api.stacks.AEKey;
import appeng.helpers.patternprovider.PatternProviderReturnInventory;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;

public class AdvPatternProviderReturnInventory extends PatternProviderReturnInventory {
    public AdvPatternProviderReturnInventory(Runnable listener, AdvPatternProviderLogic logic) {
        super(listener);

        this.setFilter((slot, what) -> {
            var filter = logic.getConfigManager().getSetting(AAESettings.FILTERED_IMPORT);
            if (filter != YesNo.YES) return true;

            for (AEKey craft : logic.getTrackedCrafts()) {
                if (what.equals(craft)) {
                    return true;
                }
            }
            return false;
        });
    }
}
