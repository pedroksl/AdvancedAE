package net.pedroksl.advanced_ae.common.parts;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.pedroksl.advanced_ae.client.renderer.AAEBlockEntityRenderHelper;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.mixins.MixinAbstractMonitorPartAccessor;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AmountFormat;
import appeng.api.util.AEColor;
import appeng.client.render.BlockEntityRenderHelper;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.hooks.ticking.TickHandler;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractMonitorPart;
import appeng.util.InteractionUtil;

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
    private boolean overdrive = false;

    private static final int positiveColor = AEColor.GREEN.mediumVariant;
    private static final int negativeColor = AEColor.RED.mediumVariant;

    public ThroughputMonitorPart(IPartItem<?> partItem) {
        super(partItem, false);

        getMainNode().addService(IGridTickable.class, this);
    }

    @Override
    public void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);

        data.writeLong(this.lastUpdateTick);
        data.writeLong(this.amountAtLastUpdate);
        data.writeLong(this.lastReportedValue);
        data.writeUtf(this.lastHumanReadableValue);
    }

    @Override
    public boolean readFromStream(RegistryFriendlyByteBuf data) {
        boolean needRedraw = super.readFromStream(data);

        this.lastUpdateTick = data.readLong();
        this.amountAtLastUpdate = data.readLong();

        var reportedValue = data.readLong();
        needRedraw |=
                (this.lastReportedValue > 0 && reportedValue < 0 || this.lastReportedValue < 0 && reportedValue > 0);
        this.lastReportedValue = reportedValue;

        this.lastHumanReadableValue = data.readUtf();

        return needRedraw;
    }

    @Override
    public void writeVisualStateToNBT(CompoundTag data) {
        super.writeVisualStateToNBT(data);
        data.putLong("lastValue", this.lastReportedValue);
        data.putString("throughput", this.lastHumanReadableValue);
    }

    @Override
    public void readVisualStateFromNBT(CompoundTag data) {
        super.readVisualStateFromNBT(data);
        this.lastReportedValue = data.getLong("lastValue");
        this.lastHumanReadableValue = data.getString("throughput");
    }

    @Override
    public boolean onUseItemOn(ItemStack heldItem, Player player, InteractionHand hand, Vec3 pos) {
        if (heldItem == ItemStack.EMPTY) {
            return super.onUseWithoutItem(player, pos);
        } else if (heldItem.is(AEItems.SPEED_CARD.asItem())
                && !isInOverdrive()
                && InteractionUtil.isInAlternateUseMode(player)) {
            if (!isClientSide()) {
                setOverdrive(true);
            }
            return true;
        } else if (heldItem.is(Items.SLIME_BALL.asItem())
                && isInOverdrive()
                && InteractionUtil.isInAlternateUseMode(player)) {
            if (!isClientSide()) {
                setOverdrive(false);
            }
            return true;
        } else {
            return super.onUseItemOn(heldItem, player, hand, pos);
        }
    }

    public boolean isInOverdrive() {
        return this.overdrive;
    }

    private void setOverdrive(boolean value) {
        this.overdrive = value;
    }

    private @Nullable AEKey getConfiguredItem() {
        return ((MixinAbstractMonitorPartAccessor) this).getConfiguredItem();
    }

    @Override
    protected void configureWatchers() {
        if (getConfiguredItem() != null) {
            updateState();
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
            if (getConfiguredItem() != null) {
                poseStack.pushPose();
                BlockOrientation orientation = BlockOrientation.get(this.getSide(), this.getSpin());
                poseStack.translate(0.5, 0.5, 0.5);
                BlockEntityRenderHelper.rotateToFace(poseStack, orientation);
                poseStack.translate(0, 0.1, 0.5);
                BlockEntityRenderHelper.renderItem2dWithAmount(
                        poseStack,
                        buffers,
                        this.getDisplayed(),
                        ((MixinAbstractMonitorPartAccessor) this).getAmount(),
                        ((MixinAbstractMonitorPartAccessor) this).getCanCraft(),
                        0.3F,
                        -0.15F,
                        this.getColor().contrastTextColor,
                        this.getLevel());

                poseStack.translate(0, -0.23F, 0);
                var sign = lastReportedValue > 0 ? "+" : lastReportedValue == 0 ? "" : "-";
                var color = lastReportedValue > 0
                        ? positiveColor
                        : lastReportedValue == 0 ? this.getColor().contrastTextColor : negativeColor;
                var text = this.overdrive
                        ? AAEText.OverdriveThroughputMonitorValue.text(sign, lastHumanReadableValue)
                        : AAEText.ThroughputMonitorValue.text(sign, lastHumanReadableValue);
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
        return new TickingRequest(20, 100, !isActive() || getConfiguredItem() == null);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode iGridNode, int i) {
        if (!this.getMainNode().isActive() || getConfiguredItem() == null) {
            resetState();
            return TickRateModulation.SLEEP;
        }

        var currentTick = TickHandler.instance().getCurrentTick();
        var tickAmount = currentTick - lastUpdateTick;
        var timeInSeconds = tickAmount / 20;

        // Long time without updates, do a fast one
        if (lastUpdateTick == -1 || timeInSeconds <= 0) {
            updateState();
            return TickRateModulation.URGENT;
        }

        // Normal update schedule
        this.lastReportedValue = (getAmount() - amountAtLastUpdate) / timeInSeconds;
        if (this.overdrive) this.lastReportedValue /= 20;
        this.lastHumanReadableValue = getConfiguredItem().formatAmount(Math.abs(lastReportedValue), AmountFormat.SLOT);

        updateState();
        this.getHost().markForUpdate();

        return TickRateModulation.SLOWER;
    }

    private void resetState() {
        this.lastUpdateTick = -1;
        this.amountAtLastUpdate = -1;
        this.lastHumanReadableValue = "";
    }

    private void updateState() {
        this.lastUpdateTick = TickHandler.instance().getCurrentTick();
        this.amountAtLastUpdate = getAmount();
    }
}
