package net.pedroksl.advanced_ae.common.entities;

import java.util.*;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.pedroksl.advanced_ae.common.blocks.AdvPatternProviderBlock;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;

import appeng.api.networking.IGridNodeListener;
import appeng.api.orientation.BlockOrientation;
import appeng.api.stacks.AEItemKey;
import appeng.api.util.AECableType;
import appeng.block.crafting.PatternProviderBlock;
import appeng.block.crafting.PushDirection;
import appeng.blockentity.grid.AENetworkBlockEntity;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import appeng.util.Platform;
import appeng.util.SettingsFrom;

public class AdvPatternProviderEntity extends AENetworkBlockEntity implements AdvPatternProviderLogicHost {
    protected final AdvPatternProviderLogic logic;

    @Nullable
    private PushDirection pendingPushDirectionChange;

    public AdvPatternProviderEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        this(blockEntityType, pos, blockState, 36);
    }

    public AdvPatternProviderEntity(
            BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState, int slots) {
        super(blockEntityType, pos, blockState);
        this.logic = createLogic(slots);
    }

    protected AdvPatternProviderLogic createLogic(int slots) {
        return new AdvPatternProviderLogic(this.getMainNode(), this, slots);
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        this.logic.onMainNodeStateChanged();
        this.updateState();
    }

    public void updateState() {
        if (!this.getMainNode().isReady()) {
            return;
        }

        var connected = false;
        var grid = getMainNode().getGrid();
        if (grid != null) {
            if (grid.getEnergyService().isNetworkPowered()) {
                connected = true;
            }
        }

        if (this.checkPosition(this.worldPosition)
                && this.level.getBlockState(this.worldPosition).getValue(AdvPatternProviderBlock.CONNECTION_STATE)
                        != connected) {
            this.level.setBlock(
                    this.worldPosition,
                    this.level
                            .getBlockState(this.worldPosition)
                            .setValue(AdvPatternProviderBlock.CONNECTION_STATE, connected),
                    Block.UPDATE_CLIENTS);
        }
    }

    private boolean checkPosition(BlockPos pos) {
        return Platform.getTickingBlockEntity(getLevel(), pos) instanceof AdvPatternProviderEntity;
    }

    private PushDirection getPushDirection() {
        return getBlockState().getValue(PatternProviderBlock.PUSH_DIRECTION);
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        // In omnidirectional mode, every side is grid-connectable
        var pushDirection = getPushDirection().getDirection();
        if (pushDirection == null) {
            return EnumSet.allOf(Direction.class);
        }

        // Otherwise all sides *except* the target side are connectable
        return EnumSet.complementOf(EnumSet.of(pushDirection));
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        this.logic.addDrops(drops);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.logic.clearContent();
    }

    @Override
    public void onReady() {
        if (pendingPushDirectionChange != null) {
            level.setBlockAndUpdate(
                    getBlockPos(),
                    getBlockState().setValue(PatternProviderBlock.PUSH_DIRECTION, pendingPushDirectionChange));
            pendingPushDirectionChange = null;
            onGridConnectableSidesChanged();
        }

        super.onReady();
        this.logic.updatePatterns();

        this.updateState();
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        this.logic.writeToNBT(data);
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);

        // Remove in 1.20.1+: Convert legacy NBT orientation to blockstate
        if (data.getBoolean("omniDirectional")) {
            pendingPushDirectionChange = PushDirection.ALL;
        } else if (data.contains("forward", Tag.TAG_STRING)) {
            try {
                var forward = Direction.valueOf(data.getString("forward").toUpperCase(Locale.ROOT));
                pendingPushDirectionChange = PushDirection.fromDirection(forward);
            } catch (IllegalArgumentException ignored) {
            }
        }

        this.logic.readFromNBT(data);
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.SMART;
    }

    @Override
    public AdvPatternProviderLogic getLogic() {
        return logic;
    }

    @Override
    public EnumSet<Direction> getTargets() {
        var pushDirection = getPushDirection();
        if (pushDirection.getDirection() == null) {
            return EnumSet.allOf(Direction.class);
        } else {
            return EnumSet.of(pushDirection.getDirection());
        }
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(AAEBlocks.ADV_PATTERN_PROVIDER);
    }

    @Override
    public void exportSettings(
            SettingsFrom mode, CompoundTag output, @org.jetbrains.annotations.Nullable Player player) {
        super.exportSettings(mode, output, player);

        if (mode == SettingsFrom.MEMORY_CARD) {
            logic.exportSettings(output);

            var pushDirection = getPushDirection();
            output.putByte("push_direction", (byte) pushDirection.ordinal());
        }
    }

    @Override
    public void importSettings(
            SettingsFrom mode, CompoundTag input, @org.jetbrains.annotations.Nullable Player player) {
        super.importSettings(mode, input, player);

        if (mode == SettingsFrom.MEMORY_CARD) {
            logic.importSettings(input, player);

            // Restore push direction blockstate
            if (input.contains(PatternProviderBlock.PUSH_DIRECTION.getName(), Tag.TAG_BYTE)) {
                var pushDirection = input.getByte(PatternProviderBlock.PUSH_DIRECTION.getName());
                if (pushDirection >= 0 && pushDirection < PushDirection.values().length) {
                    var level = getLevel();
                    if (level != null) {
                        level.setBlockAndUpdate(
                                getBlockPos(),
                                getBlockState()
                                        .setValue(
                                                PatternProviderBlock.PUSH_DIRECTION,
                                                PushDirection.values()[pushDirection]));
                    }
                }
            }
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @javax.annotation.Nullable Direction side) {
        var lo = logic.getCapability(cap);
        if (lo.isPresent()) {
            return lo;
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return AAEBlocks.ADV_PATTERN_PROVIDER.stack();
    }

    @Override
    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        onGridConnectableSidesChanged();
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        MenuOpener.open(AAEMenus.ADV_PATTERN_PROVIDER.get(), player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(AAEMenus.ADV_PATTERN_PROVIDER.get(), player, subMenu.getLocator());
    }
}
