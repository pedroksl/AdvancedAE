package net.pedroksl.advanced_ae.xmod.appflux;

import appeng.api.upgrades.Upgrades;
import com.glodblock.github.appflux.common.AFSingletons;
import net.pedroksl.advanced_ae.common.AAESingletons;

public class AFCommonLoad {

	public static void init() {
		try {
			Upgrades.add(AFSingletons.INDUCTION_CARD, AAESingletons.ADV_PATTERN_PROVIDER, 1, "gui" +
					".advanced_ae.adv_pattern_provider");
			Upgrades.add(AFSingletons.INDUCTION_CARD, AAESingletons.ADV_PATTERN_PROVIDER_PART, 1, "gui" +
					".advanced_ae.adv_pattern_provider");
		} catch (Throwable ignored) {
			// NO-OP
		}
	}

}
