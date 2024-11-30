package net.pedroksl.advanced_ae.gui;

import appeng.api.config.CopyMode;
import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.util.IConfigManager;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.CellPartitionSlot;
import appeng.menu.slot.IPartitionSlotHost;
import appeng.menu.slot.OptionalRestrictedInputSlot;
import appeng.menu.slot.RestrictedInputSlot;
import appeng.util.EnumCycler;
import appeng.util.inv.SupplierInternalInventory;
import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.helpers.PortableCellWorkbenchMenuHost;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class PortableWorkbenchMenu extends UpgradeableMenu<PortableCellWorkbenchMenuHost>
        implements IPartitionSlotHost {

    public static final String ACTION_NEXT_COPYMODE = "nextCopyMode";
    public static final String ACTION_PARTITION = "partition";
    public static final String ACTION_CLEAR = "clear";
    public static final String ACTION_SET_FUZZY_MODE = "setFuzzyMode";

    @GuiSync(2)
    public CopyMode copyMode = CopyMode.CLEAR_ON_REMOVE;

    public PortableWorkbenchMenu(int id, Inventory ip, PortableCellWorkbenchMenuHost host) {
        super(AAEMenus.PORTABLE_WORKBENCH, id, ip, host);

        registerClientAction(ACTION_NEXT_COPYMODE, this::nextWorkBenchCopyMode);
        registerClientAction(ACTION_PARTITION, this::partition);
        registerClientAction(ACTION_CLEAR, this::clear);
        registerClientAction(ACTION_SET_FUZZY_MODE, FuzzyMode.class, this::setCellFuzzyMode);
    }

    public void setCellFuzzyMode(FuzzyMode fuzzyMode) {
        if (isClientSide()) {
            sendClientAction(ACTION_SET_FUZZY_MODE, fuzzyMode);
            return;
        }

        var cwi = getHost().getCell();
        if (cwi != null) {
            cwi.setFuzzyMode(getWorkbenchItem(), fuzzyMode);
        }
    }

    public void nextWorkBenchCopyMode() {
        if (isClientSide()) {
            sendClientAction(ACTION_NEXT_COPYMODE);
        } else {
            getHost().getConfigManager().putSetting(Settings.COPY_MODE, EnumCycler.next(this.getWorkBenchCopyMode()));
        }
    }

    private CopyMode getWorkBenchCopyMode() {
        return getHost().getConfigManager().getSetting(Settings.COPY_MODE);
    }

    @Override
    protected void setupInventorySlots() {
        var cell = this.getHost().getSubInventory(ISegmentedInventory.CELLS);
        this.addSlot(
                new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.WORKBENCH_CELL, cell, 0),
                SlotSemantics.STORAGE_CELL);
    }

    @Override
    protected void setupConfig() {
        var inv = getConfigInventory().createMenuWrapper();

        for (int slot = 0; slot < 63; slot++) {
            this.addSlot(new CellPartitionSlot(inv, this, slot), SlotSemantics.CONFIG);
        }
    }

    @Override
    protected void setupUpgrades() {
        // We support up to 8 upgrade slots, see ICellWorkbenchItem, but we need to pre-create all slots here
        // while the active number of slots changes depending on the item inserted
        var upgradeInventory = new SupplierInternalInventory(this::getCachedUpgrades);
        for (int i = 0; i < 8; i++) {
            OptionalRestrictedInputSlot slot = new OptionalRestrictedInputSlot(
                    RestrictedInputSlot.PlacableItemType.UPGRADES,
                    upgradeInventory,
                    this,
                    i,
                    i,
                    this.getPlayerInventory());
            this.addSlot(slot, SlotSemantics.UPGRADE);
        }
    }

    public IUpgradeInventory getCachedUpgrades() {
        return this.getHost().getCachedUpgrades();
    }

    public ItemStack getWorkbenchItem() {
        var cells = Objects.requireNonNull(getHost().getSubInventory(ISegmentedInventory.CELLS));
        return cells.getStackInSlot(0);
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.setCopyMode(this.getWorkBenchCopyMode());
        this.setFuzzyMode(this.getWorkBenchFuzzyMode());
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        return idx < getCachedUpgrades().size();
    }

    @Override
    public boolean isPartitionSlotEnabled(int idx) {
        var cwi = getHost().getCell();
        if (cwi != null && getCopyMode() == CopyMode.CLEAR_ON_REMOVE) {
            return idx < cwi.getConfigInventory(getWorkbenchItem()).size();
        }
        return getCopyMode() == CopyMode.KEEP_ON_REMOVE;
    }

    @Override
    public void onServerDataSync() {
        super.onServerDataSync();

        getHost().getConfigManager().putSetting(Settings.COPY_MODE, this.getCopyMode());
    }

    public void clear() {
        if (isClientSide()) {
            sendClientAction(ACTION_CLEAR);
        } else {
            getConfigInventory().clear();
            this.broadcastChanges();
        }
    }

    private FuzzyMode getWorkBenchFuzzyMode() {
        final ICellWorkbenchItem cwi = getHost().getCell();
        if (cwi != null) {
            return cwi.getFuzzyMode(getWorkbenchItem());
        }
        return FuzzyMode.IGNORE_ALL;
    }

    public void partition() {
        if (isClientSide()) {
            sendClientAction(ACTION_PARTITION);
            return;
        }

        var inv = getConfigInventory();
        var is = getWorkbenchItem();

        var it = iterateCellStacks(is);

        for (int x = 0; x < inv.size(); x++) {
            if (it.hasNext()) {
                inv.setStack(x, new GenericStack(it.next(), 0));
            } else {
                inv.setStack(x, null);
            }
        }

        this.broadcastChanges();
    }

    private GenericStackInv getConfigInventory() {
        return Objects.requireNonNull(this.getHost().getConfig());
    }

    @NotNull
    private Iterator<? extends AEKey> iterateCellStacks(ItemStack is) {
        var cellInv = StorageCells.getCellInventory(is, null);
        Iterator<? extends AEKey> i;
        if (cellInv != null) {
            i = Iterators.transform(cellInv.getAvailableStacks().iterator(), Map.Entry::getKey);
        } else {
            i = Collections.emptyIterator();
        }
        return i;
    }

    public CopyMode getCopyMode() {
        return this.copyMode;
    }

    private void setCopyMode(CopyMode copyMode) {
        this.copyMode = copyMode;
    }
}
