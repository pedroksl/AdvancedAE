package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import net.pedroksl.advanced_ae.api.IFluidTankScreen;
import net.pedroksl.advanced_ae.client.gui.widgets.*;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.gui.ReactionChamberMenu;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.*;
import appeng.core.localization.Tooltips;

public class ReactionChamberScreen extends UpgradeableScreen<ReactionChamberMenu> implements IFluidTankScreen {

    private static final int INPUT_TANK_X = 9;
    private static final int OUTPUT_TANK_X = 151;
    private static final int TANKS_Y = 21;
    private static final int TANKS_WIDTH = 16;
    private static final int TANKS_HEIGHT = 58;

    private final ProgressBar pb;
    private final SettingToggleButton<YesNo> autoExportBtn;
    private final AAEToolbarActionButton outputConfigure;
    private final AlertWidget powerAlert;

    private FluidTankSlot inputSlot;
    private FluidTankSlot outputSlot;

    public ReactionChamberScreen(
            ReactionChamberMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.pb = new ProgressBar(this.menu, style.getImage("progressBar"), ProgressBar.Direction.VERTICAL);
        widgets.add("progressBar", this.pb);

        this.autoExportBtn = new ServerSettingToggleButton<>(Settings.AUTO_EXPORT, YesNo.NO);
        this.addToLeftToolbar(autoExportBtn);

        this.outputConfigure =
                new AAEToolbarActionButton(AAEActionItems.DIRECTIONAL_OUTPUT, btn -> menu.configureOutput());
        this.outputConfigure.setVisibility(getMenu().getAutoExport() == YesNo.YES);
        this.addToLeftToolbar(this.outputConfigure);

        AAEActionButton clearBtn = new AAEActionButton(AAEActionItems.F_FLUSH, btn -> menu.clearFluid());
        clearBtn.setHalfSize(true);
        clearBtn.setDisableBackground(true);
        widgets.add("clearFluid", clearBtn);

        AAEActionButton clearOutBtn = new AAEActionButton(AAEActionItems.F_FLUSH, btn -> menu.clearFluidOut());
        clearOutBtn.setHalfSize(true);
        clearOutBtn.setDisableBackground(true);
        widgets.add("clearFluidOut", clearOutBtn);

        this.powerAlert = new AlertWidget(style.getImage("powerAlert"));
        this.powerAlert.setTooltip(Tooltip.create(Tooltips.of(
                AAEText.InsufficientPower.text().withStyle(Tooltips.RED),
                Component.literal("\n")
                        .append(AAEText.InsufficientPowerDetails.text())
                        .withStyle(Tooltips.NORMAL_TOOLTIP_TEXT))));
        this.widgets.add("powerAlert", this.powerAlert);
    }

    @Override
    protected void init() {
        this.inputSlot = this.addRenderableWidget(new FluidTankSlot(
                this,
                0,
                this.leftPos + INPUT_TANK_X,
                this.topPos + TANKS_Y,
                TANKS_WIDTH,
                TANKS_HEIGHT,
                this.menu.INPUT_FLUID_SIZE,
                Component.empty()));
        this.outputSlot = this.addRenderableWidget(new FluidTankSlot(
                this,
                1,
                this.leftPos + OUTPUT_TANK_X,
                this.topPos + TANKS_Y,
                TANKS_WIDTH,
                TANKS_HEIGHT,
                this.menu.OUTPUT_FLUID_SIZE,
                Component.empty()));

        super.init();
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        int progress = this.menu.getCurrentProgress() * 100 / this.menu.getMaxProgress();
        this.pb.setFullMsg(Component.literal(progress + "%"));

        this.autoExportBtn.set(getMenu().getAutoExport());
        this.outputConfigure.setVisibility(getMenu().getAutoExport() == YesNo.YES);

        this.inputSlot.setPosition(this.leftPos + INPUT_TANK_X, this.topPos + TANKS_Y);
        this.outputSlot.setPosition(this.leftPos + OUTPUT_TANK_X, this.topPos + TANKS_Y);

        this.powerAlert.visible = this.getMenu().getShowWarning();
    }

    public void updateFluidTankContents(FluidStack inputFluid, FluidStack outputFluid) {
        this.inputSlot.setFluidStack(inputFluid);
        this.outputSlot.setFluidStack(outputFluid);
    }

    @Override
    public void playSoundFeedback(boolean isInsert) {
        this.inputSlot.playDownSound(Minecraft.getInstance().getSoundManager(), isInsert);
    }

    private static class AlertWidget extends AbstractWidget {

        private final Blitter powerAlert;

        public AlertWidget(Blitter powerAlert) {
            super(0, 0, 18, 18, Component.empty());
            this.powerAlert = powerAlert;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
            this.powerAlert.dest(this.getX(), this.getY()).blit(guiGraphics);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    }
}
