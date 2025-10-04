package net.pedroksl.advanced_ae.common.items;

import java.util.function.Supplier;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.api.ShowQuantumCrafters;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;

import appeng.api.util.IConfigManager;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.api.terminal.AE2wtlibConfigManager;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;

public class QuantumCrafterWirelessTerminalItem extends ItemWT {

    public QuantumCrafterWirelessTerminalItem() {
        super();
    }

    @Override
    public IConfigManager getConfigManager(Supplier<ItemStack> target) {
        return AE2wtlibConfigManager.builder(target)
                .registerSetting(AAESettings.TERMINAL_SHOW_QUANTUM_CRAFTERS, ShowQuantumCrafters.VISIBLE)
                .build();
    }

    @Override
    public MenuType<?> getMenuType(ItemMenuHostLocator itemMenuHostLocator, Player player) {
        return AAEMenus.QUANTUM_CRAFTER_WIRELESS_TERMINAL.get();
    }
}
