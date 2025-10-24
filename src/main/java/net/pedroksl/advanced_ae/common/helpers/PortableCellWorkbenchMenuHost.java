package net.pedroksl.advanced_ae.common.helpers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAEHotkeysRegistry;
import net.pedroksl.advanced_ae.common.definitions.AAENbt;

import appeng.api.config.CopyMode;
import appeng.api.config.Settings;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.helpers.IConfigInvHost;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.util.ConfigInventory;
import appeng.util.ConfigManager;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;

public class PortableCellWorkbenchMenuHost extends ItemMenuHost
        implements IConfigurableObject, IUpgradeableObject, InternalInventoryHost, IConfigInvHost {

    private final AppEngInternalInventory cell = new AppEngInternalInventory(this, 1, 1);
    private final GenericStackInv config =
            new GenericStackInv(this::configChanged, GenericStackInv.Mode.CONFIG_TYPES, 63);
    private final ConfigManager manager = new ConfigManager(this::saveChanges);

    private IUpgradeInventory cacheUpgrades = null;
    private ConfigInventory cacheConfig = null;
    private boolean locked = false;

    public PortableCellWorkbenchMenuHost(Player player, int inventorySlot, ItemStack stack) {
        super(player, inventorySlot, stack);
        this.manager.registerSetting(Settings.COPY_MODE, CopyMode.CLEAR_ON_REMOVE);
        this.cell.setEnableClientEvents(true);

        var itemTag = this.getItemStack().getTagElement(AAENbt.PORTABLE_CELL_STACK_TAG);
        if (itemTag != null) {
            this.cell.readFromNBT(itemTag, "cell");
        }
    }

    public ICellWorkbenchItem getCell() {
        if (this.cell.getStackInSlot(0).isEmpty()) {
            return null;
        }

        if (this.cell.getStackInSlot(0).getItem() instanceof ICellWorkbenchItem) {
            return (ICellWorkbenchItem) this.cell.getStackInSlot(0).getItem();
        }

        return null;
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.manager;
    }

    @Override
    public GenericStackInv getConfig() {
        return this.config;
    }

    private void configChanged() {
        if (locked) {
            return;
        }

        this.locked = true;
        try {
            var c = this.getCellConfigInventory();
            if (c != null) {
                copy(this.config, c);
                // Copy items back. The cell may change the items on insert, for example if a fluid tank gets turned
                // into a dummy fluid item.
                copy(c, this.config);
            }
        } finally {
            this.locked = false;
        }
    }

    public static void copy(GenericStackInv from, GenericStackInv to) {
        for (int i = 0; i < Math.min(from.size(), to.size()); ++i) {
            var fromStack = from.getStack(i);
            if (fromStack != null && !to.isAllowed(fromStack.what())) {
                fromStack = null; // Thing is not allowed in slot
            }
            to.setStack(i, fromStack);
        }
        for (int i = from.size(); i < to.size(); i++) {
            to.setStack(i, null);
        }
    }

    private ConfigInventory getCellConfigInventory() {
        if (this.cacheConfig == null) {
            var cell = this.getCell();
            if (cell == null) {
                return null;
            }

            var is = this.cell.getStackInSlot(0);
            if (is.isEmpty()) {
                return null;
            }

            var inv = cell.getConfigInventory(is);
            if (inv == null) {
                return null;
            }

            this.cacheConfig = inv;
        }
        return this.cacheConfig;
    }

    public void saveChanges() {
        var itemTag = new CompoundTag();
        this.cell.writeToNBT(itemTag, "cell");

        if (!itemTag.isEmpty()) {
            this.getItemStack().addTagElement(AAENbt.PORTABLE_CELL_STACK_TAG, itemTag);
        } else {
            this.getItemStack().removeTagKey(AAENbt.PORTABLE_CELL_STACK_TAG);
        }
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        if (inv == this.cell && !this.locked) {
            this.locked = true;
            try {
                this.cacheUpgrades = null;
                this.cacheConfig = null;

                var configInventory = this.getCellConfigInventory();
                if (configInventory != null) {
                    if (!configInventory.isEmpty()) {
                        // Copy cell -> config inventory
                        copy(configInventory, this.config);
                    } else {
                        // Copy config inventory -> cell, when cell's config is empty
                        copy(this.config, configInventory);
                        // Copy items back. The cell may change the items on insert, for example if a fluid tank gets
                        // turned
                        // into a dummy fluid item.
                        copy(configInventory, this.config);
                    }
                } else if (this.manager.getSetting(Settings.COPY_MODE) == CopyMode.CLEAR_ON_REMOVE) {
                    this.config.clear();
                    this.saveChanges();
                }
            } finally {
                this.locked = false;
            }
        }
    }

    public IUpgradeInventory getCachedUpgrades() {
        if (this.cacheUpgrades == null) {
            final ICellWorkbenchItem cell = this.getCell();
            if (cell == null) {
                return UpgradeInventories.empty();
            }

            final ItemStack is = this.cell.getStackInSlot(0);
            if (is.isEmpty()) {
                return UpgradeInventories.empty();
            }

            var inv = cell.getUpgrades(is);
            if (inv == null) {
                return UpgradeInventories.empty();
            }

            this.cacheUpgrades = inv;
        }
        return this.cacheUpgrades;
    }

    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(ISegmentedInventory.CELLS)) {
            return this.cell;
        }
        return InternalInventory.empty();
    }

    public String getCloseHotkey() {
        return AAEHotkeysRegistry.Keys.PATTERN_ENCODER.getId();
    }
}
