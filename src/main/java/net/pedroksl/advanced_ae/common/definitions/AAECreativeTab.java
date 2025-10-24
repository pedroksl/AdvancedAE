package net.pedroksl.advanced_ae.common.definitions;

import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.ae2addonlib.registry.CreativeTabRegistry;

public final class AAECreativeTab extends CreativeTabRegistry {

    public static final AAECreativeTab INSTANCE = new AAECreativeTab();

    AAECreativeTab() {
        super(AdvancedAE.MOD_ID, AAEText.ModName.text(), AAEBlocks.ADV_PATTERN_PROVIDER::stack);
    }
}
