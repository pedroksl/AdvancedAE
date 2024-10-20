package net.pedroksl.advanced_ae.common.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.definitions.AAEHotkeys;
import net.pedroksl.advanced_ae.common.items.AdvPatternEncoderItem;
import net.pedroksl.advanced_ae.gui.AdvPatternEncoderMenu;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;

public class AdvPatternEncoderHost extends ItemMenuHost<AdvPatternEncoderItem> implements InternalInventoryHost {

    private final AppEngInternalInventory inOutInventory = new AppEngInternalInventory(this, 2);
    private AdvPatternEncoderMenu.InventoryChangedHandler invChangeHandler;

    public AdvPatternEncoderHost(AdvPatternEncoderItem item, Player player, ItemMenuHostLocator locator) {
        super(item, player, locator);

        var itemTag = this.getItemStack().get(AAEComponents.STACK_TAG);
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
            this.getItemStack().set(AAEComponents.STACK_TAG, itemTag);
        } else {
            this.getItemStack().remove(AAEComponents.STACK_TAG);
        }
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        var itemTag = this.getItemStack().getOrDefault(AAEComponents.STACK_TAG, new CompoundTag());
        var registry = this.getPlayer().registryAccess();
        if (this.inOutInventory == inv) {
            this.inOutInventory.writeToNBT(itemTag, "inOutInventory", registry);
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

    public String getCloseHotkey() {
        return AAEHotkeys.PATTERN_ENCODER_HOTKEY;
    }

    public AppEngInternalInventory getInventory() {
        return this.inOutInventory;
    }

    public void setInventoryChangedHandler(AdvPatternEncoderMenu.InventoryChangedHandler handler) {
        invChangeHandler = handler;
    }
}
