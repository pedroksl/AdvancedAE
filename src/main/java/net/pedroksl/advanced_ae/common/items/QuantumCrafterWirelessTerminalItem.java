package net.pedroksl.advanced_ae.common.items;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.api.ShowQuantumCrafters;
import net.pedroksl.advanced_ae.gui.QuantumCrafterWirelessTermMenu;

import appeng.api.config.*;
import appeng.api.util.IConfigManager;
import appeng.util.ConfigManager;

import de.mari_023.ae2wtlib.terminal.ItemWT;

public class QuantumCrafterWirelessTerminalItem extends ItemWT {

    public QuantumCrafterWirelessTerminalItem() {
        super();
    }

    @Override
    public @NotNull IConfigManager getConfigManager(ItemStack target) {
        var out = new ConfigManager((manager, settingName) -> {
            manager.writeToNBT(target.getOrCreateTag());
        });

        out.registerSetting(AAESettings.TERMINAL_SHOW_QUANTUM_CRAFTERS, ShowQuantumCrafters.VISIBLE);

        out.readFromNBT(target.getOrCreateTag().copy());
        return out;
    }

    @Override
    public @NotNull MenuType<?> getMenuType(ItemStack itemStack) {
        return QuantumCrafterWirelessTermMenu.TYPE;
    }
}
