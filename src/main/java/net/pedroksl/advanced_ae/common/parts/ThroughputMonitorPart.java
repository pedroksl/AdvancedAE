package net.pedroksl.advanced_ae.common.parts;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.pedroksl.advanced_ae.client.renderer.AAEBlockEntityRenderHelper;
import net.pedroksl.advanced_ae.common.definitions.AAEConfig;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.logic.ThroughputCache;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AmountFormat;
import appeng.api.util.AEColor;
import appeng.client.render.BlockEntityRenderHelper;
import appeng.core.AppEng;
import appeng.hooks.ticking.TickHandler;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractMonitorPart;

public class ThroughputMonitorPart extends AbstractMonitorPart implements IGridTickable {

    @PartModels
    public static final ResourceLocation MODEL_OFF = AppEng.makeId("part/storage_monitor_off");

    @PartModels
    public static final ResourceLocation MODEL_ON = AppEng.makeId("part/storage_monitor_on");

    @PartModels
    public static final ResourceLocation MODEL_LOCKED_OFF = AppEng.makeId("part/storage_monitor_locked_off");

    @PartModels
    public static final ResourceLocation MODEL_LOCKED_ON = AppEng.makeId("part/storage_monitor_locked_on");

    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    public static final IPartModel MODELS_LOCKED_OFF = new PartModel(MODEL_BASE, MODEL_LOCKED_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_LOCKED_ON = new PartModel(MODEL_BASE, MODEL_LOCKED_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_LOCKED_HAS_CHANNEL =
            new PartModel(MODEL_BASE, MODEL_LOCKED_ON, MODEL_STATUS_HAS_CHANNEL);

    private final ThroughputCache cache = new ThroughputCache();
    protected double lastReportedValue = -1;
    protected String lastHumanReadableValue = "";
    private WorkRoutine workRoutine = WorkRoutine.SECOND;
    private WorkRoutine lastWorkRoutine = WorkRoutine.SECOND;

    private static final int positiveColor = AEColor.GREEN.mediumVariant;
    private static final int negativeColor = AEColor.RED.mediumVariant;

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

    @Override
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);
        data.putString("throughput", this.lastHumanReadableValue);
        data.putInt("routine", this.workRoutine.ordinal());
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.lastHumanReadableValue = data.getString("throughput");
        this.workRoutine = WorkRoutine.fromInt(data.getInt("routine"));
    }

    @Override
    public void writeToStream(FriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeDouble(this.lastReportedValue);
        data.writeUtf(this.lastHumanReadableValue);
        data.writeEnum(this.workRoutine);
    }

    @Override
    public boolean readFromStream(FriendlyByteBuf data) {
        boolean needRedraw = super.readFromStream(data);

        var reportedValue = data.readLong();
        needRedraw |= reportedValue != this.lastReportedValue;
        this.lastReportedValue = reportedValue;

        this.lastHumanReadableValue = data.readUtf();

        var routine = data.readEnum(WorkRoutine.class);
        needRedraw |= this.workRoutine != routine;
        this.workRoutine = routine;

        return needRedraw;
    }

    @Override
    public void writeVisualStateToNBT(CompoundTag data) {
        super.writeVisualStateToNBT(data);
        data.putDouble("lastValue", this.lastReportedValue);
        data.putString("throughput", this.lastHumanReadableValue);
        data.putInt("routine", this.workRoutine.ordinal());
    }

    @Override
    public void readVisualStateFromNBT(CompoundTag data) {
        super.readVisualStateFromNBT(data);
        this.lastReportedValue = data.getLong("lastValue");
        this.lastHumanReadableValue = data.getString("throughput");
        this.workRoutine = WorkRoutine.fromInt(data.getInt("routine"));
    }

    @Override
    public boolean onPartActivate(Player player, InteractionHand hand, Vec3 pos) {
        if (!isClientSide()) {
            var heldItem = player.getItemInHand(hand);
            if (heldItem.is(AAEItems.MONITOR_CONFIGURATOR.asItem())) {
                cycleWorkRoutine();
                return true;
            }
        }

        return super.onPartActivate(player, hand, pos);
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
    @OnlyIn(Dist.CLIENT)
    public void renderDynamic(
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int combinedLightIn,
            int combinedOverlayIn) {
        if (this.isActive()) {
            if (getDisplayed() != null) {
                poseStack.pushPose();
                BlockOrientation orientation = BlockOrientation.get(this.getSide(), this.getSpin());
                poseStack.translate(0.5, 0.5, 0.5);
                BlockEntityRenderHelper.rotateToFace(poseStack, orientation);
                poseStack.translate(0, 0.1, 0.5);
                BlockEntityRenderHelper.renderItem2dWithAmount(
                        poseStack,
                        buffers,
                        this.getDisplayed(),
                        getAmount(),
                        canCraft(),
                        0.3F,
                        -0.15F,
                        this.getColor().contrastTextColor,
                        this.getLevel());

                poseStack.translate(0, -0.23F, 0);
                var sign = lastReportedValue > 0 ? "+" : lastReportedValue == 0 ? "" : "-";
                var text =
                        switch (this.workRoutine) {
                            case TICK -> AAEText.OverdriveThroughputMonitorValue.text(sign, lastHumanReadableValue);
                            case SECOND -> AAEText.ThroughputMonitorValue.text(sign, lastHumanReadableValue);
                            case MINUTE -> AAEText.SlowThroughputMonitorValue.text(sign, lastHumanReadableValue);
                            case TEN_MINUTE -> AAEText.SlowerThroughputMonitorValue.text(sign, lastHumanReadableValue);
                        };

                var color = lastReportedValue > 0
                        ? positiveColor
                        : lastReportedValue == 0 ? this.getColor().contrastTextColor : negativeColor;
                AAEBlockEntityRenderHelper.renderString(poseStack, buffers, text, color);
                poseStack.popPose();
            }
        }
    }

    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(
                MODELS_OFF,
                MODELS_ON,
                MODELS_HAS_CHANNEL,
                MODELS_LOCKED_OFF,
                MODELS_LOCKED_ON,
                MODELS_LOCKED_HAS_CHANNEL);
    }

    @Override
    protected void onMainNodeStateChanged(IGridNodeListener.State reason) {
        getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));

        super.onMainNodeStateChanged(reason);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(20, 100, !isActive() || getDisplayed() == null, true);
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
            this.lastHumanReadableValue = "-";
            return TickRateModulation.URGENT;
        }

        // Normal update schedule
        if (this.workRoutine == this.lastWorkRoutine) {
            var amountPerTick = cache.averagePerTick(this.workRoutine.timeLimit_s);
            this.lastReportedValue = amountPerTick * this.workRoutine.ticks;
            if (this.lastReportedValue > 10 || this.lastReportedValue == 0) {
                this.lastHumanReadableValue =
                        getDisplayed().formatAmount(Math.round(Math.abs(lastReportedValue)), AmountFormat.SLOT);
            } else {
                this.lastHumanReadableValue = String.format("%.2f", Math.abs(this.lastReportedValue));
            }

        } else {
            this.lastHumanReadableValue = "";
        }

        updateState(currentAmount, currentTick);
        this.getHost().markForUpdate();

        return TickRateModulation.SLOWER;
    }

    private void resetState() {
        cache.clear();
        this.lastHumanReadableValue = "";
    }

    private void updateState(long amount, long tick) {
        cache.push(amount, tick);
        this.lastWorkRoutine = this.workRoutine;
    }
}
