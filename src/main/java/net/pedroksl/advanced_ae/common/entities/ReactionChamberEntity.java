package net.pedroksl.advanced_ae.common.entities;

import java.util.EnumSet;
import java.util.List;
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
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.recipes.ReactionChamberRecipe;
import net.pedroksl.advanced_ae.recipes.ReactionChamberRecipes;

import appeng.api.config.Setting;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.stacks.AEKeyType;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.AEItemFilters;

public class ReactionChamberEntity extends AENetworkedPoweredBlockEntity
        implements IGridTickable, IUpgradeableObject, IConfigurableObject {
    private static final int MAX_PROCESSING_STEPS = 200;

    private final IUpgradeInventory upgrades;
    private final IConfigManager configManager;

    private final AppEngInternalInventory inputInv = new AppEngInternalInventory(this, 3, 64);
    private final AppEngInternalInventory outputInv = new AppEngInternalInventory(this, 1, 64);
    private final InternalInventory inv = new CombinedInternalInventory(this.inputInv, this.outputInv);

    private final FilteredInternalInventory inputExposed;
    private final FilteredInternalInventory outputExposed;
    private final InternalInventory invExposed;

    private final GenericStackInv fluidInv;

    private boolean working;
    private int processingTime;
    private long clientStart;

    private ReactionChamberRecipe cachedTask = null;

    public ReactionChamberEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        this.getMainNode().setIdlePowerUsage(0).addService(IGridTickable.class, this);
        this.setInternalMaxPower(15000);

        this.inputExposed = new FilteredInternalInventory(this.inputInv, AEItemFilters.INSERT_ONLY);
        this.outputExposed = new FilteredInternalInventory(this.outputInv, AEItemFilters.EXTRACT_ONLY);
        this.invExposed = new CombinedInternalInventory(this.inputExposed, this.outputExposed);

        this.fluidInv =
                new GenericStackInv(Set.of(AEKeyType.fluids()), this::onChangeTank, GenericStackInv.Mode.STORAGE, 1);
        this.fluidInv.setCapacity(AEKeyType.fluids(), 1000L);

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

        data.putBoolean("smash", isWorking());
    }

    @Override
    protected void loadVisualState(CompoundTag data) {
        super.loadVisualState(data);

        setWorking(data.getBoolean("smash"));
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return EnumSet.allOf(Direction.class);
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.inv;
    }

    public GenericStackInv getGenericInv() {
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
        // Check for valid recipe

        if (cachedTask == null) {
            // Reset recipe
            this.setProcessingTime(0);
            this.cachedTask = null;
        }

        // Update displayed stacks on the client
        if (!this.isWorking()) {
            this.markForUpdate();
        }

        getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));

        this.saveChanges();
    }

    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        onChangeInventory();
    }

    public void onChangeTank() {
        onChangeInventory();
    }

    @Nullable
    public ReactionChamberRecipe getTask() {
        if (this.cachedTask == null && level != null) {

            this.cachedTask = ReactionChamberRecipes.findRecipe(
                    level,
                    inputInv.getStackInSlot(0),
                    inputInv.getStackInSlot(1),
                    inputInv.getStackInSlot(2),
                    fluidInv.getStack(0));
        }
        return this.cachedTask;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(1, 20, true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode iGridNode, int i) {
        return TickRateModulation.SLEEP;
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
        this.upgrades.writeToNBT(data, "upgrades", registries);
        this.configManager.writeToNBT(data, registries);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
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

        for (int i = 0; i < this.inputInv.size(); i++) {
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
    }

    @Override
    public void clearContent() {
        super.clearContent();
        upgrades.clear();
    }
}
