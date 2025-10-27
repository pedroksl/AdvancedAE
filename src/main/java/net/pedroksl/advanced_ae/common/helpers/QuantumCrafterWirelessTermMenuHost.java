package net.pedroksl.advanced_ae.common.helpers;

import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.api.IQuantumCrafterTermMenuHost;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;

import appeng.menu.ISubMenu;

import de.mari_023.ae2wtlib.terminal.WTMenuHost;

public class QuantumCrafterWirelessTermMenuHost extends WTMenuHost implements IQuantumCrafterTermMenuHost {
    public QuantumCrafterWirelessTermMenuHost(
            Player player,
            @Nullable Integer inventorySlot,
            ItemStack is,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(player, inventorySlot, is, returnToMainMenu);
        this.readFromNbt();
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return AAEItems.QUANTUM_CRAFTER_WIRELESS_TERMINAL.stack();
    }
}
