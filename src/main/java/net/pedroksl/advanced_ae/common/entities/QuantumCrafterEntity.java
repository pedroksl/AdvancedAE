package net.pedroksl.advanced_ae.common.entities;

import java.util.*;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.api.IDirectionalOutputHost;
import net.pedroksl.advanced_ae.common.blocks.QuantumCrafterBlock;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;

import appeng.api.config.*;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.inventories.ItemTransfer;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.StorageHelper;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.crafting.pattern.AECraftingPattern;
import appeng.me.helpers.MachineSource;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.AEItemFilters;

public class QuantumCrafterEntity extends AENetworkedPoweredBlockEntity
        implements IGridTickable, IUpgradeableObject, IConfigurableObject, IDirectionalOutputHost {
    private static final int MAX_POWER_STORAGE = 8000;
    private static final int MAX_CRAFT_AMOUNT = 1024;
    private static final int MAX_OUTPUT_INV_SIZE = 1024;

    private final IUpgradeInventory upgrades;
    private final IConfigManager configManager;

    private final AppEngInternalInventory patternInv = new AppEngInternalInventory(this, 9, 1);
    private final AppEngInternalInventory outputInv = new AppEngInternalInventory(this, 18, MAX_OUTPUT_INV_SIZE);
    private final InternalInventory inv = new CombinedInternalInventory(this.patternInv, this.outputInv);

    private final FilteredInternalInventory inputExposed =
            new FilteredInternalInventory(this.patternInv, AEItemFilters.INSERT_ONLY);
    private final FilteredInternalInventory outputExposed =
            new FilteredInternalInventory(this.outputInv, AEItemFilters.EXTRACT_ONLY);
    private final InternalInventory invExposed = new CombinedInternalInventory(this.inputExposed, this.outputExposed);

    private boolean working = false;
    private boolean dirty = false;
    private int currentCraftJob = 0;
    private YesNo lastRedstoneState;

    private IActionSource mySrc;
    private boolean isActive = false;

    private final List<CraftingJob> craftingJobs = new ArrayList<>();

    private EnumSet<RelativeSide> allowedOutputs = EnumSet.allOf(RelativeSide.class);

    public QuantumCrafterEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        this.getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .setIdlePowerUsage(0)
                .addService(IGridTickable.class, this);
        this.setInternalMaxPower(MAX_POWER_STORAGE);

        this.upgrades = UpgradeInventories.forMachine(AAEBlocks.QUANTUM_CRAFTER, 5, this::saveChanges);

        this.configManager = IConfigManager.builder(this::onConfigChanged)
                .registerSetting(AAESettings.ME_EXPORT, YesNo.NO)
                .registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE)
                .build();

        this.setPowerSides(getGridConnectableSides(getOrientation()));

        this.mySrc = new MachineSource(this);
        this.lastRedstoneState = YesNo.UNDECIDED;
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
        final BlockState newState = current.setValue(QuantumCrafterBlock.WORKING, working);

        if (current != newState) {
            this.level.setBlock(this.worldPosition, newState, Block.UPDATE_CLIENTS);
        }
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

    public InternalInventory getPatternInventory() {
        return this.patternInv;
    }

    public InternalInventory getOutputInv() {
        return this.outputInv;
    }

    @Override
    public InternalInventory getInternalInventory() {
        return inv;
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

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        if (inv == this.patternInv) {
            makeCraftingRecipeList();
        }

        getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
    }

    private void makeCraftingRecipeList() {
        craftingJobs.clear();
        for (var x = 0; x < this.patternInv.size(); x++) {
            ItemStack is = this.patternInv.getStackInSlot(x);
            if (!is.isEmpty()) {
                IPatternDetails details = PatternDetailsHelper.decodePattern(is, this.getLevel());
                if (details instanceof AECraftingPattern craftPattern) {
                    craftingJobs.add(new CraftingJob(craftPattern));
                }
            }
        }
    }

    private boolean hasAutoExportWork() {
        for (var x = 0; x < this.outputInv.size(); x++) {
            if (!this.outputInv.getStackInSlot(x).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean isExportToMe() {
        return configManager.getSetting(AAESettings.ME_EXPORT) == YesNo.YES;
    }

    private boolean hasWork() {
        if (this.isEnabled()) {

            return !this.patternInv.isEmpty();
        }

        return false;
    }

    private boolean hasCraftWork() {
        if (this.craftingJobs.isEmpty()) {
            makeCraftingRecipeList();
        }

        for (var job : this.craftingJobs) {
            if (maximumCraftableAmount(job) > 0) {
                if (hasAvailableOutputStorage(job)) {
                    return true;
                }
            }
        }
        return false;
    }

    private int maximumCraftableAmount(CraftingJob job) {
        if (this.getGridNode() == null) return 0;

        var grid = this.getGridNode().getGrid();

        int totalCrafts = MAX_CRAFT_AMOUNT;
        for (var input : job.pattern.getInputs()) {
            var minStock = job.minimumInputToKeep(input);

            var success = false;
            for (var genInput : input.getPossibleInputs()) {
                var inputAmount = input.getMultiplier() * genInput.amount();

                var toExtract = job.requiredInputTotal(genInput, totalCrafts);
                if (job.isInputConsumed(genInput)) {
                    toExtract += minStock;
                }

                var extracted = grid.getStorageService()
                        .getInventory()
                        .extract(genInput.what(), toExtract, Actionable.SIMULATE, IActionSource.ofMachine(this));

                if (!job.isInputConsumed(genInput) && extracted >= toExtract) {
                    success = true;
                    break;
                } else if (extracted > minStock) {
                    success = true;
                    if (extracted > Integer.MAX_VALUE) {
                        extracted = Integer.MAX_VALUE;
                    }
                    int possibleCrafts = (int) Math.floor((double) (extracted - minStock) / inputAmount);
                    totalCrafts = Math.min(possibleCrafts, totalCrafts);
                    break;
                }
            }

            if (!success) {
                return 0;
            }
        }

        var output = job.pattern.getOutputs().getFirst();
        var maxStock = job.limitMaxOutput;
        var extracted = grid.getStorageService()
                .getInventory()
                .extract(output.what(), maxStock, Actionable.SIMULATE, IActionSource.ofMachine(this));
        var amountInOutput = 0;
        for (int x = 0; x < this.outputInv.size(); x++) {
            var stack = this.outputInv.getStackInSlot(x);
            if (stack.is(GenericStack.wrapInItemStack(output).getItem())) {
                amountInOutput += stack.getCount();
            }
        }

        int limitByOutput = (int) Math.floor((double) (maxStock - extracted - amountInOutput) / output.amount());
        totalCrafts = Math.max(0, Math.min(totalCrafts, limitByOutput));
        if (extracted < maxStock) {
            return totalCrafts;
        } else {
            return (int) Math.floor((double) extracted / output.amount());
        }
    }

    private boolean hasAvailableOutputStorage(CraftingJob job) {
        for (var output : job.pattern.getOutputs()) {
            if (output.what() instanceof AEItemKey key) {
                var stack = key.toStack();
                for (var x = 0; x < this.outputInv.size(); x++) {
                    stack = this.outputInv.insertItem(x, stack, true);
                    if (stack.isEmpty()) {
                        break;
                    }
                }

                if (!stack.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(1, 20, !hasWork());
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode iGridNode, int i) {
        if (!this.getMainNode().isActive()) {
            setWorking(false);
            return TickRateModulation.IDLE;
        }

        if (this.hasCraftWork()) {
            final int speedFactor =
                    switch (this.upgrades.getInstalledUpgrades(AEItems.SPEED_CARD)) {
                        default -> 1;
                        case 1 -> 8;
                        case 2 -> 16;
                        case 3 -> 32;
                        case 4 -> 64;
                    };

            this.setWorking(true);
            getMainNode().ifPresent(grid -> {
                IEnergyService eg = grid.getEnergyService();
                IEnergySource src = this;

                final int powerConsumption = 10 * speedFactor;
                final double powerThreshold = (double) powerConsumption - 0.01;
                double powerReq = this.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);

                if (powerReq <= powerThreshold) {
                    src = eg;
                    powerReq = eg.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);
                }

                if (powerReq > powerThreshold) {
                    src.extractAEPower(powerConsumption, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    performCrafts(speedFactor);
                }
            });
        } else {
            setWorking(false);
        }

        if (this.pushOutResult()) {
            return TickRateModulation.URGENT;
        }

        return this.hasCraftWork()
                ? TickRateModulation.URGENT
                : this.hasAutoExportWork() ? TickRateModulation.SLOWER : TickRateModulation.SLEEP;
    }

    private void performCrafts(int maxCrafts) {
        int totalCrafts = 0;
        int initialCraft = this.currentCraftJob;
        while (totalCrafts < maxCrafts) {
            var job = getNextJob();
            if (job == null) break;

            final int craftAmount = maximumCraftableAmount(job);
            final int craftsLeft = maxCrafts - totalCrafts;
            final int toCraft = Math.min(craftAmount, craftsLeft);

            totalCrafts += performCraft(job, toCraft);

            if (this.currentCraftJob == initialCraft) {
                break;
            }
        }
    }

    private int performCraft(CraftingJob job, int toCraft) {
        if (this.getGridNode() == null) return 0;

        List<Long> requiredPerCraft = new ArrayList<>();
        List<GenericStack> extractedItems = new ArrayList<>();
        var grid = this.getGridNode().getGrid();
        var energy = grid.getEnergyService();
        var storage = grid.getStorageService();

        // Extract all the items from ME Storage
        for (var input : job.pattern.getInputs()) {
            for (var genInput : input.getPossibleInputs()) {
                var inputAmount = input.getMultiplier() * genInput.amount();

                var toExtract = job.requiredInputTotal(genInput, toCraft);
                var extracted = StorageHelper.poweredExtraction(
                        energy, storage.getInventory(), genInput.what(), toExtract, IActionSource.ofMachine(this));
                if (extracted >= inputAmount) {
                    requiredPerCraft.add(inputAmount);
                    extractedItems.add(new GenericStack(genInput.what(), extracted));
                    break;
                }
            }
        }

        // Check how many complete recipes were extracted
        var completeRecipes = extractedItems.size() == job.pattern.getInputs().length ? toCraft : 0;
        for (var x = 0; x < extractedItems.size(); x++) {
            if (!job.isInputConsumed(extractedItems.get(x))) {
                continue;
            }
            var required = requiredPerCraft.get(x);
            var extracted = extractedItems.get(x).amount();
            var recipes = (int) (extracted / required);
            completeRecipes = Math.min(completeRecipes, recipes);
        }

        // Create outputs and put them in output slots
        for (var output : job.pattern.getOutputs()) {
            if (output.what() instanceof AEItemKey key) {
                var stack = key.toStack();
                stack.setCount(stack.getCount() * completeRecipes);
                for (var x = 0; x < this.outputInv.size(); x++) {
                    stack = this.outputInv.insertItem(x, stack, Actionable.MODULATE.isSimulate());
                    if (stack.isEmpty()) {
                        break;
                    }
                }
            }
        }

        // output remaining items from the craft
        for (var stack : job.remainingItems) {
            var newStack = stack.copy();
            if (job.isStackAnInput(stack)) {
                continue;
            }
            newStack.setCount(stack.getCount() * completeRecipes);
            for (var x = 0; x < this.outputInv.size(); x++) {
                newStack = this.outputInv.insertItem(x, newStack, Actionable.MODULATE.isSimulate());
                if (newStack.isEmpty()) {
                    break;
                }
            }
        }

        // Return unused items back to storage
        for (var x = 0; x < extractedItems.size(); x++) {
            var required = requiredPerCraft.get(x);
            var input = extractedItems.get(x);
            var toReturn = input.amount();
            if (job.isInputConsumed(input)) {
                toReturn -= (required * completeRecipes);
            }
            var successfulReturn = storage.getInventory()
                    .insert(extractedItems.get(x).what(), toReturn, Actionable.MODULATE, IActionSource.ofMachine(this));

            // Failed to add to ME System, try to return items to output Inventory
            if (successfulReturn < toReturn) {
                var stack = ((AEItemKey) input.what()).toStack();
                stack.setCount(Math.max(0, (int) (toReturn - successfulReturn)));
                for (var y = 0; y < this.outputInv.size(); y++) {
                    stack = this.outputInv.insertItem(y, stack, Actionable.MODULATE.isSimulate());
                    if (stack.isEmpty()) {
                        break;
                    }
                }
            }
        }

        return completeRecipes;
    }

    private @Nullable CraftingJob getNextJob() {
        try {
            var job = craftingJobs.get(this.currentCraftJob++);
            if (this.currentCraftJob >= craftingJobs.size()) {
                this.currentCraftJob = 0;
            }
            return job;
        } catch (IndexOutOfBoundsException e) {
            if (this.currentCraftJob >= craftingJobs.size()) {
                this.currentCraftJob = 0;
            }
            return null;
        }
    }

    private boolean pushOutResult() {
        if (!this.hasAutoExportWork()) {
            return false;
        }

        if (isExportToMe()) {
            return exportToMe();
        } else {
            return exportToAdjacentBlocks();
        }
    }

    private boolean exportToMe() {
        if (this.getGridNode() == null) return false;

        var storage = this.getGridNode().getGrid().getStorageService();
        var inventory = storage.getInventory();

        var success = false;
        for (var x = 0; x < this.outputInv.size(); x++) {
            var extractStack = this.outputInv.extractItem(x, MAX_OUTPUT_INV_SIZE, false);
            var inserted = inventory.insert(
                    AEItemKey.of(extractStack),
                    extractStack.getCount(),
                    Actionable.MODULATE,
                    IActionSource.ofMachine(this));
            extractStack.setCount(extractStack.getCount() - (int) inserted);
            this.outputInv.insertItem(x, extractStack, false);

            if (inserted > 0) {
                success = true;
            }
        }

        return success;
    }

    private boolean exportToAdjacentBlocks() {
        var orientation = this.getOrientation();

        List<ItemTransfer> invMap = new ArrayList<>();
        for (var side : allowedOutputs) {
            var dir = orientation.getSide(side);
            var targetPos = getBlockPos().relative(dir);
            BlockEntity te = level.getBlockEntity(targetPos);
            if (te instanceof ReactionChamberEntity) continue;

            var target = InternalInventory.wrapExternal(level, getBlockPos().relative(dir), dir.getOpposite());
            if (target != null) {
                invMap.add(target);
            }
        }

        var success = false;
        for (var x = 0; x < this.outputInv.size(); x++) {
            int startItems = this.outputInv.getStackInSlot(x).getCount();
            for (var target : invMap) {
                this.outputInv.insertItem(
                        x, target.addItems(this.outputInv.extractItem(x, MAX_OUTPUT_INV_SIZE, false)), false);
                if (this.outputInv.getStackInSlot(x).getCount() == 0) {
                    break;
                }
            }
            int endItems = this.outputInv.getStackInSlot(x).getCount();

            if (startItems != endItems) {
                success = true;
            }
        }
        return success;
    }

    @Override
    public IConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);

        for (var upgrade : upgrades) {
            drops.add(upgrade);
        }
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.SMART;
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);

        ListTag outputTags = new ListTag();
        for (var side : this.allowedOutputs) {
            outputTags.add(StringTag.valueOf(side.name()));
        }
        data.put("outputSides", outputTags);

        this.upgrades.writeToNBT(data, "upgrades", registries);
        this.configManager.writeToNBT(data, registries);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);

        this.allowedOutputs.clear();
        ListTag outputTags = data.getList("outputSides", Tag.TAG_STRING);
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

        setWorking(data.readBoolean());
        final boolean isActive = data.readBoolean();
        c = isActive != this.isActive || c;
        this.isActive = isActive;

        return c;
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);

        data.writeBoolean(isWorking());
        data.writeBoolean(isActive());
    }

    private void onConfigChanged() {
        getMainNode().ifPresent((grid, node) -> {
            if (this.hasWork()) {
                grid.getTickManager().wakeDevice(node);
            } else {
                grid.getTickManager().sleepDevice(node);
            }
        });

        saveChanges();
    }

    public void updateRedstoneState() {
        final YesNo currentState = this.level.getBestNeighborSignal(this.worldPosition) != 0 ? YesNo.YES : YesNo.NO;
        if (this.lastRedstoneState != currentState) {
            this.lastRedstoneState = currentState;
            this.onConfigChanged();
        }
    }

    private boolean getRedstoneState() {
        if (this.lastRedstoneState == YesNo.UNDECIDED) {
            this.updateRedstoneState();
        }

        return this.lastRedstoneState == YesNo.YES;
    }

    private boolean isEnabled() {
        if (!upgrades.isInstalled(AEItems.REDSTONE_CARD)) {
            return true;
        }

        final RedstoneMode rs = this.configManager.getSetting(Settings.REDSTONE_CONTROLLED);
        if (rs == RedstoneMode.HIGH_SIGNAL) {
            return this.getRedstoneState();
        }
        return !this.getRedstoneState();
    }

    public boolean isActive() {
        if (level != null && !level.isClientSide) {
            return this.getMainNode().isOnline();
        } else {
            return this.isActive;
        }
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        if (reason != IGridNodeListener.State.GRID_BOOT) {
            this.markForUpdate();
        }
    }

    @Override
    public EnumSet<RelativeSide> getAllowedOutputs() {
        return allowedOutputs;
    }

    @Override
    public void updateOutputSides(EnumSet<RelativeSide> allowedOutputs) {
        this.allowedOutputs = allowedOutputs;
        saveChanges();
    }

    @Override
    public ItemStack getAdjacentBlock(RelativeSide side) {
        var dir = getOrientation().getSide(side);
        BlockPos blockPos = getBlockPos().relative(dir);

        Level level = getLevel();
        if (level == null) {
            return null;
        }

        BlockState blockState = level.getBlockState(blockPos);
        ItemStack itemStack = blockState.getBlock().asItem().getDefaultInstance();
        if (blockState.hasBlockEntity()) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity != null) {
                blockEntity.saveToItem(itemStack, level.registryAccess());
            }
        }
        return itemStack;
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu iSubMenu) {
        MenuOpener.returnTo(AAEMenus.QUANTUM_CRAFTER, player, MenuLocators.forBlockEntity(this));
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AAEBlocks.QUANTUM_CRAFTER.asItem());
    }

    private record CraftingJob(
            AECraftingPattern pattern,
            List<ItemStack> remainingItems,
            Map<IPatternDetails.IInput, Integer> keepMinInput,
            long limitMaxOutput) {

        public static final int DEFAULT_KEEP_INPUT = 0;
        public static final long DEFAULT_KEEP_OUTPUT = Long.MAX_VALUE;
        private static final int GRID_SIZE = 3;

        public CraftingJob(AECraftingPattern pattern) {
            this(pattern, getRemainingItems(pattern), createNewInputMap(pattern), DEFAULT_KEEP_OUTPUT);
        }

        public int minimumInputToKeep(IPatternDetails.IInput stack) {
            if (keepMinInput.containsKey(stack)) {
                return keepMinInput.get(stack);
            }

            return DEFAULT_KEEP_INPUT;
        }

        public void setMinimumInputToKeep(IPatternDetails.IInput stack, int value) {
            if (keepMinInput.containsKey(stack)) {
                keepMinInput.put(stack, value);
            }
        }

        public int requiredInputTotal(GenericStack input, int toCraft) {
            var multiplier = 0;
            for (var i : pattern.getInputs()) {
                for (var genInput : i.getPossibleInputs()) {
                    if (input.what().matches(genInput)) {
                        multiplier = (int) Math.ceil(i.getMultiplier() * input.amount());
                        break;
                    }
                }
            }

            if (input.what() instanceof AEItemKey key) {
                ItemStack stack = key.toStack();

                for (var item : remainingItems) {
                    if (item.is(stack.getItem())) {
                        return multiplier;
                    }
                }
                return multiplier * toCraft;
            }
            if (input.what() instanceof AEFluidKey) {
                return multiplier * toCraft;
            }
            return 0;
        }

        public boolean isInputConsumed(GenericStack input) {
            for (var item : remainingItems) {
                if (input.what().wrapForDisplayOrFilter().is(item.getItem())) {
                    return false;
                }
            }
            return true;
        }

        public boolean isStackAnInput(ItemStack stack) {
            for (var input : pattern.getInputs()) {
                for (var genInput : input.getPossibleInputs()) {
                    if (genInput.what().wrapForDisplayOrFilter().is(stack.getItem())) {
                        return true;
                    }
                }
            }
            return false;
        }

        private static Map<IPatternDetails.IInput, Integer> createNewInputMap(AECraftingPattern pattern) {
            Map<IPatternDetails.IInput, Integer> map = new HashMap<>();
            for (var input : pattern.getInputs()) {
                map.put(input, DEFAULT_KEEP_INPUT);
            }
            return map;
        }

        private static List<ItemStack> getRemainingItems(AECraftingPattern pattern) {
            List<ItemStack> craftingInput = new ArrayList<>();
            int bucketsToRemove = 0;
            for (var x = 0; x < pattern.getSparseInputs().size(); x++) {
                var input = pattern.getSparseInputs().get(x);
                if (input == null) {
                    craftingInput.add(ItemStack.EMPTY);
                } else if (input.what() instanceof AEItemKey key) {
                    craftingInput.add(key.toStack());
                }
                if (pattern.canSubstituteFluids() && pattern.getValidFluid(x) != null) {
                    bucketsToRemove++;
                }
            }

            var itemList = pattern.getRemainingItems(CraftingInput.of(GRID_SIZE, GRID_SIZE, craftingInput));

            HashMap<Item, Integer> condensedItemList = new HashMap<>();
            for (var item : itemList) {
                condensedItemList.merge(item.getItem(), item.getCount(), Integer::sum);
            }

            List<ItemStack> finalList = new ArrayList<>();
            for (var entry : condensedItemList.entrySet()) {
                if (entry.getKey() != ItemStack.EMPTY.getItem()) {
                    if (bucketsToRemove > 0 && entry.getKey() == Items.BUCKET) {
                        var bucketCount = Math.max(0, entry.getValue() - bucketsToRemove);
                        finalList.add(new ItemStack(entry.getKey(), bucketCount));
                    } else {
                        finalList.add(new ItemStack(entry.getKey(), entry.getValue()));
                    }
                }
            }
            return finalList;
        }
    }
}
