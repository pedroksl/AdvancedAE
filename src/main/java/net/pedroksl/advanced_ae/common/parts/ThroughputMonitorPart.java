package net.pedroksl.advanced_ae.common.parts;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.pedroksl.advanced_ae.common.definitions.AAEConfig;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.logic.ThroughputCache;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartItem;
import appeng.api.stacks.AmountFormat;
import appeng.hooks.ticking.TickHandler;
import appeng.parts.reporting.AbstractMonitorPart;

public class ThroughputMonitorPart extends AbstractMonitorPart implements IGridTickable {

    private final ThroughputCache cache = new ThroughputCache();
    protected double lastReportedValue = -1;
    private WorkRoutine workRoutine = WorkRoutine.SECOND;
    private WorkRoutine lastWorkRoutine = WorkRoutine.SECOND;

    private enum WorkRoutine {
        TICK(1, 10),
        SECOND(20, 20),
        MINUTE(1200, AAEConfig.instance().getThroughputMonitorCacheSize() / 2),
        TEN_MINUTE(12000, AAEConfig.instance().getThroughputMonitorCacheSize() * 5);

        public static WorkRoutine cycle(WorkRoutine routine) {
            return switch (routine) {
                case TICK -> SECOND;
                case SECOND -> MINUTE;
                case MINUTE -> TEN_MINUTE;
                case TEN_MINUTE -> TICK;
            };
        }

        public static WorkRoutine fromInt(int value) {
            return switch (value) {
                case 0 -> TICK;
                case 2 -> MINUTE;
                case 3 -> TEN_MINUTE;
                default -> SECOND;
            };
        }

        public final int ticks;
        public final int timeLimit_s;

        WorkRoutine(int ticks, int timeLimit_s) {
            this.ticks = ticks;
            this.timeLimit_s = timeLimit_s;
        }
    }

    public ThroughputMonitorPart(IPartItem<?> partItem) {
        super(partItem, false);

        getMainNode().addService(IGridTickable.class, this);
    }

    public MutableComponent getThroughputText() {
        if (getDisplayed() == null) return Component.empty();

        var sign = this.lastReportedValue > 0 ? "+" : this.lastReportedValue == 0 ? "" : "-";

        String valueText;
        if (Math.abs(this.lastReportedValue) > 10 || this.lastReportedValue == 0) {
            valueText = getDisplayed().formatAmount(Math.round(Math.abs(this.lastReportedValue)), AmountFormat.SLOT);
        } else {
            valueText = String.format("%.2f", Math.abs(this.lastReportedValue));
        }

        return switch (this.workRoutine) {
            case TICK -> AAEText.OverdriveThroughputMonitorValue.text(sign, valueText);
            case SECOND -> AAEText.ThroughputMonitorValue.text(sign, valueText);
            case MINUTE -> AAEText.SlowThroughputMonitorValue.text(sign, valueText);
            case TEN_MINUTE -> AAEText.SlowerThroughputMonitorValue.text(sign, valueText);
        };
    }

    public double getThroughput() {
        return lastReportedValue;
    }

    @Override
    public void writeToNBT(ValueOutput output) {
        super.writeToNBT(output);
        output.putDouble("throughput", this.lastReportedValue);
        output.putInt("routine", this.workRoutine.ordinal());
    }

    @Override
    public void readFromNBT(ValueInput input) {
        super.readFromNBT(input);
        this.lastReportedValue = input.getDoubleOr("throughput", 0);
        this.workRoutine = WorkRoutine.fromInt(input.getIntOr("routine", 0));
    }

    @Override
    public void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeDouble(this.lastReportedValue);
        data.writeEnum(this.workRoutine);
    }

    @Override
    public boolean readFromStream(RegistryFriendlyByteBuf data) {
        boolean needRedraw = super.readFromStream(data);

        this.lastReportedValue = data.readDouble();
        this.workRoutine = data.readEnum(WorkRoutine.class);

        return needRedraw;
    }

    @Override
    public void writeVisualStateToNBT(ValueOutput output) {
        super.writeVisualStateToNBT(output);
        output.putDouble("lastValue", this.lastReportedValue);
        output.putInt("routine", this.workRoutine.ordinal());
    }

    @Override
    public void readVisualStateFromNBT(ValueInput input) {
        super.readVisualStateFromNBT(input);
        this.lastReportedValue = input.getDoubleOr("lastValue", 0);
        this.workRoutine = WorkRoutine.fromInt(input.getIntOr("routine", 0));
    }

    @Override
    public boolean onUseItemOn(ItemStack heldItem, Player player, InteractionHand hand, Vec3 pos) {
        if (heldItem == ItemStack.EMPTY) {
            return super.onUseWithoutItem(player, pos);
        } else if (heldItem.is(AAEItems.MONITOR_CONFIGURATOR.asItem())) {
            if (!isClientSide()) {
                cycleWorkRoutine();
            }
            return true;
        } else {
            return super.onUseItemOn(heldItem, player, hand, pos);
        }
    }

    private void cycleWorkRoutine() {
        this.workRoutine = WorkRoutine.cycle(this.workRoutine);

        getMainNode().ifPresent((grid, node) -> grid.getTickManager().alertDevice(node));
    }

    @Override
    protected void configureWatchers() {
        if (getDisplayed() != null) {
            updateState(getAmount(), TickHandler.instance().getCurrentTick());
            getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        } else {
            getMainNode().ifPresent((grid, node) -> grid.getTickManager().sleepDevice(node));
        }

        super.configureWatchers();
    }

    @Override
    protected void onMainNodeStateChanged(IGridNodeListener.State reason) {
        getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));

        super.onMainNodeStateChanged(reason);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(20, 100, !isActive() || getDisplayed() == null);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode iGridNode, int i) {
        if (!this.getMainNode().isActive() || getDisplayed() == null) {
            resetState();
            return TickRateModulation.SLEEP;
        }

        long currentTick = TickHandler.instance().getCurrentTick();
        long currentAmount = getAmount();

        // Long time without updates, do a fast one
        if (cache.size() == 0) {
            updateState(currentAmount, currentTick);
            this.lastReportedValue = 0;
            return TickRateModulation.URGENT;
        }

        // Normal update schedule
        if (this.workRoutine == this.lastWorkRoutine) {
            var amountPerTick = cache.averagePerTick(this.workRoutine.timeLimit_s);
            this.lastReportedValue = amountPerTick * this.workRoutine.ticks;
        } else {
            this.lastReportedValue = 0;
        }

        updateState(currentAmount, currentTick);
        this.getHost().markForUpdate();

        return TickRateModulation.SLOWER;
    }

    private void resetState() {
        cache.clear();
        this.lastReportedValue = 0;
    }

    private void updateState(long amount, long tick) {
        cache.push(amount, tick);
        this.lastWorkRoutine = this.workRoutine;
    }
}
