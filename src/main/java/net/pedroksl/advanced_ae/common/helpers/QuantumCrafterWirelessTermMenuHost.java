package net.pedroksl.advanced_ae.common.helpers;

import java.util.function.BiConsumer;

import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.api.IQuantumCrafterTermMenuHost;

import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;

public class QuantumCrafterWirelessTermMenuHost extends WTMenuHost implements IQuantumCrafterTermMenuHost {
    public QuantumCrafterWirelessTermMenuHost(
            ItemWT item, Player player, ItemMenuHostLocator locator, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
    }
}
