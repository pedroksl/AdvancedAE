package net.pedroksl.advanced_ae.common.parts;

import java.util.EnumSet;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNodeListener;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.stacks.AEItemKey;
import appeng.api.util.AECableType;
import appeng.menu.locator.MenuLocators;
import appeng.parts.AEBasePart;
import appeng.util.SettingsFrom;

public class AdvPatternProviderPart extends AEBasePart implements AdvPatternProviderLogicHost {

    protected final AdvPatternProviderLogic logic;

    public AdvPatternProviderPart(IPartItem<?> partItem) {
        this(partItem, 36);
    }

    public AdvPatternProviderPart(IPartItem<?> partItem, int slots) {
        super(partItem);
        this.getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL);

        this.logic = this.createLogic(slots);
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        super.onMainNodeStateChanged(reason);
        this.logic.onMainNodeStateChanged();
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(2.0, 2.0, 14.0, 14.0, 14.0, 16.0);
        bch.addBox(5.0, 5.0, 12.0, 11.0, 11.0, 14.0);
    }

    @Override
    public void readFromNBT(ValueInput input) {
        super.readFromNBT(input);
        this.logic.readFromNBT(input);
    }

    @Override
    public void writeToNBT(ValueOutput output) {
        super.writeToNBT(output);
        this.logic.writeToNBT(output);
    }

    @Override
    public void addToWorld() {
        super.addToWorld();
        this.logic.updatePatterns();
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
        super.addAdditionalDrops(drops, wrenched);
        this.logic.addDrops(drops);
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 4.0F;
    }

    @Override
    public void exportSettings(SettingsFrom mode, DataComponentMap.Builder builder) {
        super.exportSettings(mode, builder);
        if (mode == SettingsFrom.MEMORY_CARD) {
            this.logic.exportSettings(builder);
        }
    }

    @Override
    public void importSettings(SettingsFrom mode, DataComponentMap input, @Nullable Player player) {
        super.importSettings(mode, input, player);
        if (mode == SettingsFrom.MEMORY_CARD) {
            this.logic.importSettings(input, player);
        }
    }

    @Override
    public void onNeighborChanged(BlockGetter level, BlockPos pos, BlockPos neighbor) {
        this.logic.updateRedstoneState();
    }

    @Override
    public boolean onUseWithoutItem(Player p, Vec3 pos) {
        if (AdvancedAE.instance().getClientLevel() == null) {
            this.openMenu(p, MenuLocators.forPart(this));
        }
        return true;
    }

    protected AdvPatternProviderLogic createLogic(int slots) {
        return new AdvPatternProviderLogic(this.getMainNode(), this, slots);
    }

    @Override
    public AdvPatternProviderLogic getLogic() {
        return this.logic;
    }

    @Override
    public EnumSet<Direction> getTargets() {
        return EnumSet.of(this.getSide());
    }

    @Override
    public void saveChanges() {
        this.getHost().markForSave();
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(this.getPartItem());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return AAEItems.SMALL_ADV_PATTERN_PROVIDER.stack();
    }
}
