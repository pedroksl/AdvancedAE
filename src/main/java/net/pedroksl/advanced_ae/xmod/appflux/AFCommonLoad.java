package net.pedroksl.advanced_ae.xmod.appflux;

import appeng.api.upgrades.Upgrades;
import com.glodblock.github.appflux.common.AFItemAndBlock;
import net.pedroksl.advanced_ae.common.AAEItemAndBlock;

public class AFCommonLoad {

	public static void init() {
		try {
			Upgrades.add(AFItemAndBlock.INDUCTION_CARD, AAEItemAndBlock.ADV_PATTERN_PROVIDER, 1, "gui" +
					".advanced_ae.adv_pattern_provider");
			Upgrades.add(AFItemAndBlock.INDUCTION_CARD, AAEItemAndBlock.ADV_PATTERN_PROVIDER_PART, 1, "gui" +
					".advanced_ae.adv_pattern_provider");
		} catch (Throwable ignored) {
			// NO-OP
		}
	}

}
