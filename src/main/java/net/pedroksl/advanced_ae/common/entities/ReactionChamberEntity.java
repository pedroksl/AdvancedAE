package net.pedroksl.advanced_ae.common.entities;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.recipes.ReactionChamberRecipe;
import net.pedroksl.advanced_ae.recipes.ReactionChamberRecipes;

import appeng.api.config.*;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.AEItemFilters;

public class ReactionChamberEntity extends AENetworkedPoweredBlockEntity
        implements IGridTickable, IUpgradeableObject, IConfigurableObject {
    private static final int MAX_PROCESSING_STEPS = 200;
    private static final int MAX_TANK_CAPACITY = 16000;

    private final IUpgradeInventory upgrades;
    private final IConfigManager configManager;

    private final AppEngInternalInventory inputInv = new AppEngInternalInventory(this, 3, 64);
    private final AppEngInternalInventory outputInv = new AppEngInternalInventory(this, 1, 64);
    private final InternalInventory inv = new CombinedInternalInventory(this.inputInv, this.outputInv);

    private final FilteredInternalInventory inputExposed =
            new FilteredInternalInventory(this.inputInv, AEItemFilters.INSERT_ONLY);
    private final FilteredInternalInventory outputExposed =
            new FilteredInternalInventory(this.outputInv, AEItemFilters.EXTRACT_ONLY);
    private final InternalInventory invExposed = new CombinedInternalInventory(this.inputExposed, this.outputExposed);

    private final GenericStackInv fluidInv =
            new GenericStackInv(Set.of(AEKeyType.fluids()), this::onChangeTank, GenericStackInv.Mode.STORAGE, 1);

    private boolean working = false;
    private int processingTime = 0;
    private boolean dirty = false;
    private long clientStart;

    private ReactionChamberRecipe cachedTask = null;

    public ReactionChamberEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        this.getMainNode().setIdlePowerUsage(0).addService(IGridTickable.class, this);
        this.setInternalMaxPower(15000);

        this.fluidInv.setCapacity(AEKeyType.fluids(), MAX_TANK_CAPACITY);

        this.upgrades = UpgradeInventories.forMachine(AAEBlocks.REACTION_CHAMBER, 4, this::saveChanges);

        this.configManager = IConfigManager.builder(this::onConfigChanged)
                .registerSetting(Settings.AUTO_EXPORT, YesNo.NO)
                .build();

        this.setPowerSides(getGridConnectableSides(getOrientation()));
    }

    public long getClientStart() {
        return this.clientStart;
    }

    private void setClientStart(long clientStart) {
        this.clientStart = clientStart;
    }

    public boolean isWorking() {
        return this.working;
    }

    public void setWorking(boolean working) {
        if (working && !this.working) {
            setClientStart(System.currentTimeMillis());
            this.markForUpdate();
        }
        this.working = working;
    }

    public int getMaxProcessingTime() {
        return MAX_PROCESSING_STEPS;
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    private void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    @Override
    protected void saveVisualState(CompoundTag data) {
        super.saveVisualState(data);

        data.putBoolean("isWorking", isWorking());
    }

    @Override
    protected void loadVisualState(CompoundTag data) {
        super.loadVisualState(data);

        setWorking(data.getBoolean("isWorking"));
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return EnumSet.allOf(Direction.class);
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.inv;
    }

    public InternalInventory getInput() {
        return this.inputInv;
    }

    public InternalInventory getOutput() {
        return this.outputInv;
    }

    public GenericStackInv getTank() {
        return this.fluidInv;
    }

    @Nullable
    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(ISegmentedInventory.STORAGE)) {
            return this.getInternalInventory();
        } else if (id.equals(ISegmentedInventory.UPGRADES)) {
            return this.upgrades;
        }

        return super.getSubInventory(id);
    }

    @Override
    protected InternalInventory getExposedInventoryForSide(Direction facing) {
        return this.invExposed;
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return upgrades;
    }

    private void onChangeInventory() {
        this.dirty = true;

        getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
    }

    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        onChangeInventory();
    }

    public void onChangeTank() {
        onChangeInventory();
    }

    private boolean hasAutoExportWork() {
        return !this.outputInv.getStackInSlot(0).isEmpty()
                && configManager.getSetting(Settings.AUTO_EXPORT) == YesNo.YES;
    }

    private boolean hasCraftWork() {
        var task = this.getTask();
        if (task != null) {
            // Only process if the result would fit.
            return this.outputInv
                    .insertItem(0, task.getResultItem().copy(), true)
                    .isEmpty();
        }

        this.setProcessingTime(0);
        return this.isWorking();
    }

    @Nullable
    public ReactionChamberRecipe getTask() {
        if (this.cachedTask == null && level != null) {

            this.cachedTask = findRecipe(level);
        }
        return this.cachedTask;
    }

    private ReactionChamberRecipe findRecipe(Level level) {
        return ReactionChamberRecipes.findRecipe(
                level,
                this.inputInv.getStackInSlot(0),
                this.inputInv.getStackInSlot(1),
                this.inputInv.getStackInSlot(2),
                this.fluidInv.getStack(0));
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(1, 20, !hasAutoExportWork() && !this.hasCraftWork());
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode iGridNode, int ticksSinceLastCall) {
        if (this.dirty) {
            // Check if running recipe is still valid
            if (level != null) {
                var recipe = findRecipe(level);
                if (recipe == null) {
                    this.setProcessingTime(0);
                    this.setWorking(false);
                    this.cachedTask = null;
                }
            }
            this.dirty = false;
        }

        if (this.hasCraftWork()) {
            this.setWorking(true);
            getMainNode().ifPresent(grid -> {
                IEnergyService eg = grid.getEnergyService();
                IEnergySource src = this;

                // Note: required ticks = 16 + ceil(MAX_PROCESSING_STEPS / speedFactor)
                final int speedFactor =
                        switch (this.upgrades.getInstalledUpgrades(AEItems.SPEED_CARD)) {
                            default -> 2; // 116 ticks
                            case 1 -> 3; // 83 ticks
                            case 2 -> 5; // 56 ticks
                            case 3 -> 10; // 36 ticks
                            case 4 -> 50; // 20 ticks
                        };
                final int powerConsumption = 10 * speedFactor;
                final double powerThreshold = powerConsumption - 0.01;
                double powerReq = this.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);

                if (powerReq <= powerThreshold) {
                    src = eg;
                    powerReq = eg.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);
                }

                if (powerReq > powerThreshold) {
                    src.extractAEPower(powerConsumption, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    this.setProcessingTime(this.getProcessingTime() + speedFactor);
                }
            });

            if (this.getProcessingTime() >= this.getMaxProcessingTime()) {
                this.setProcessingTime(0);
                final ReactionChamberRecipe out = this.getTask();
                if (out != null) {
                    final ItemStack outputCopy = out.getResultItem().copy();

                    if (this.outputInv.insertItem(0, outputCopy, false).isEmpty()) {
                        this.setProcessingTime(0);

                        GenericStack fluid = this.fluidInv.getStack(0);
                        FluidStack fluidStack = null;
                        if (fluid != null) {
                            AEKey aeKey = fluid.what();
                            if (aeKey instanceof AEFluidKey key) {
                                fluidStack = key.toStack((int) fluid.amount());
                            }
                        }

                        for (var input : out.getValidInputs()) {
                            for (var x = 0; x < this.inputInv.size(); x++) {
                                var stack = this.inputInv.getStackInSlot(x);
                                if (input.checkType(stack)) {
                                    input.consume(stack);
                                    this.inputInv.setItemDirect(x, stack);
                                }

                                if (input.isEmpty()) {
                                    break;
                                }
                            }

                            if (fluidStack != null && !input.isEmpty() && input.checkType(fluidStack)) {
                                input.consume(fluidStack);
                            }
                        }

                        if (fluidStack != null) {
                            if (fluidStack.isEmpty()) {
                                this.fluidInv.setStack(0, null);
                            } else {
                                this.fluidInv.setStack(
                                        0,
                                        new GenericStack(
                                                Objects.requireNonNull(AEFluidKey.of(fluidStack)),
                                                fluidStack.getAmount()));
                            }
                        }
                    }
                }

                this.cachedTask = null;
                this.setWorking(false);
            }
        }

        if (this.pushOutResult()) {
            return TickRateModulation.URGENT;
        }

        return this.hasCraftWork()
                ? TickRateModulation.URGENT
                : this.hasAutoExportWork() ? TickRateModulation.SLOWER : TickRateModulation.SLEEP;
    }

    private boolean pushOutResult() {
        if (!this.hasAutoExportWork()) {
            return false;
        }

        var pushSides = EnumSet.allOf(Direction.class);

        for (var dir : pushSides) {
            var target = InternalInventory.wrapExternal(level, getBlockPos().relative(dir), dir.getOpposite());

            if (target != null) {
                int startItems = this.outputInv.getStackInSlot(0).getCount();
                this.outputInv.insertItem(0, target.addItems(this.outputInv.extractItem(0, 64, false)), false);
                int endItems = this.outputInv.getStackInSlot(0).getCount();

                if (startItems != endItems) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.configManager;
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.fluidInv.writeToChildTag(data, "tank", registries);
        this.upgrades.writeToNBT(data, "upgrades", registries);
        this.configManager.writeToNBT(data, registries);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.fluidInv.readFromChildTag(data, "tank", registries);
        this.upgrades.readFromNBT(data, "upgrades", registries);
        this.configManager.readFromNBT(data, registries);
    }

    @Override
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        var c = super.readFromStream(data);

        var oldWorking = isWorking();
        var newWorking = data.readBoolean();

        if (oldWorking != newWorking && newWorking) {
            setWorking(true);
        }

        for (int i = 0; i < this.inv.size(); i++) {
            this.inv.setItemDirect(i, ItemStack.OPTIONAL_STREAM_CODEC.decode(data));
        }
        this.cachedTask = null;

        return c;
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);

        for (int i = 0; i < this.inv.size(); i++) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(data, this.inv.getStackInSlot(i));
        }
    }

    private void onConfigChanged(IConfigManager manager, Setting<?> setting) {
        if (setting == Settings.AUTO_EXPORT) {
            getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }

        saveChanges();
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);

        for (var upgrade : upgrades) {
            drops.add(upgrade);
        }
        var fluid = this.fluidInv.getStack(0);
        if (fluid != null) {
            fluid.what().addDrops(fluid.amount(), drops, level, pos);
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.fluidInv.clear();
        this.upgrades.clear();
    }
}
