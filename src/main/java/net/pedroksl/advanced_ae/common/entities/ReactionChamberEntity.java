package net.pedroksl.advanced_ae.common.entities;

import java.util.*;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.pedroksl.advanced_ae.common.blocks.ReactionChamberBlock;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.recipes.ReactionChamberRecipe;
import net.pedroksl.advanced_ae.recipes.ReactionChamberRecipes;
import net.pedroksl.advanced_ae.xmod.Addons;
import net.pedroksl.advanced_ae.xmod.appflux.AppliedFluxPlugin;
import net.pedroksl.ae2addonlib.api.IDirectionalOutputHost;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.config.*;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.me.storage.CompositeStorage;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.automation.StackWorldBehaviors;
import appeng.util.SettingsFrom;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.AEItemFilters;

public class ReactionChamberEntity extends AENetworkedPoweredBlockEntity
        implements IGridTickable, IUpgradeableObject, IConfigurableObject, IDirectionalOutputHost {
    private static final int MAX_INPUT_SLOTS = 9;
    private static final int MAX_PROCESSING_STEPS = 200;
    private static final int MAX_POWER_STORAGE = 500000;
    private static final int MAX_TANK_CAPACITY = 16000;

    private final IUpgradeInventory upgrades;
    private final IConfigManager configManager;

    private final AppEngInternalInventory inputInv = new AppEngInternalInventory(this, MAX_INPUT_SLOTS, 64);
    private final AppEngInternalInventory outputInv = new AppEngInternalInventory(this, 1, 64);
    private final InternalInventory inv = new CombinedInternalInventory(this.inputInv, this.outputInv);

    private final FilteredInternalInventory inputExposed =
            new FilteredInternalInventory(this.inputInv, AEItemFilters.INSERT_ONLY);
    private final FilteredInternalInventory outputExposed =
            new FilteredInternalInventory(this.outputInv, AEItemFilters.EXTRACT_ONLY);
    private final InternalInventory invExposed = new CombinedInternalInventory(this.inputExposed, this.outputExposed);

    private final CustomGenericInv fluidInv =
            new CustomGenericInv(Set.of(AEKeyType.fluids()), this::onChangeTank, GenericStackInv.Mode.STORAGE, 2);

    private boolean working = false;
    private int processingTime = 0;
    private boolean dirty = false;

    private ReactionChamberRecipe cachedTask = null;

    private EnumSet<RelativeSide> allowedOutputs = EnumSet.noneOf(RelativeSide.class);

    @SuppressWarnings("UnstableApiUsage")
    private final HashMap<Direction, Map<AEKeyType, ExternalStorageStrategy>> exportStrategies = new HashMap<>();

    private boolean showWarning = false;

    public ReactionChamberEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        this.getMainNode().setIdlePowerUsage(0).addService(IGridTickable.class, this);
        this.setInternalMaxPower(MAX_POWER_STORAGE);

        this.fluidInv.setCapacity(AEKeyType.fluids(), MAX_TANK_CAPACITY);

        this.upgrades = UpgradeInventories.forMachine(AAEBlocks.REACTION_CHAMBER, 4, this::saveChanges);

        this.configManager = IConfigManager.builder(this::onConfigChanged)
                .registerSetting(Settings.AUTO_EXPORT, YesNo.NO)
                .build();

        this.setPowerSides(getGridConnectableSides(getOrientation()));
    }

    public boolean isWorking() {
        return this.working;
    }

    public void setWorking(boolean working) {
        if (working != this.working) {
            updateBlockState(working);
            this.markForUpdate();
        }
        this.working = working;
    }

    private void updateBlockState(boolean working) {
        if (this.level == null || this.notLoaded() || this.isRemoved()) {
            return;
        }

        final BlockState current = this.level.getBlockState(this.worldPosition);
        if (current.getBlock() instanceof ReactionChamberBlock) {
            final BlockState newState = current.setValue(ReactionChamberBlock.WORKING, working);

            if (current != newState) {
                this.level.setBlock(this.worldPosition, newState, Block.UPDATE_CLIENTS);
            }
        }
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

        data.putBoolean("working", isWorking());
    }

    @Override
    protected void loadVisualState(CompoundTag data) {
        super.loadVisualState(data);

        setWorking(data.getBoolean("working"));
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

    public void setShowWarning(boolean show) {
        this.showWarning = show;
    }

    public boolean showWarning() {
        return this.showWarning;
    }

    @Override
    public EnumSet<RelativeSide> getAllowedOutputs() {
        return this.allowedOutputs;
    }

    public FluidStack getFluidStack() {
        var fluid = this.fluidInv.getStack(1);
        FluidStack fluidStack = null;
        if (fluid != null) {
            AEKey aeKey = fluid.what();
            if (aeKey instanceof AEFluidKey key) {
                fluidStack = key.toStack((int) fluid.amount());
            }
        }
        return fluidStack;
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
        return (!this.outputInv.getStackInSlot(0).isEmpty()
                        || this.fluidInv.getStack(0) != null
                        || this.fluidInv.getAmount(0) > 0)
                && configManager.getSetting(Settings.AUTO_EXPORT) == YesNo.YES;
    }

    private boolean hasCraftWork() {
        var task = this.getTask();
        if (task != null) {
            // Only process if the result would fit.
            if (task.isItemOutput()) {
                return this.outputInv.insertItem(0, task.getResultItem(), true).isEmpty();
            } else {
                var fluid = task.getResultFluid();
                return this.fluidInv.canAdd(0, AEFluidKey.of(fluid), fluid.getAmount());
            }
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
        List<ItemStack> inputs = new ArrayList<>();
        for (var x = 0; x < this.inputInv.size(); x++) {
            inputs.add(this.inputInv.getStackInSlot(x));
        }

        return ReactionChamberRecipes.findRecipe(level, inputs, this.fluidInv.getStack(1));
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
            this.markForUpdate();
            this.dirty = false;
        }

        if (this.hasCraftWork()) {
            this.setWorking(true);
            getMainNode().ifPresent(grid -> {
                IEnergyService eg = grid.getEnergyService();
                IEnergySource src = this;

                final int speedFactor =
                        switch (this.upgrades.getInstalledUpgrades(AEItems.SPEED_CARD)) {
                            default -> 2; // 116 ticks
                            case 1 -> 3; // 83 ticks
                            case 2 -> 5; // 56 ticks
                            case 3 -> 10; // 36 ticks
                            case 4 -> 50; // 20 ticks
                        };

                final int progressReq = MAX_PROCESSING_STEPS - this.getProcessingTime();
                final float powerRatio = progressReq < speedFactor ? (float) progressReq / speedFactor : 1;
                final int requiredTicks = Mth.ceil((float) MAX_PROCESSING_STEPS / speedFactor);
                final int powerConsumption = Mth.floor(((float) getTask().getEnergy() / requiredTicks) * powerRatio);
                final double powerThreshold = powerConsumption - 0.01;

                // Try to recharge from fe cells
                if (Addons.APPFLUX.isLoaded()) {
                    AppliedFluxPlugin.rechargeEnergyStorage(
                            grid,
                            Integer.MAX_VALUE,
                            IActionSource.ofMachine(this),
                            this.getEnergyStorage(Direction.UP));
                }

                double powerReq = this.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);

                if (powerReq <= powerThreshold) {
                    src = eg;
                    var oldPowerReq = powerReq;
                    powerReq = eg.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);
                    if (oldPowerReq > powerReq) {
                        src = this;
                        powerReq = oldPowerReq;
                    }
                }

                if (powerReq > powerThreshold) {
                    src.extractAEPower(powerConsumption, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    this.setProcessingTime(this.getProcessingTime() + speedFactor);
                    setShowWarning(false);
                } else if (powerReq != 0) {
                    var progressRatio = src == this
                            ? powerReq / powerConsumption
                            : (powerReq - 10 * eg.getIdlePowerUsage()) / powerConsumption;
                    var factor = Mth.floor(progressRatio * speedFactor);

                    if (factor > 1) {
                        var extracted = src.extractAEPower(
                                (double) (powerConsumption * factor) / speedFactor,
                                Actionable.MODULATE,
                                PowerMultiplier.CONFIG);
                        var actualFactor = (int) Math.floor(extracted / powerConsumption * speedFactor);
                        this.setProcessingTime(this.getProcessingTime() + actualFactor);
                    }
                    // Add warning
                    setShowWarning(true);
                }
            });

            if (this.getProcessingTime() >= this.getMaxProcessingTime()) {
                this.setProcessingTime(0);
                final ReactionChamberRecipe out = this.getTask();
                if (out != null) {
                    final ItemStack output = out.getResultItem();
                    final FluidStack fluidOut = out.getResultFluid();

                    if ((out.isItemOutput()
                                    && this.outputInv
                                            .insertItem(0, output, false)
                                            .isEmpty())
                            || (!out.isItemOutput()
                                    && this.fluidInv.add(0, AEFluidKey.of(fluidOut), fluidOut.getAmount())
                                            >= fluidOut.getAmount() - 0.01)) {
                        this.setProcessingTime(0);

                        GenericStack fluid = this.fluidInv.getStack(1);
                        FluidStack fluidStack = null;
                        if (fluid != null) {
                            AEKey aeKey = fluid.what();
                            if (aeKey instanceof AEFluidKey key) {
                                fluidStack = key.toStack((int) fluid.amount());
                            }
                        }

                        for (var input : out.getValidInputs()) {
                            for (int x = 0; x < this.inputInv.size(); x++) {
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
                                this.fluidInv.setStack(1, null);
                            } else {
                                this.fluidInv.setStack(
                                        1,
                                        new GenericStack(
                                                Objects.requireNonNull(AEFluidKey.of(fluidStack)),
                                                fluidStack.getAmount()));
                            }
                        }
                    }
                }
                this.saveChanges();
                this.cachedTask = null;
                this.setWorking(false);
            }
        } else {
            setShowWarning(false);
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

        var orientation = this.getOrientation();

        for (var side : allowedOutputs) {
            var dir = orientation.getSide(side);
            var target = getTarget(dir);

            if (target != null) {
                var source = IActionSource.ofMachine(this);
                var movedStacks = false;
                var genStack = GenericStack.fromItemStack(this.outputInv.getStackInSlot(0));
                if (genStack != null && genStack.what() != null) {
                    var extractedStack = this.outputInv.extractItem(0, 64, false);
                    var inserted =
                            target.insert(genStack.what(), extractedStack.getCount(), Actionable.MODULATE, source);
                    extractedStack.setCount(extractedStack.getCount() - (int) inserted);
                    this.outputInv.insertItem(0, extractedStack, false);
                    movedStacks |= inserted > 0;
                }

                var outFluid = this.fluidInv.getStack(0);
                if (outFluid != null && outFluid.what() != null) {
                    var extracted = this.fluidInv.extract(0, outFluid.what(), outFluid.amount(), Actionable.MODULATE);
                    var inserted = target.insert(outFluid.what(), extracted, Actionable.MODULATE, source);
                    this.fluidInv.add(0, ((AEFluidKey) outFluid.what()), (int) (extracted - inserted));

                    if (this.fluidInv.getAmount(0) == 0) clearFluidOut();

                    movedStacks |= inserted > 0;
                }

                if (movedStacks) {
                    return true;
                }
            }
        }

        return false;
    }

    @SuppressWarnings("UnstableApiUsage")
    private CompositeStorage getTarget(Direction dir) {
        if (this.exportStrategies.get(dir) == null) {
            var be = this.getBlockEntity();
            this.exportStrategies.put(
                    dir,
                    StackWorldBehaviors.createExternalStorageStrategies(
                            (ServerLevel) be.getLevel(), be.getBlockPos().relative(dir), dir.getOpposite()));
        }

        var externalStorages = new IdentityHashMap<AEKeyType, MEStorage>(2);
        for (var entry : exportStrategies.get(dir).entrySet()) {
            var wrapper = entry.getValue().createWrapper(false, () -> {});
            if (wrapper != null) {
                externalStorages.put(entry.getKey(), wrapper);
            }
        }

        if (!externalStorages.isEmpty()) {
            return new CompositeStorage(externalStorages);
        }
        return null;
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

        ListTag outputTags = new ListTag();
        for (var side : this.allowedOutputs) {
            outputTags.add(StringTag.valueOf(side.name()));
        }
        data.put("outputs", outputTags);

        this.upgrades.writeToNBT(data, "upgrades", registries);
        this.configManager.writeToNBT(data, registries);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.fluidInv.readFromChildTag(data, "tank", registries);

        this.allowedOutputs.clear();
        ListTag outputTags = data.getList("outputs", Tag.TAG_STRING);
        if (!outputTags.isEmpty()) {
            for (var x = 0; x < outputTags.size(); x++) {
                RelativeSide side = Enum.valueOf(RelativeSide.class, outputTags.getString(x));
                this.allowedOutputs.add(side);
            }
        }

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

        this.fluidInv.setStack(0, GenericStack.readBuffer(data));
        this.fluidInv.setStack(1, GenericStack.readBuffer(data));
        this.cachedTask = null;

        return c;
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);

        data.writeBoolean(isWorking());
        for (int i = 0; i < this.inv.size(); i++) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(data, this.inv.getStackInSlot(i));
        }

        GenericStack.writeBuffer(this.fluidInv.getStack(0), data);
        GenericStack.writeBuffer(this.fluidInv.getStack(1), data);
    }

    @Override
    public void exportSettings(SettingsFrom mode, DataComponentMap.Builder builder, @Nullable Player player) {
        super.exportSettings(mode, builder, player);

        if (mode == SettingsFrom.MEMORY_CARD) {
            var outputs = getAllowedOutputs();
            var list = new ArrayList<Integer>();
            for (var output : outputs) {
                list.add(output.getUnrotatedSide().get3DDataValue());
            }
            builder.set(AAEComponents.EXPORTED_ALLOWED_SIDES, list);
        }
    }

    @Override
    public void importSettings(SettingsFrom mode, DataComponentMap input, @Nullable Player player) {
        super.importSettings(mode, input, player);

        if (mode == SettingsFrom.MEMORY_CARD) {
            var list = input.get(AAEComponents.EXPORTED_ALLOWED_SIDES);
            if (list != null) {
                var level = getLevel();
                if (level != null) {
                    var be = level.getBlockEntity(getBlockPos());
                    if (be instanceof IDirectionalOutputHost host) {

                        var outputs = EnumSet.noneOf(RelativeSide.class);
                        for (var item : list) {
                            outputs.add(RelativeSide.fromUnrotatedSide(Direction.from3DDataValue(item)));
                        }
                        host.updateOutputSides(outputs);
                    }
                }
            }
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

        for (var i = 0; i < this.fluidInv.size(); i++) {
            var fluid = this.fluidInv.getStack(i);
            if (fluid != null) {
                fluid.what().addDrops(fluid.amount(), drops, level, pos);
            }
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.fluidInv.clear();
        this.upgrades.clear();
    }

    public void clearFluid() {
        this.fluidInv.clear(1);
    }

    public void clearFluidOut() {
        this.fluidInv.clear(0);
    }

    @Override
    public void updateOutputSides(EnumSet<RelativeSide> allowedOutputs) {
        this.allowedOutputs = allowedOutputs;
        saveChanges();
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu iSubMenu) {
        MenuOpener.returnTo(AAEMenus.REACTION_CHAMBER.get(), player, MenuLocators.forBlockEntity(this));
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AAEBlocks.REACTION_CHAMBER.asItem());
    }

    private static class CustomGenericInv extends GenericStackInv {
        public CustomGenericInv(Set<AEKeyType> supportedKeyTypes, @Nullable Runnable listener, Mode mode, int size) {
            super(supportedKeyTypes, listener, mode, size);
        }

        @Override
        public boolean isAllowedIn(int slot, AEKey what) {
            if (slot == 0) return false;

            return super.isAllowedIn(slot, what);
        }

        @Override
        public long extract(int slot, AEKey what, long amount, Actionable mode) {
            if (slot == 1) return 0L;

            return super.extract(slot, what, amount, mode);
        }

        public boolean canAdd(int slot, AEFluidKey key, int amount) {
            var stack = this.getStack(slot);
            if (stack == null) return true;
            if (!stack.what().equals(key)) return false;
            return stack.amount() + amount <= this.getMaxAmount(key);
        }

        public int add(int slot, AEFluidKey key, int amount) {
            if (!canAdd(slot, key, amount)) return 0;

            var stack = this.getStack(slot);
            var newAmount = amount;
            if (stack != null) newAmount += (int) stack.amount();
            assert stack != null;
            this.setStack(slot, new GenericStack(key, newAmount));
            return amount;
        }

        public void clear(int slot) {
            boolean changed = this.stacks[slot] != null;
            this.setStack(slot, null);

            if (changed) {
                onChange();
            }
        }
    }
}
