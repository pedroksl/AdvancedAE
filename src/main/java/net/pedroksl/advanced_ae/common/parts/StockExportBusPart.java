package net.pedroksl.advanced_ae.common.parts;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.helpers.StorageReader;
import net.pedroksl.advanced_ae.common.helpers.StorageReaderImpl;
import net.pedroksl.advanced_ae.mixins.MixinIOBusPartInvoker;
import net.pedroksl.advanced_ae.xmod.Addons;
import net.pedroksl.advanced_ae.xmod.appmek.AppMekPlugin;

import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.*;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.ISubMenuHost;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.items.parts.PartModels;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.PartModel;
import appeng.parts.automation.*;
import appeng.util.ConfigInventory;
import appeng.util.prioritylist.DefaultPriorityList;

@SuppressWarnings("UnstableApiUsage")
public class StockExportBusPart extends ExportBusPart implements ISubMenuHost {

    public static final ResourceLocation MODEL_BASE = AdvancedAE.makeId("part/stock_export_bus_part");

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, AppEng.makeId("part/export_bus_off"));

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, AppEng.makeId("part/export_bus_on"));

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL =
            new PartModel(MODEL_BASE, AppEng.makeId("part/export_bus_has_channel"));

    private ConfigInventory config;
    private ArrayList<StorageReader> storageReaders;

    public StockExportBusPart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    protected int getUpgradeSlots() {
        return 6;
    }

    @Override
    public void readFromNBT(CompoundTag extra, HolderLookup.Provider registries) {
        super.readFromNBT(extra, registries);
        this.config.readFromChildTag(extra, "extraConfig", registries);
    }

    @Override
    public void writeToNBT(CompoundTag extra, HolderLookup.Provider registries) {
        super.writeToNBT(extra, registries);
        this.config.writeToChildTag(extra, "extraConfig", registries);
    }

    public ConfigInventory getConfig() {
        if (this.config == null) {
            this.config = ConfigInventory.configStacks(63)
                    .supportedTypes(StackWorldBehaviors.withExportStrategy())
                    .changeListener(() -> ((MixinIOBusPartInvoker) this).callUpdateState())
                    .allowOverstacking(true)
                    .build();
        }

        return this.config;
    }

    private ArrayList<StorageReader> getSimStrategies() {
        BlockEntity self = this.getHost().getBlockEntity();
        BlockPos fromPos = self.getBlockPos().relative(this.getSide());
        Direction fromSide = this.getSide().getOpposite();
        ServerLevel level = ((ServerLevel) this.getLevel());

        if (storageReaders == null) {
            this.storageReaders = new ArrayList<>();
            this.storageReaders.add(StorageReaderImpl.item(level, fromPos, fromSide));
            this.storageReaders.add(StorageReaderImpl.fluid(level, fromPos, fromSide));

            if (Addons.APPMEK.isLoaded()) {
                this.storageReaders.add(
                        AppMekPlugin.chemicalStorageReader(((ServerLevel) this.getLevel()), fromPos, fromSide));
            }
        }
        return storageReaders;
    }

    @Override
    protected boolean doBusWork(IGrid grid) {
        IStorageService storageService = grid.getStorageService();
        ICraftingService cg = grid.getCraftingService();
        var schedulingMode = this.getConfigManager().getSetting(Settings.SCHEDULING_MODE);

        StackTransferContext context = createTransferContext(storageService, grid.getEnergyService());

        int x;
        for (x = 0; x < this.availableSlots() && context.hasOperationsLeft(); x++) {
            final int slotToExport = this.getStartingSlot(schedulingMode, x);
            GenericStack stack = this.getConfig().getStack(slotToExport);
            if (stack == null || stack.what() == null) {
                continue;
            }

            AEKey what = stack.what();
            int amount = (int) stack.amount();

            if (this.craftOnly()) {
                attemptCrafting(context, cg, slotToExport, what, amount);
                continue;
            }

            var before = context.getOperationsRemaining();

            var transferFactor = what.getAmountPerOperation();
            var maxAmount = (long) context.getOperationsRemaining() * transferFactor;

            var currentAmount = getCurrentStock(what);

            maxAmount = Math.min(maxAmount, amount - currentAmount);

            var transferred = getExportStrategy().transfer(context, what, maxAmount);
            if (transferred > 0) {
                context.reduceOperationsRemaining(Math.max(1, transferred / transferFactor));
            }

            if (before == context.getOperationsRemaining() && this.isCraftingEnabled()) {
                attemptCrafting(context, cg, slotToExport, what, amount);
            }
        }

        // Round-robin should only advance if something was actually exported
        if (context.hasDoneWork()) {
            this.updateSchedulingMode(schedulingMode, x);
        }

        return context.hasDoneWork();
    }

    private void attemptCrafting(
            StackTransferContext context, ICraftingService cg, int slotToExport, AEKey what, long targetAmount) {
        // don't bother crafting / checking or result, if target cannot accept at least 1 of requested item
        var maxAmount = (long) context.getOperationsRemaining() * what.getAmountPerOperation();

        var currentAmount = getCurrentStock(what);

        maxAmount = Math.min(maxAmount, targetAmount - currentAmount);

        var amount = getExportStrategy().push(what, maxAmount, Actionable.SIMULATE);
        if (amount > 0) {
            requestCrafting(cg, slotToExport, what, amount);
            context.reduceOperationsRemaining(Math.max(1, amount / what.getAmountPerOperation()));
        }
    }

    @NotNull
    private StackTransferContext createTransferContext(IStorageService storageService, IEnergyService energyService) {
        return new StackTransferContextImpl(
                storageService, energyService, this.source, getOperationsPerTick(), DefaultPriorityList.INSTANCE);
    }

    protected long getCurrentStock(AEKey what) {
        var strategies = getSimStrategies();

        var total = 0L;
        for (var strategy : strategies) {
            if (strategy instanceof StorageReader s) {
                total += s.getCurrentStock(what);
            }
        }
        return total;
    }

    private boolean craftOnly() {
        return isCraftingEnabled() && this.getConfigManager().getSetting(Settings.CRAFT_ONLY) == YesNo.YES;
    }

    private boolean isCraftingEnabled() {
        return isUpgradedWith(AEItems.CRAFTING_CARD);
    }

    protected MenuType<?> getMenuType() {
        return AAEMenus.STOCK_EXPORT_BUS.get();
    }

    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.open(AAEMenus.STOCK_EXPORT_BUS.get(), player, MenuLocators.forPart(this));
    }

    public ItemStack getMainMenuIcon() {
        return this.getPartItem().asItem().getDefaultInstance();
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }
}
