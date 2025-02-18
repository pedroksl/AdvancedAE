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
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEText;

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

    private long lastUpdateTick = -1;
    protected long amountAtLastUpdate = -1;
    protected long lastReportedValue = -1;
    protected String lastHumanReadableValue = "";
    private WorkRoutine workRoutine = WorkRoutine.SECOND;
    private WorkRoutine lastWorkRoutine = WorkRoutine.SECOND;

    private static final int positiveColor = AEColor.GREEN.mediumVariant;
    private static final int negativeColor = AEColor.RED.mediumVariant;

    private enum WorkRoutine {
        TICK,
        SECOND,
        MINUTE;

        public static WorkRoutine cycle(WorkRoutine routine) {
            return switch (routine) {
                case TICK -> SECOND;
                case SECOND -> MINUTE;
                case MINUTE -> TICK;
            };
        }

        public static WorkRoutine fromInt(int value) {
            return switch (value) {
                case 0 -> TICK;
                case 2 -> MINUTE;
                default -> SECOND;
            };
        }
    }

    public ThroughputMonitorPart(IPartItem<?> partItem) {
        super(partItem, false);

        getMainNode().addService(IGridTickable.class, this);
    }

    @Override
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);
        data.putLong("lastValue", this.lastReportedValue);
        data.putString("throughput", this.lastHumanReadableValue);
        data.putInt("routine", this.workRoutine.ordinal());
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.lastReportedValue = data.getLong("lastValue");
        this.lastHumanReadableValue = data.getString("throughput");
        this.workRoutine = WorkRoutine.fromInt(data.getInt("routine"));
    }

    @Override
    public void writeToStream(FriendlyByteBuf data) {
        super.writeToStream(data);

        data.writeLong(this.lastUpdateTick);
        data.writeLong(this.amountAtLastUpdate);
        data.writeLong(this.lastReportedValue);
        data.writeUtf(this.lastHumanReadableValue);
        data.writeEnum(this.workRoutine);
    }

    @Override
    public boolean readFromStream(FriendlyByteBuf data) {
        boolean needRedraw = super.readFromStream(data);

        this.lastUpdateTick = data.readLong();
        this.amountAtLastUpdate = data.readLong();

        var reportedValue = data.readLong();
        needRedraw |=
                (this.lastReportedValue > 0 && reportedValue < 0 || this.lastReportedValue < 0 && reportedValue > 0);
        this.lastReportedValue = reportedValue;

        this.lastHumanReadableValue = data.readUtf();

        this.workRoutine = data.readEnum(WorkRoutine.class);

        return needRedraw;
    }

    @Override
    public void writeVisualStateToNBT(CompoundTag data) {
        super.writeVisualStateToNBT(data);
        data.putLong("lastValue", this.lastReportedValue);
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
            updateState(TickHandler.instance().getCurrentTick());
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
                var color = lastReportedValue > 0
                        ? positiveColor
                        : lastReportedValue == 0 ? this.getColor().contrastTextColor : negativeColor;
                var text =
                        switch (this.workRoutine) {
                            case TICK -> AAEText.OverdriveThroughputMonitorValue.text(sign, lastHumanReadableValue);
                            case SECOND -> AAEText.ThroughputMonitorValue.text(sign, lastHumanReadableValue);
                            case MINUTE -> AAEText.SlowThroughputMonitorValue.text(sign, lastHumanReadableValue);
                        };
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
        return new TickingRequest(20, 400, !isActive() || getDisplayed() == null, true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode iGridNode, int i) {
        if (!this.getMainNode().isActive() || getDisplayed() == null) {
            resetState();
            return TickRateModulation.SLEEP;
        }

        long currentTick = TickHandler.instance().getCurrentTick();
        long tickAmount = currentTick - lastUpdateTick;
        var timeInSeconds = tickAmount / 20f;

        // Long time without updates, do a fast one
        if (lastUpdateTick == -1 || timeInSeconds <= 0) {
            updateState(currentTick);
            this.lastHumanReadableValue = "-";
            return TickRateModulation.URGENT;
        }

        // Normal update schedule
        if (this.workRoutine == this.lastWorkRoutine) {
            var amountPerSecond = (getAmount() - amountAtLastUpdate) / timeInSeconds;
            this.lastReportedValue = Math.round(
                    switch (this.workRoutine) {
                        case TICK -> amountPerSecond / 20f;
                        case SECOND -> amountPerSecond;
                        case MINUTE -> amountPerSecond * 60f;
                    });
            this.lastHumanReadableValue = getDisplayed().formatAmount(Math.abs(lastReportedValue), AmountFormat.SLOT);
        } else {
            this.lastHumanReadableValue = "-";
        }

        updateState(currentTick);
        this.getHost().markForUpdate();

        return TickRateModulation.SLOWER;
    }

    private void resetState() {
        this.lastUpdateTick = -1;
        this.amountAtLastUpdate = -1;
        this.lastHumanReadableValue = "";
    }

    private void updateState(long tick) {
        this.lastUpdateTick = tick;
        this.amountAtLastUpdate = getAmount();
        this.lastWorkRoutine = this.workRoutine;
    }
}
