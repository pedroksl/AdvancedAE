package net.pedroksl.advanced_ae.common.entities;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.config.Setting;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.util.inv.AppEngInternalInventory;

public class ReactionChamberEntity extends AENetworkedPoweredBlockEntity
        implements IGridTickable, IUpgradeableObject, IConfigurableObject {

    // private final IUpgradeInventory upgrades;
    private final IConfigManager configManager;

    private final AppEngInternalInventory inputInv = new AppEngInternalInventory(this, 3, 64);

    public ReactionChamberEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        this.getMainNode().setIdlePowerUsage(0).addService(IGridTickable.class, this);
        this.setInternalMaxPower(1600);

        // this.upgrades = UpgradeInventories.forMachine(AAEBlocks.REACTION_CHAMBER, 4, this::saveChanges);
        this.configManager = IConfigManager.builder(this::onConfigChanged)
                .registerSetting(Settings.AUTO_EXPORT, YesNo.NO)
                .build();
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        // In omnidirectional mode, every side is grid-connectable
        return EnumSet.complementOf(EnumSet.of(Direction.UP));
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.inputInv;
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
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        var c = super.readFromStream(data);

        for (int i = 0; i < this.inputInv.size(); i++) {
            this.inputInv.setItemDirect(i, ItemStack.OPTIONAL_STREAM_CODEC.decode(data));
        }

        return c;
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);

        for (int i = 0; i < this.inputInv.size(); i++) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(data, this.inputInv.getStackInSlot(i));
        }
    }

    private void onConfigChanged(IConfigManager manager, Setting<?> setting) {
        saveChanges();
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);

        //		for (var upgrade : upgrades) {
        //			drops.add(upgrade);
        //		}
    }

    @Override
    public void clearContent() {
        super.clearContent();
        // upgrades.clear();
    }

    //	@Override
    //	public IUpgradeInventory getUpgrades() {
    //		return upgrades;
    //	}
}
