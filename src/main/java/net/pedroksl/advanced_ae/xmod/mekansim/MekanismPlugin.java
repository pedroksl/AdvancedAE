package net.pedroksl.advanced_ae.xmod.mekansim;

import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.helpers.RadiationShieldingProvider;

import appeng.api.implementations.items.IAEItemPowerStorage;

public class MekanismPlugin {

    public static RadiationShieldingProvider attachCapability(ItemStack stack) {
        try {
            return new RadiationShieldingProvider(stack, (IAEItemPowerStorage) stack.getItem());
        } catch (Throwable ignored) {
            return null;
        }
    }
}
