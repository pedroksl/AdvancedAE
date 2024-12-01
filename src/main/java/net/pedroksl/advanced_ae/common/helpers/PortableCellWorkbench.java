package net.pedroksl.advanced_ae.common.helpers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.CopyMode;
import appeng.api.config.Settings;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.IConfigManager;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.util.ConfigInventory;
import appeng.util.ConfigManager;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;

public class PortableCellWorkbench implements InternalInventoryHost {

    private final AppEngInternalInventory cell = new AppEngInternalInventory(this, 1);

    private final ConfigManager manager = new ConfigManager(this::saveChanges);

    private IUpgradeInventory cacheUpgrades = null;
    private ConfigInventory cacheConfig = null;
    private boolean locked = false;

    private final PortableCellWorkbenchMenuHost host;

    public PortableCellWorkbench(PortableCellWorkbenchMenuHost host) {
        this.host = host;
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

    public void saveAdditional(CompoundTag data) {
        this.cell.writeToNBT(data, "cell");
        this.host.getConfig().writeToChildTag(data, "config");
        this.manager.writeToNBT(data);
    }

    public void loadTag(CompoundTag data) {
        this.cell.readFromNBT(data, "cell");
        this.host.getConfig().readFromChildTag(data, "config");
        this.manager.readFromNBT(data);
    }

    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(ISegmentedInventory.CELLS)) {
            return this.cell;
        }

        return null;
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
                        copy(configInventory, this.host.getConfig());
                    } else {
                        // Copy config inventory -> cell, when cell's config is empty
                        copy(this.host.getConfig(), configInventory);
                        // Copy items back. The cell may change the items on insert, for example if a fluid tank gets
                        // turned
                        // into a dummy fluid item.
                        copy(configInventory, this.host.getConfig());
                    }
                } else if (this.manager.getSetting(Settings.COPY_MODE) == CopyMode.CLEAR_ON_REMOVE) {
                    this.host.getConfig().clear();
                    this.saveChanges();
                }
            } finally {
                this.locked = false;
            }
        }
    }

    @Override
    public boolean isClientSide() {
        return this.host.isClientSide();
    }

    public void saveChanges() {}

    public void configChanged() {
        if (locked) {
            return;
        }

        this.locked = true;
        try {
            var c = this.getCellConfigInventory();
            if (c != null) {
                copy(this.host.getConfig(), c);
                // Copy items back. The cell may change the items on insert, for example if a fluid tank gets turned
                // into a dummy fluid item.
                copy(c, this.host.getConfig());
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

    public IConfigManager getConfigManager() {
        return this.manager;
    }

    public IUpgradeInventory getUpgrades() {
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

            return this.cacheUpgrades = inv;
        }
        return this.cacheUpgrades;
    }
}
