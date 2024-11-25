package net.pedroksl.advanced_ae.common.parts;

import java.lang.reflect.Method;

import com.glodblock.github.glodium.reflect.ReflectKit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.helpers.SimulatedStorageImportStrategy;

import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
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
import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.HandlerStrategy;
import appeng.parts.automation.IOBusPart;
import appeng.parts.automation.StackWorldBehaviors;
import appeng.util.ConfigInventory;

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

    private static final Method createTransferContext;
    private static final Method updateState;
    private ConfigInventory config;

    private SimulatedStorageImportStrategy<IItemHandler, ItemStack> itemImportStrategy;
    private SimulatedStorageImportStrategy<IFluidHandler, FluidStack> fluidImportStrategy;

    public StockExportBusPart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public void readFromNBT(CompoundTag extra) {
        super.readFromNBT(extra);
        this.config.readFromChildTag(extra, "extraConfig");
    }

    @Override
    public void writeToNBT(CompoundTag extra) {
        super.writeToNBT(extra);
        this.config.writeToChildTag(extra, "extraConfig");
    }

    public ConfigInventory getConfig() {
        if (this.config == null) {
            this.config = ConfigInventory.configStacks(
                    StackWorldBehaviors.hasExportStrategyFilter(), 63, () -> updatePartState(this), true);
        }

        return this.config;
    }

    private void getStrategies() {
        BlockEntity self = this.getHost().getBlockEntity();
        BlockPos fromPos = self.getBlockPos().relative(this.getSide());
        Direction fromSide = this.getSide().getOpposite();

        if (this.itemImportStrategy == null) {
            this.itemImportStrategy = new SimulatedStorageImportStrategy<>(
                    ForgeCapabilities.ITEM_HANDLER,
                    HandlerStrategy.ITEMS,
                    (ServerLevel) this.getLevel(),
                    fromPos,
                    fromSide);
        }

        if (this.fluidImportStrategy == null) {
            this.fluidImportStrategy = new SimulatedStorageImportStrategy<>(
                    ForgeCapabilities.FLUID_HANDLER,
                    HandlerStrategy.FLUIDS,
                    (ServerLevel) this.getLevel(),
                    fromPos,
                    fromSide);
        }
    }

    @Override
    protected boolean doBusWork(IGrid grid) {
        IStorageService storageService = grid.getStorageService();
        ICraftingService cg = grid.getCraftingService();
        var schedulingMode = this.getConfigManager().getSetting(Settings.SCHEDULING_MODE);

        StackTransferContext context = getStackTransferContext(this, storageService, grid.getEnergyService());

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

            var currentAmount = getCurrentStock(context, what, amount);

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

    private boolean craftOnly() {
        return this.isCraftingEnabled() && this.getConfigManager().getSetting(Settings.CRAFT_ONLY) == YesNo.YES;
    }

    private boolean isCraftingEnabled() {
        return this.isUpgradedWith(AEItems.CRAFTING_CARD);
    }

    private void attemptCrafting(
            StackTransferContext context, ICraftingService cg, int slotToExport, AEKey what, long targetAmount) {
        // don't bother crafting / checking or result, if target cannot accept at least 1 of requested item
        var maxAmount = (long) context.getOperationsRemaining() * what.getAmountPerOperation();

        var currentAmount = getCurrentStock(context, what, targetAmount);

        maxAmount = Math.min(maxAmount, targetAmount - currentAmount);

        var amount = getExportStrategy().push(what, maxAmount, Actionable.SIMULATE);
        if (amount > 0) {
            requestCrafting(cg, slotToExport, what, amount);
            context.reduceOperationsRemaining(Math.max(1, amount / what.getAmountPerOperation()));
        }
    }

    private long getCurrentStock(StackTransferContext context, AEKey what, long targetAmount) {
        getStrategies();

        return this.itemImportStrategy.simulateTransfer(what, targetAmount, context.getActionSource())
                + this.fluidImportStrategy.simulateTransfer(what, targetAmount, context.getActionSource());
    }

    public static void updatePartState(IOBusPart owner) {
        ReflectKit.executeMethod(owner, updateState);
    }

    protected MenuType<?> getMenuType() {
        return AAEMenus.STOCK_EXPORT_BUS;
    }

    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.open(AAEMenus.STOCK_EXPORT_BUS, player, MenuLocators.forPart(this));
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

    @SuppressWarnings("experimental")
    private static StackTransferContext getStackTransferContext(
            ExportBusPart part, IStorageService storageService, IEnergyService energyService) {
        return ReflectKit.executeMethod2(part, createTransferContext, new Object[] {storageService, energyService});
    }

    static {
        try {
            updateState = ReflectKit.reflectMethod(IOBusPart.class, "updateState");
            createTransferContext = ReflectKit.reflectMethod(
                    ExportBusPart.class, "createTransferContext", IStorageService.class, IEnergyService.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Reflection failed", e);
        }
    }
}
