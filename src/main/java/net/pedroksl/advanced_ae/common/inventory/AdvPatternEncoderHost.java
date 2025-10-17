package net.pedroksl.advanced_ae.common.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
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

        var itemTag = this.getItemStack().get(LibComponents.NBT_TAG);
        var registry = player.registryAccess();
        if (itemTag != null) {
            this.inOutInventory.readFromNBT(itemTag, "inOutInventory", registry);
        }
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory inv) {
        var itemTag = new CompoundTag();
        var registry = this.getPlayer().registryAccess();
        this.inOutInventory.writeToNBT(itemTag, "inOutInventory", registry);

        if (!itemTag.isEmpty()) {
            this.getItemStack().set(LibComponents.NBT_TAG, itemTag);
        } else {
            this.getItemStack().remove(LibComponents.NBT_TAG);
        }
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        var itemTag = this.getItemStack().getOrDefault(LibComponents.NBT_TAG, new CompoundTag());
        var registry = this.getPlayer().registryAccess();
        if (this.inOutInventory == inv) {
            this.inOutInventory.writeToNBT(itemTag, "inOutInventory", registry);
        }

        if (!itemTag.isEmpty()) {
            this.getItemStack().set(LibComponents.NBT_TAG, itemTag);
        } else {
            this.getItemStack().remove(LibComponents.NBT_TAG);
        }

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
