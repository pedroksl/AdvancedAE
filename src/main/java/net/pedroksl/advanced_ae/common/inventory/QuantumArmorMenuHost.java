package net.pedroksl.advanced_ae.common.inventory;

import java.util.function.BiConsumer;

import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import net.pedroksl.advanced_ae.gui.QuantumArmorConfigMenu;

public class QuantumArmorMenuHost<T extends QuantumArmorBase> extends ItemMenuHost<T> implements InternalInventoryHost {

    private final AppEngInternalInventory input = new AppEngInternalInventory(this, 1, 1);
    private QuantumArmorConfigMenu.InventoryChangedHandler invChangeHandler;

    private final BiConsumer<Player, ISubMenu> returnToMainMenu;

    public QuantumArmorMenuHost(
            T item, Player player, ItemMenuHostLocator locator, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator);
        this.returnToMainMenu = returnToMainMenu;
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory appEngInternalInventory) {
        var itemTag = new CompoundTag();
        var registry = this.getPlayer().registryAccess();
        this.input.writeToNBT(itemTag, "input", registry);

        if (!itemTag.isEmpty()) {
            this.getItemStack().set(AAEComponents.STACK_TAG, itemTag);
        } else {
            this.getItemStack().remove(AAEComponents.STACK_TAG);
        }
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        var itemTag = this.getItemStack().getOrDefault(AAEComponents.STACK_TAG, new CompoundTag());
        var registry = this.getPlayer().registryAccess();
        if (this.input == inv) {
            this.input.writeToNBT(itemTag, "input", registry);
        }

        if (!itemTag.isEmpty()) {
            this.getItemStack().set(AAEComponents.STACK_TAG, itemTag);
        } else {
            this.getItemStack().remove(AAEComponents.STACK_TAG);
        }

        if (invChangeHandler != null) {
            invChangeHandler.handleChange(inv, slot);
        }
    }

    public void returnToMainMenu(Player player, ISubMenu iSubMenu) {
        this.returnToMainMenu.accept(player, iSubMenu);
    }

    public void setInventoryChangedHandler(QuantumArmorConfigMenu.InventoryChangedHandler handler) {
        invChangeHandler = handler;
    }

    public AppEngInternalInventory getInventory() {
        return this.input;
    }
}
