package net.pedroksl.advanced_ae.common.inventory;

import net.pedroksl.advanced_ae.common.definitions.AAEHotkeys;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.gui.patternencoder.AdvPatternEncoderMenu;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.inventories.InternalInventory;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;

public class AdvPatternEncoderHost extends ItemMenuHost implements InternalInventoryHost {

    private final AppEngInternalInventory inOutInventory = new AppEngInternalInventory(this, 2);
    private AdvPatternEncoderMenu.inventoryChangedHandler invChangeHandler;

    public AdvPatternEncoderHost(Player player, @Nullable Integer slot, ItemStack itemStack) {
        super(player, slot, itemStack);

        CompoundTag itemTag = this.getItemStack().getTag();
        if (itemTag != null) {
            this.inOutInventory.readFromNBT(itemTag, "inOutInventory");
        }
    }

    @Override
    public void saveChanges() {
        CompoundTag itemTag = this.getItemStack().getOrCreateTag();
        this.inOutInventory.writeToNBT(itemTag, "inOutInventory");
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        CompoundTag itemTag = this.getItemStack().getOrCreateTag();
        if (this.inOutInventory == inv) {
            this.inOutInventory.writeToNBT(itemTag, "inOutInventory");
        }

        invChangeHandler.handleChange(inv, slot);
    }

    public AppEngInternalInventory getInventory() {
        return this.inOutInventory;
    }

    public String getCloseHotkey() {
        return AAEHotkeys.Keys.PATTERN_ENCODER.getId();
    }

    public void setInventoryChangedHandler(AdvPatternEncoderMenu.inventoryChangedHandler handler) {
        invChangeHandler = handler;
    }
}
