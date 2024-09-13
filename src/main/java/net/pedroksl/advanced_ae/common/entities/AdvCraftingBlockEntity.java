package net.pedroksl.advanced_ae.common.entities;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Iterators;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.pedroksl.advanced_ae.common.blocks.AAEAbstractCraftingUnitBlock;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitBlock;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
import net.pedroksl.advanced_ae.common.cluster.AdvCraftingCPUCalculator;
import net.pedroksl.advanced_ae.common.cluster.AdvCraftingCPUCluster;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;

import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridMultiblock;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.orientation.BlockOrientation;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.crafting.CraftingCubeModelData;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.me.cluster.IAEMultiBlock;
import appeng.util.NullConfigManager;
import appeng.util.Platform;
import appeng.util.iterators.ChainedIterator;

public class AdvCraftingBlockEntity extends AENetworkedBlockEntity
        implements IAEMultiBlock<AdvCraftingCPUCluster>, IPowerChannelState, IConfigurableObject {

    private final AdvCraftingCPUCalculator calc = new AdvCraftingCPUCalculator(this);
    private CompoundTag previousState = null;
    private boolean isCoreBlock = false;
    private AdvCraftingCPUCluster cluster;

    public AdvCraftingBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
        this.getMainNode()
                .setFlags(GridFlags.MULTIBLOCK, GridFlags.REQUIRE_CHANNEL)
                .addService(IGridMultiblock.class, this::getMultiblockNodes);
    }

    @Override
    protected Item getItemFromBlockEntity() {
        if (this.level == null) {
            return Items.AIR;
        }
        return getUnitBlock().type.getItemFromType();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        if (this.cluster != null) {
            this.cluster.updateName();
        }
    }

    public AAEAbstractCraftingUnitBlock<?> getUnitBlock() {
        if (this.level == null || this.notLoaded() || this.isRemoved()) {
            return AAEBlocks.QUANTUM_UNIT.block();
        }
        var block = this.level.getBlockState(this.worldPosition).getBlock();
        return block instanceof AAEAbstractCraftingUnitBlock
                ? (AAEAbstractCraftingUnitBlock<?>) block
                : AAEBlocks.QUANTUM_UNIT.block();
    }

    public long getStorageBytes() {
        return getUnitBlock().type.getStorageBytes();
    }

    public int getStorageMultiplier() {
        return ((AAECraftingUnitType) getUnitBlock().type).getStorageMultiplier();
    }

    public int getAcceleratorThreads() {
        return getUnitBlock().type.getAcceleratorThreads();
    }

    public int getAccelerationMultiplier() {
        return ((AAECraftingUnitType) getUnitBlock().type).getAccelerationMultiplier();
    }

    @Override
    public void onReady() {
        super.onReady();
        this.getMainNode().setVisualRepresentation(this.getItemFromBlockEntity());
        if (level instanceof ServerLevel serverLevel) {
            this.calc.calculateMultiblock(serverLevel, worldPosition);
        }
    }

    public void updateMultiBlock(BlockPos changedPos) {
        if (level instanceof ServerLevel serverLevel) {
            this.calc.updateMultiblockAfterNeighborUpdate(serverLevel, worldPosition, changedPos);
        }
    }

    public void updateStatus(AdvCraftingCPUCluster c) {
        if (this.cluster != null && this.cluster != c) {
            this.cluster.breakCluster();
        }

        this.cluster = c;
        this.updateSubType(true);
    }

    public void updateSubType(boolean updateFormed) {
        if (this.level == null || this.notLoaded() || this.isRemoved()) {
            return;
        }

        final boolean formed = this.isFormed();
        boolean power = this.getMainNode().isOnline();

        final BlockState current = this.level.getBlockState(this.worldPosition);

        // The block entity might try to update while being destroyed
        if (current.getBlock() instanceof AAEAbstractCraftingUnitBlock) {
            int lightLevel = formed && power && this.getUnitBlock().type == AAECraftingUnitType.QUANTUM_CORE
                    ? 10
                    : this.getUnitBlock().type == AAECraftingUnitType.STRUCTURE ? 5 : 0;

            final BlockState newState = current.setValue(AAEAbstractCraftingUnitBlock.POWERED, power)
                    .setValue(AAEAbstractCraftingUnitBlock.FORMED, formed)
                    .setValue(AAECraftingUnitBlock.LIGHT_LEVEL, lightLevel);

            if (current != newState) {
                // Not using flag 2 here (only send to clients, prevent block update) will cause
                // infinite loops
                // In case there is an inconsistency in the crafting clusters.
                this.level.setBlock(this.worldPosition, newState, Block.UPDATE_CLIENTS);
            }
        }

        if (updateFormed) {
            onGridConnectableSidesChanged();
        }
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        if (isFormed()) {
            return getUnitBlock().type == AAECraftingUnitType.QUANTUM_CORE
                    ? EnumSet.of(Direction.UP, Direction.DOWN)
                    : EnumSet.allOf(Direction.class);
        } else {
            return EnumSet.noneOf(Direction.class);
        }
    }

    public boolean isFormed() {
        if (isClientSide()) {
            return getBlockState().getValue(AAEAbstractCraftingUnitBlock.FORMED);
        }
        return this.cluster != null;
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        data.putBoolean("core", this.isCoreBlock());
        if (this.isCoreBlock() && this.cluster != null) {
            this.cluster.writeToNBT(data, registries);
        }
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.setCoreBlock(data.getBoolean("core"));
        if (this.isCoreBlock()) {
            if (this.cluster != null) {
                this.cluster.readFromNBT(data, registries);
            } else {
                this.setPreviousState(data.copy());
            }
        }
    }

    @Override
    public void disconnect(boolean update) {
        if (this.cluster != null) {
            this.cluster.destroy();
            if (update) {
                this.updateSubType(true);
            }
        }
    }

    @Override
    public AdvCraftingCPUCluster getCluster() {
        return this.cluster;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        if (reason != IGridNodeListener.State.GRID_BOOT) {
            this.updateSubType(false);
        }
    }

    public void breakCluster() {
        if (this.cluster != null) {
            this.cluster.cancelJobs();
            var inventories = this.cluster.getInventories();

            // Drop stacks
            var places = new ArrayList<BlockPos>();

            for (var blockEntity : (Iterable<AdvCraftingBlockEntity>) this.cluster::getBlockEntities) {
                if (this == blockEntity) {
                    places.add(worldPosition);
                } else {
                    for (var d : Direction.values()) {
                        var p = blockEntity.worldPosition.relative(d);

                        if (this.level.isEmptyBlock(p)) {
                            places.add(p);
                        }
                    }
                }
            }

            if (places.isEmpty()) {
                throw new IllegalStateException(
                        this.cluster + " does not contain any kind of blocks, which were destroyed.");
            }

            for (var inv : inventories) {
                for (var entry : inv.list) {
                    var position = Util.getRandom(places, level.getRandom());
                    var stacks = new ArrayList<ItemStack>();
                    entry.getKey().addDrops(entry.getLongValue(), stacks, this.level, position);
                    Platform.spawnDrops(this.level, position, stacks);
                }

                inv.clear(); // Ensure items only ever get dropped once
            }

            this.cluster.destroy();
        }
    }

    @Override
    public boolean isPowered() {
        if (isClientSide()) {
            return this.level.getBlockState(this.worldPosition).getValue(AAEAbstractCraftingUnitBlock.POWERED);
        }
        return this.getMainNode().isActive();
    }

    @Override
    public boolean isActive() {
        if (!isClientSide()) {
            return this.getMainNode().isActive();
        }
        return this.isPowered() && this.isFormed();
    }

    public boolean isCoreBlock() {
        return this.isCoreBlock;
    }

    public void setCoreBlock(boolean isCoreBlock) {
        this.isCoreBlock = isCoreBlock;
    }

    public CompoundTag getPreviousState() {
        return this.previousState;
    }

    public void setPreviousState(CompoundTag previousState) {
        this.previousState = previousState;
    }

    @Override
    public ModelData getModelData() {
        return CraftingCubeModelData.create(getConnections());
    }

    protected EnumSet<Direction> getConnections() {
        if (level == null) {
            return EnumSet.noneOf(Direction.class);
        }

        EnumSet<Direction> connections = EnumSet.noneOf(Direction.class);

        for (Direction facing : Direction.values()) {
            if (this.isConnected(level, worldPosition, facing)) {
                connections.add(facing);
            }
        }

        return connections;
    }

    private boolean isConnected(BlockGetter level, BlockPos pos, Direction side) {
        BlockPos adjacentPos = pos.relative(side);
        return level.getBlockState(adjacentPos).getBlock() instanceof AAEAbstractCraftingUnitBlock;
    }

    /**
     * When the block state changes (i.e. becoming formed or unformed), we need to update the model data since it
     * contains connections to neighboring block entities.
     */
    @Override
    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        requestModelDataUpdate();
    }

    private Iterator<IGridNode> getMultiblockNodes() {
        if (this.getCluster() == null) {
            return new ChainedIterator<>();
        }
        return Iterators.transform(this.getCluster().getBlockEntities(), AdvCraftingBlockEntity::getGridNode);
    }

    @Override
    public IConfigManager getConfigManager() {
        var cluster = this.getCluster();

        if (cluster != null) {
            return this.getCluster().getConfigManager();
        } else {
            return NullConfigManager.INSTANCE;
        }
    }
}
