package net.pedroksl.advanced_ae.xmod.appflux;

import com.glodblock.github.appflux.common.AFSingletons;

import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEText;

import appeng.api.upgrades.Upgrades;

public class AFCommonLoad {

    public static void init() {
        try {
            Upgrades.add(
                    AFSingletons.INDUCTION_CARD,
                    AAEBlocks.ADV_PATTERN_PROVIDER,
                    1,
                    AAEText.AdvPatternProvider.getTranslationKey());
            Upgrades.add(
                    AFSingletons.INDUCTION_CARD,
                    AAEItems.ADV_PATTERN_PROVIDER,
                    1,
                    AAEText.AdvPatternProvider.getTranslationKey());
            Upgrades.add(AFSingletons.INDUCTION_CARD, AAEBlocks.SMALL_ADV_PATTERN_PROVIDER, 1);
            Upgrades.add(AFSingletons.INDUCTION_CARD, AAEItems.SMALL_ADV_PATTERN_PROVIDER, 1);
        } catch (Throwable ignored) {
            // NO-OP
        }
    }
}
