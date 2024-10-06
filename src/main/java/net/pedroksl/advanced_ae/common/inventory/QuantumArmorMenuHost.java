package net.pedroksl.advanced_ae.common.inventory;

import java.util.function.BiConsumer;

import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;

public class QuantumArmorMenuHost<T extends QuantumArmorBase> extends ItemMenuHost<T> {

    private final BiConsumer<Player, ISubMenu> returnToMainMenu;

    public QuantumArmorMenuHost(
            T item, Player player, ItemMenuHostLocator locator, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator);
        this.returnToMainMenu = returnToMainMenu;
    }
}
