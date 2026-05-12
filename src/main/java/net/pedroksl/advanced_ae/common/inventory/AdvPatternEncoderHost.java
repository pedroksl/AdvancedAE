package net.pedroksl.advanced_ae.common.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ItemContainerContents;
import net.pedroksl.advanced_ae.common.definitions.AAEHotkeysRegistry;
import net.pedroksl.advanced_ae.common.items.AdvPatternEncoderItem;
import net.pedroksl.advanced_ae.gui.AdvPatternEncoderMenu;
import net.pedroksl.ae2addonlib.registry.helpers.LibComponents;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;

public class AdvPatternEncoderHost extends ItemMenuHost<AdvPatternEncoderItem> implements InternalInventoryHost {

    private final AppEngInternalInventory inOutInventory = new AppEngInternalInventory(this, 2);
    private AdvPatternEncoderMenu.InventoryChangedHandler invChangeHandler;

    public AdvPatternEncoderHost(AdvPatternEncoderItem item, Player player, ItemMenuHostLocator locator) {
        super(item, player, locator);

        var inv = this.getItemStack().getOrDefault(LibComponents.ITEM_INVENTORY, ItemContainerContents.EMPTY);
        this.inOutInventory.fromItemContainerContents(inv);
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory inv) {
        this.getItemStack().set(LibComponents.ITEM_INVENTORY, this.inOutInventory.toItemContainerContents());
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        this.getItemStack().set(LibComponents.ITEM_INVENTORY, this.inOutInventory.toItemContainerContents());

        if (invChangeHandler != null) {
            invChangeHandler.handleChange(inv, slot);
        }
    }

    public String getCloseHotkey() {
        return AAEHotkeysRegistry.Keys.PATTERN_ENCODER_HOTKEY.getId();
    }

    public AppEngInternalInventory getInventory() {
        return this.inOutInventory;
    }

    public void setInventoryChangedHandler(AdvPatternEncoderMenu.InventoryChangedHandler handler) {
        invChangeHandler = handler;
    }
}
