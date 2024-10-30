package net.pedroksl.advanced_ae.common.entities;

import java.util.*;

import com.mojang.datafixers.util.Pair;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.*;
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
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
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
import appeng.api.stacks.AEKey;
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
import appeng.util.SettingsFrom;
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

    private boolean initialized = false;
    private boolean working = false;
    private YesNo lastRedstoneState;

    private final IActionSource mySrc;
    private boolean isActive = false;

    private final List<CraftingJob> craftingJobs = Arrays.asList(new CraftingJob[9]);

    private EnumSet<RelativeSide> allowedOutputs = EnumSet.allOf(RelativeSide.class);
    private final List<Boolean> enabledPatternSlots = Arrays.asList(new Boolean[9]);

    public QuantumCrafterEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        this.getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .setIdlePowerUsage(0)
                .addService(IGridTickable.class, this);
        this.setInternalMaxPower(MAX_POWER_STORAGE);

        this.upgrades = UpgradeInventories.forMachine(AAEBlocks.QUANTUM_CRAFTER, 5, this::saveChanges);

        this.configManager = IConfigManager.builder(this::onConfigChanged)
                .registerSetting(AAESettings.ME_EXPORT, YesNo.YES)
                .registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE)
                .build();

        this.setPowerSides(getGridConnectableSides(getOrientation()));

        this.mySrc = new MachineSource(this);
        this.lastRedstoneState = YesNo.UNDECIDED;

        Collections.fill(enabledPatternSlots, false);
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
        for (var x = 0; x < this.patternInv.size(); x++) {
            var success = false;
            ItemStack is = this.patternInv.getStackInSlot(x);
            if (!is.isEmpty()) {
                IPatternDetails details = PatternDetailsHelper.decodePattern(is, this.getLevel());
                if (details instanceof AECraftingPattern craftPattern) {
                    if (craftingJobs.get(x) != null) {
                        if (craftingJobs.get(x).pattern == null) {
                            craftingJobs.get(x).setPattern(craftPattern);
                        }
                        continue;
                    }
                    craftingJobs.set(x, new CraftingJob(craftPattern));
                    success = true;
                }
            }
            if (!success) {
                craftingJobs.set(x, null);
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
        if (!initialized) {
            makeCraftingRecipeList();
            initialized = true;
        }

        if (this.isEnabled()) {
            return !this.patternInv.isEmpty();
        }

        this.setWorking(false);
        return false;
    }

    private boolean hasCraftWork() {
        if (!initialized) {
            makeCraftingRecipeList();
            initialized = true;
        }

        if (!this.isEnabled()) {
            return false;
        }

        for (var x = 0; x < this.craftingJobs.size(); x++) {
            var job = craftingJobs.get(x);
            if (job == null || job.pattern == null || !enabledPatternSlots.get(x)) continue;

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
        if (job == null || job.pattern == null) return 0;

        var inputs = job.pattern.getInputs();
        var outputs = job.pattern.getOutputs();

        var grid = this.getGridNode().getGrid();

        int totalCrafts = MAX_CRAFT_AMOUNT;
        for (var input : inputs) {
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
                        .extract(genInput.what(), toExtract, Actionable.SIMULATE, this.mySrc);

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

        var output = outputs.getFirst();
        var maxStock = job.limitMaxOutput;
        if (maxStock > 0) {
            var extracted = grid.getStorageService()
                    .getInventory()
                    .extract(output.what(), maxStock, Actionable.SIMULATE, this.mySrc);
            var amountInOutput = 0;
            for (int x = 0; x < this.outputInv.size(); x++) {
                var stack = this.outputInv.getStackInSlot(x);
                if (stack.is(GenericStack.wrapInItemStack(output).getItem())) {
                    amountInOutput += stack.getCount();
                }
            }

            var producedAmount = job.outputAmountPerCraft(output);

            int limitByOutput = (int) Math.floor((double) (maxStock - extracted - amountInOutput) / producedAmount);
            totalCrafts = Math.max(0, Math.min(totalCrafts, limitByOutput));
            if (extracted <= maxStock) {
                return totalCrafts;
            } else {
                return (int) Math.floor((double) extracted / output.amount());
            }
        } else {
            return totalCrafts;
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
                : this.hasAutoExportWork() ? TickRateModulation.SLOWER : TickRateModulation.IDLE;
    }

    private void performCrafts(int maxCrafts) {
        for (var x = 0; x < craftingJobs.size(); x++) {
            var job = getNextJob(x);

            if (job == null || !enabledPatternSlots.get(x)) {
                continue;
            }

            final int craftAmount = maximumCraftableAmount(job);
            final int toCraft = Math.min(craftAmount, maxCrafts);
            performCraft(job, toCraft);
        }
    }

    private void performCraft(CraftingJob job, int toCraft) {
        if (this.getGridNode() == null) return;
        if (job == null || job.pattern == null) return;

        var inputs = job.pattern.getInputs();
        var outputs = job.pattern.getOutputs();

        List<Long> requiredPerCraft = new ArrayList<>();
        List<GenericStack> extractedItems = new ArrayList<>();
        var grid = this.getGridNode().getGrid();
        var energy = grid.getEnergyService();
        var storage = grid.getStorageService();

        // Extract all the items from ME Storage
        for (var input : inputs) {
            for (var genInput : input.getPossibleInputs()) {
                var inputAmount = input.getMultiplier() * genInput.amount();

                var toExtract = job.requiredInputTotal(genInput, toCraft);
                var extracted = StorageHelper.poweredExtraction(
                        energy, storage.getInventory(), genInput.what(), toExtract, this.mySrc);
                if (extracted >= inputAmount) {
                    requiredPerCraft.add(inputAmount);
                    extractedItems.add(new GenericStack(genInput.what(), extracted));
                    break;
                }
            }
        }

        // Check how many complete recipes were extracted
        var completeRecipes = extractedItems.size() == inputs.length ? toCraft : 0;
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
        for (var output : outputs) {
            if (output.what() instanceof AEItemKey key) {
                var stack = key.toStack();
                stack.setCount((int) job.outputAmountPerCraft(output) * completeRecipes);

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
            var successfulReturn = StorageHelper.poweredInsert(
                    energy, storage.getInventory(), extractedItems.get(x).what(), toReturn, this.mySrc);

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
    }

    private @Nullable CraftingJob getNextJob(int jobIndex) {
        try {
            var job = craftingJobs.get(jobIndex);
            return job == null || job.pattern == null ? null : job;
        } catch (IndexOutOfBoundsException e) {
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
        var energy = this.getGridNode().getGrid().getEnergyService();
        var inventory = storage.getInventory();

        var success = false;
        for (var x = 0; x < this.outputInv.size(); x++) {
            var extractStack = this.outputInv.extractItem(x, MAX_OUTPUT_INV_SIZE, false);

            var key = AEItemKey.of(extractStack);
            if (key == null) continue;

            var inserted = StorageHelper.poweredInsert(energy, inventory, key, extractStack.getCount(), this.mySrc);
            extractStack.setCount(extractStack.getCount() - (int) inserted);
            this.outputInv.insertItem(x, extractStack, false);

            if (inserted > 0) {
                success = true;
            }
        }

        return success;
    }

    private boolean exportToAdjacentBlocks() {
        if (level == null) {
            return false;
        }

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

        ListTag enabledTags = new ListTag();
        for (var value : enabledPatternSlots) {
            enabledTags.add(ByteTag.valueOf(value));
        }
        data.put("enabledPatterns", enabledTags);

        try {
            ListTag jobTags = new ListTag();
            for (var job : craftingJobs) {
                CompoundTag tag = new CompoundTag();
                if (job != null) {
                    job.writeToNBT(tag, registries);
                    jobTags.add(tag);
                } else {
                    jobTags.add(tag);
                }
            }
            data.put("craftingJobs", jobTags);
        } catch (NullPointerException ignored) {

        }

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

        ListTag enabledTags = data.getList("enabledPatterns", Tag.TAG_BYTE);
        if (!enabledTags.isEmpty()) {
            for (var x = 0; x < enabledPatternSlots.size(); x++) {
                enabledPatternSlots.set(x, ((ByteTag) enabledTags.get(x)).getAsByte() > 0);
            }
        }

        if (data.contains("craftingJobs")) {
            ListTag jobTags = data.getList("craftingJobs", Tag.TAG_COMPOUND);
            if (!jobTags.isEmpty()) {
                for (var x = 0; x < jobTags.size(); x++) {
                    CompoundTag tag = ((CompoundTag) jobTags.get(x));
                    if (!tag.isEmpty()) {
                        craftingJobs.set(x, CraftingJob.fromTag(tag, registries));
                    }
                }
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
        if (level == null) {
            return;
        }

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
        if (rs == RedstoneMode.LOW_SIGNAL) {
            return !this.getRedstoneState();
        }
        if (rs == RedstoneMode.HIGH_SIGNAL) {
            return this.getRedstoneState();
        }
        return true;
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
    public void returnToMainMenu(Player player, ISubMenu iSubMenu) {
        MenuOpener.returnTo(AAEMenus.QUANTUM_CRAFTER, player, MenuLocators.forBlockEntity(this));
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AAEBlocks.QUANTUM_CRAFTER.asItem());
    }

    public List<Boolean> getEnabledPatternSlots() {
        return enabledPatternSlots;
    }

    public void toggleEnablePattern(int index) {
        if (index >= 0 && index < enabledPatternSlots.size()) {
            enabledPatternSlots.set(index, !enabledPatternSlots.get(index));
        }

        saveChanges();
    }

    public LinkedHashMap<AEKey, Long> getPatternConfigInputs(int index) {
        LinkedHashMap<AEKey, Long> inputs = new LinkedHashMap<>();
        try {
            var job = craftingJobs.get(index);
            if (job == null || job.pattern == null) return null;
            for (var input : job.pattern.getInputs()) {
                var genStack = input.getPossibleInputs()[0];
                inputs.put(genStack.what(), job.minimumInputToKeep(input));
            }
            return inputs;

        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Pair<AEKey, Long> getPatternConfigOutput(int index) {
        try {
            var job = craftingJobs.get(index);
            if (job == null || job.pattern == null) return null;
            return new Pair<>(job.pattern.getOutputs().getFirst().what(), job.limitMaxOutput);

        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public void setStockAmount(int index, int inputIndex, long amount) {
        try {
            var job = craftingJobs.get(index);
            job.setMinimumInputToKeep(inputIndex, amount);
        } catch (IndexOutOfBoundsException ignored) {

        }
    }

    public void setMaxCrafted(int index, long amount) {
        try {
            var job = craftingJobs.get(index);
            job.limitMaxOutput = amount;
        } catch (IndexOutOfBoundsException ignored) {

        }
    }

    public static class CraftingJob {

        public static final long DEFAULT_KEEP_INPUT = 0;
        public static final long DEFAULT_KEEP_OUTPUT = 0;
        private static final int GRID_SIZE = 3;

        public AECraftingPattern pattern;
        private final List<ItemStack> remainingItems;
        private final List<Long> keepMinInput;
        public long limitMaxOutput;

        public CraftingJob(AECraftingPattern pattern) {
            this.pattern = pattern;
            this.remainingItems = getRemainingItems();
            this.keepMinInput = createNewInputMap();
            this.limitMaxOutput = DEFAULT_KEEP_OUTPUT;
        }

        private CraftingJob(
                AECraftingPattern pattern,
                List<ItemStack> remainingItems,
                List<Long> keepMinInput,
                long limitMaxOutput) {
            this.pattern = pattern;
            this.remainingItems = remainingItems;
            this.keepMinInput = keepMinInput;
            this.limitMaxOutput = limitMaxOutput;
        }

        public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
            ListTag remainingTag = new ListTag();
            for (var item : remainingItems) {
                if (item == null || item.isEmpty()) continue;

                CompoundTag itemTag = new CompoundTag();
                itemTag = (CompoundTag) item.save(registries, itemTag);
                remainingTag.add(itemTag);
            }
            data.put("remainingItems", remainingTag);

            ListTag listMinTag = new ListTag();
            for (var value : keepMinInput) {
                listMinTag.add(LongTag.valueOf(value));
            }
            data.put("listMinInput", listMinTag);

            data.putLong("limitMaxOutput", limitMaxOutput);
        }

        public static CraftingJob fromTag(CompoundTag data, HolderLookup.Provider registries) {
            ListTag remainingTag = data.getList("remainingItems", Tag.TAG_COMPOUND);
            List<ItemStack> remainingItems = Arrays.asList(new ItemStack[remainingTag.size()]);
            if (!remainingTag.isEmpty()) {

                for (var x = 0; x < remainingTag.size(); x++) {
                    remainingItems.set(x, ItemStack.parseOptional(registries, remainingTag.getCompound(x)));
                }
            }

            ListTag listMinTag = data.getList("listMinInput", Tag.TAG_LONG);
            List<Long> keepMinInput = Arrays.asList(new Long[listMinTag.size()]);
            if (!listMinTag.isEmpty()) {

                for (var x = 0; x < listMinTag.size(); x++) {
                    keepMinInput.set(x, ((LongTag) listMinTag.get(x)).getAsLong());
                }
            }

            var limitMaxOutput = data.getLong("limitMaxOutput");

            return new CraftingJob(null, remainingItems, keepMinInput, limitMaxOutput);
        }

        public void setPattern(AECraftingPattern pattern) {
            this.pattern = pattern;
        }

        public long minimumInputToKeep(IPatternDetails.IInput stack) {
            for (var x = 0; x < pattern.getInputs().length; x++) {
                var input = pattern.getInputs()[x];
                if (input.equals(stack) && keepMinInput.size() > x) {
                    return keepMinInput.get(x);
                }
            }
            return DEFAULT_KEEP_INPUT;
        }

        public void setMinimumInputToKeep(int inputIndex, long value) {
            if (inputIndex >= pattern.getInputs().length) return;

            keepMinInput.set(inputIndex, value);
        }

        public long requiredInputTotal(GenericStack input, int toCraft) {
            long multiplier = 0;
            for (var i : pattern.getInputs()) {
                var found = false;
                for (var genInput : i.getPossibleInputs()) {
                    if (input.what().matches(genInput)) {
                        multiplier = i.getMultiplier() * input.amount();
                        found = true;
                        break;
                    }
                }

                if (found) {
                    break;
                }
            }

            if (!isInputConsumed(input)) {
                return multiplier;
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
            for (var output : pattern.getOutputs()) {
                if (input.what().matches(output)) {
                    return output.amount() - input.amount() <= 0;
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

        public long outputAmountPerCraft(GenericStack stack) {
            for (var i : pattern.getInputs()) {
                for (var genInput : i.getPossibleInputs()) {
                    if (stack.what().matches(genInput)) {
                        return stack.amount() - genInput.amount();
                    }
                }
            }
            return stack.amount();
        }

        private List<Long> createNewInputMap() {
            List<Long> list = new ArrayList<>();
            for (var ignored : pattern.getInputs()) {
                list.add(DEFAULT_KEEP_INPUT);
            }
            return list;
        }

        private List<ItemStack> getRemainingItems() {
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
                        if (bucketCount > 0) {
                            finalList.add(new ItemStack(entry.getKey(), bucketCount));
                        }
                    } else {
                        finalList.add(new ItemStack(entry.getKey(), entry.getValue()));
                    }
                }
            }
            return Collections.unmodifiableList(finalList);
        }
    }
}
