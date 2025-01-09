package net.pedroksl.advanced_ae.common.inventory;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.GenericStack;
import appeng.helpers.patternprovider.PatternProviderReturnInventory;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;

public class AdvPatternProviderReturnInventory extends PatternProviderReturnInventory {
    public AdvPatternProviderReturnInventory(Runnable listener, AdvPatternProviderLogic logic) {
        super(listener);

        this.setFilter((slot, what) -> {
            var filter = logic.getConfigManager().getSetting(Settings.FILTER_ON_EXTRACT);
            if (filter != YesNo.YES) return true;

            for (IPatternDetails pattern : logic.getAvailablePatterns()) {
                for (GenericStack output : pattern.getOutputs()) {
                    if (what.matches(output)) return true;
                }
            }
            return false;
        });
    }
}
